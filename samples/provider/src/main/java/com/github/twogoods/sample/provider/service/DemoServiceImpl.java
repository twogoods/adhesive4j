package com.github.twogoods.sample.provider.service;

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
}
