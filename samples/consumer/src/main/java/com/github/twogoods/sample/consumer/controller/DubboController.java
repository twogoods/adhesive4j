package com.github.twogoods.sample.consumer.controller;

import com.github.twogoods.iface.DemoService;
import com.github.twogoods.iface.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author twogoods
 * @since 2024/9/11
 */
@ConditionalOnProperty(value = "demo.dubbo.enable", havingValue = "true")
@RestController
@RequestMapping("/dubbo")
public class DubboController {

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

    @RequestMapping(value = "/dubbo2")
    public String dubbo2() {
        User user = demoService.renew(new User("test", 1));
        return user.toString();
    }

    @RequestMapping(value = "/dubbo3")
    public String dubbo3() throws Throwable {
        return demoService.error(new User("test", 1));
    }

    @RequestMapping(value = "/rest")
    public String rest() {
        return demoService.sayHello(new User("test", 1));
    }
}
