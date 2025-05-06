package com.github.twogoods.adhesive.agent.plugin.rest;

import com.github.twogoods.adhesive.agent.plugin.springmvc.mock.MockHttpServletRequest;
import com.github.twogoods.adhesive.agent.plugin.springmvc.mock.MockHttpServletResponse;
import com.github.twogoods.adhesive.agent.spy.http.HttpAdhesiveInvoker;
import com.github.twogoods.adhesive.agent.spy.http.HttpRequest;
import com.github.twogoods.adhesive.agent.spy.http.HttpResponse;
import com.github.twogoods.adhesive.agent.spy.http.HttpSpy;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/3/10
 */
public class HttpLocalInvoker implements HttpAdhesiveInvoker {

    DispatcherServlet dispatcherServlet;

    Method method;

    public static void regist(ClassLoader classLoader) {
//        HttpLocalInvoker invoker = new HttpLocalInvoker();
//        invoker.init();
        HttpSpy.registerInvoker(classLoader, new HttpLocalInvoker());
    }

    public void update(Object dispatcherServlet) {
        if (dispatcherServlet instanceof DispatcherServlet) {
            this.dispatcherServlet = (DispatcherServlet) dispatcherServlet;
        }
    }

    public void initReflectCall() {
        if (method != null) {
            return;
        }
        try {
            method = DispatcherServlet.class.getDeclaredMethod("doService", HttpServletRequest.class, HttpServletResponse.class);
            method.setAccessible(true);
            dispatcherServlet.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public HttpResponse invoke(ClassLoader callerCl, ClassLoader providerCl, HttpRequest request) throws Throwable {
        initReflectCall();
        MockHttpServletRequest hsr = new MockHttpServletRequest(request.method, request.uri.getPath());
        request.headers.forEach((k, list) -> list.forEach(v -> hsr.addHeader(k, v)));
        hsr.setContent(request.body);
        MockHttpServletResponse hsrsp = new MockHttpServletResponse();
        try {
            method.invoke(dispatcherServlet, hsr, hsrsp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, List<String>> respHeaders = new HashMap<>();
        for (String name : hsrsp.getHeaderNames()) {
            respHeaders.put(name, hsrsp.getHeaders(name));
        }
        HttpResponse resp = new HttpResponse(respHeaders, hsrsp.getStatus(), hsrsp.getContentAsByteArray());
        return resp;
    }

    @Override
    public Object convertRestResponse(HttpResponse resp) {
        HttpHeaders headers = new HttpHeaders();
        resp.headers.forEach(headers::addAll);
        return new AdhesiveSpringHttpResponse(resp.code, "", headers, resp.body);
    }
}
