package com.github.twogoods.sample.provider.controller;

import com.github.twogoods.iface.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.AdhesiveServlet;

/**
 * @author twogoods
 * @since 2024/9/11
 */
@RestController
public class EchoController {

    @Autowired
    private AdhesiveServlet adhesiveServlet;

    @RequestMapping(value = "/echo")
    public String echo(String name) {
        return "provider " + name;
    }

    @PostMapping(value = "/post")
    public User post(@RequestBody User user, @RequestHeader HttpHeaders headers) {
        System.out.println(user);
        return new User(headers.toString(), 0);
    }

    @RequestMapping(value = "/call")
    public String call() {
        try {
            String resp = adhesiveServlet.test();
            return resp;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
