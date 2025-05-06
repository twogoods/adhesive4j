package com.github.twogoods.adhesive.agent.plugin.springmvc;

import com.github.twogoods.adhesive.agent.plugin.springmvc.mock.MockHttpServletRequest;
import com.github.twogoods.adhesive.agent.plugin.springmvc.mock.MockHttpServletResponse;
import com.github.twogoods.adhesive.agent.spy.http.HttpRequest;
import com.github.twogoods.adhesive.agent.spy.http.HttpResponse;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/3/10
 */
public class DispatcherInvoker {
    private DispatcherServlet dispatcherServlet;
    private Method method;

    public void init() {
        try {
            method = DispatcherServlet.class.getMethod("doService", HttpServletRequest.class, HttpServletResponse.class);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse invoke(HttpRequest request) {
        //TODO header body
        MockHttpServletRequest hsr = new MockHttpServletRequest(request.method, request.uri.getPath());
        request.headers.forEach((k, list) -> list.forEach(v -> hsr.addHeader(k, v)));
        hsr.setContent(request.body);
        MockHttpServletResponse hsrsp = new MockHttpServletResponse();
        try {
            HttpServletResponse resp = (HttpServletResponse) method.invoke(dispatcherServlet, hsr, hsrsp);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
