package com.github.twogoods.sample.provider;

import com.github.twogoods.iface.User;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.protocol.dubbo.DubboProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.AdhesiveServlet;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.HashMap;

/**
 * @author twogoods
 * @since 2024/9/11
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProviderApp {

    public static void call() {
        DubboProtocol dubboProtocol = DubboProtocol.getDubboProtocol();
        dubboProtocol.getExporters().forEach(exporter -> {
            Invoker invoker = exporter.getInvoker();
            if (invoker.getUrl().getServiceInterface().equals("com.github.twogoods.iface.DemoService")) {
                Invocation invocation = new RpcInvocation(null, "sayHello", "com.github.twogoods.iface.DemoService",
                        null, new Class[]{User.class}, new Object[]{new User("haha", 1212)}, new HashMap<>(), invoker, new HashMap<>(), null);
                Result res = (AsyncRpcResult) invoker.invoke(invocation);
                if (res.getException() != null) {
                    res.getException().printStackTrace();
                } else {
                    System.out.println(res.getValue());
                }
            }
        });
    }

    @Bean
    public AdhesiveServlet adhesiveServlet(DispatcherServlet dispatcherServlet) {
        return new AdhesiveServlet(dispatcherServlet);
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ProviderApp.class, args);
//        Thread.sleep(5000);
//        call();
    }
}
