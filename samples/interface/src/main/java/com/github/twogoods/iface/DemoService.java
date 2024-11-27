package com.github.twogoods.iface;

/**
 * @author twogoods
 * @since 2024/9/11
 */
public interface DemoService {
    String sayHello(User user);

    User renew(User user);

    String error(User user) throws Throwable;
}
