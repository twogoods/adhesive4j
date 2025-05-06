package com.github.twogoods.adhesive.agent.plugin.rest;

import com.github.twogoods.adhesive.agent.spy.http.HttpRequest;
import com.github.twogoods.adhesive.agent.spy.http.HttpResponse;
import com.github.twogoods.adhesive.agent.spy.http.HttpSpy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/3/10
 */
public class ClientHttpRequestAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean doInvoke(@Advice.Local(value = "_IS_SKIP") Boolean isSkip) {
        System.out.println("ClientHttpRequest skip execute。。。");
        isSkip = true;
        return isSkip;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void exit(@Advice.This() ClientHttpRequest request,
                            @Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) ClientHttpResponse result) {
        System.out.println("ClientHttpRequest execute。。。");
        Map<String, List<String>> header = new HashMap<>();
        header.putAll(request.getHeaders());

        ClientHttpResponse resp = null;
        ClassLoader callerCl = request.getClass().getClassLoader();
        System.out.println("ClientHttpRequest cl " + callerCl.getClass().getName());
        try {
            HttpRequest httpRequest = new HttpRequest(request.getURI(), request.getMethod().name(), header, request.getBody().toString().getBytes());
            HttpResponse response = HttpSpy.invoke(callerCl, httpRequest);
//            resp = new AdhesiveSpringHttpResponse(response.code, "", null, response.body);
            resp = (ClientHttpResponse) HttpSpy.convertRestResponse(callerCl, response);
        } catch (Throwable e) {
            e.printStackTrace();
//            resp = new AdhesiveSpringHttpResponse(500, "", null, e.getMessage().getBytes());
            resp = (ClientHttpResponse) HttpSpy.convertRestResponse(callerCl, new HttpResponse(500, e.getMessage().getBytes()));
        }
        result = resp;
    }
}
