package com.github.twogoods.sample.consumer;

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author twogoods
 * @since 2024/9/11
 */
@SpringBootApplication
public class ConsumerApp {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    public static void main(String[] args) {
        TomcatURLStreamHandlerFactory.disable();
        SpringApplication.run(ConsumerApp.class, args);
    }
}
