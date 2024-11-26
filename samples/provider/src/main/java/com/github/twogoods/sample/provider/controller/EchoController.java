package com.github.twogoods.sample.provider.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author twogoods
 * @since 2024/9/11
 */
@RestController
public class EchoController {
    @RequestMapping(value = "/echo")
    public String echo(String name) {
        return "hi " + name;
    }
}
