package com.github.twogoods.adhesive.agent.spy.http;

/**
 * @author twogoods
 * @since 2024/11/22
 */
public interface HttpAdhesiveInvoker {

    void update(Object dubbo);

    HttpResponse invoke(ClassLoader callerCl, ClassLoader providerCl, HttpRequest request) throws Throwable;

    Object convertRestResponse(HttpResponse resp);

}
