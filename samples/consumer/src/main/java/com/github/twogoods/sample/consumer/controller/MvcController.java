package com.github.twogoods.sample.consumer.controller;

import com.github.twogoods.iface.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/3/10
 */
@RestController
@RequestMapping("/")
public class MvcController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    @ResponseBody
    public String echo(@RequestParam(value = "name", defaultValue = "adhesive") String name) {
        return "consumer " + name;
    }

    @RequestMapping(value = "/call", method = RequestMethod.GET)
    @ResponseBody
    public String call() {
        ResponseEntity<User> responseEntity;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("k1", "v1");
        User user = new User("a", 12);
        HttpEntity<User> request = new HttpEntity<>(user, requestHeaders);
        responseEntity = restTemplate.exchange("http://localhost:8088/post", HttpMethod.POST, request, User.class);
        if (responseEntity.getStatusCode().value() == 200) {
            User res = responseEntity.getBody() == null ? null : responseEntity.getBody();
            return res.toString();
        } else {
            return responseEntity.getStatusCode().toString();
        }
    }
}
