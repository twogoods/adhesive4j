package com.github.twogoods.sample.consumer.controller;

import com.github.twogoods.iface.DemoService;
import com.github.twogoods.iface.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author twogoods
 * @since 2024/9/11
 */
@RestController
public class IndexController {

    @DubboReference
    DemoService demoService;

    @RequestMapping(value = "/http")
    public String http() {
        return "hi ...";
    }

    @RequestMapping(value = "/dubbo")
    public String dubbo() {
        return demoService.sayHello(new User("test", 1));
    }
}
