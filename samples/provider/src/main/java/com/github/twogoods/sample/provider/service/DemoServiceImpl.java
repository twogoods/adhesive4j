package com.github.twogoods.sample.provider.service;

import com.github.twogoods.iface.BizException;
import com.github.twogoods.iface.DemoService;
import com.github.twogoods.iface.User;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author twogoods
 * @since 2024/9/11
 */
@DubboService
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(User user) {
        return user.getName() + " " + user.getAge();
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
