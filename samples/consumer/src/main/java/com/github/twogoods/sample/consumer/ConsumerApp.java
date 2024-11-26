package com.github.twogoods.sample.consumer;

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author twogoods
 * @since 2024/9/11
 */
@SpringBootApplication
@EnableDubbo(scanBasePackages = {"com.github.twogoods.sample.consumer"})
public class ConsumerApp {
    public static void main(String[] args) {
        TomcatURLStreamHandlerFactory.disable();
        SpringApplication.run(ConsumerApp.class, args);
    }
}
