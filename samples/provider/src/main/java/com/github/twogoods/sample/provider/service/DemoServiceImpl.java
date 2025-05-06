package com.github.twogoods.sample.provider.service;

import com.github.twogoods.iface.BizException;
import com.github.twogoods.iface.DemoService;
import com.github.twogoods.iface.User;

/**
 * @author twogoods
 * @since 2024/9/11
 */
public class DemoServiceImpl implements DemoService {

    private String version;

    public DemoServiceImpl(String version) {
        this.version = version;
    }

    @Override
    public String sayHello(User user) {
        return "[" + version + "] " + user.getName() + " " + user.getAge();
    }

    @Override
    public User renew(User user) {
        return new User("new " + user.getName(), user.getAge() + 1);
    }

    @Override
    public String error(User user) throws Throwable {
        throw new BizException(user.getName());
    }
}
