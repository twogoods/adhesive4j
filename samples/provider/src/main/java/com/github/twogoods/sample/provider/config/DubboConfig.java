package com.github.twogoods.sample.provider.config;

import com.github.twogoods.iface.DemoService;
import com.github.twogoods.sample.provider.service.DemoServiceImpl;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luhaoshuai@bytedance.com
 * @since 2025/3/10
 */
@ConditionalOnProperty(value = "demo.dubbo.enable", havingValue = "true")
@Configuration
@EnableDubbo(scanBasePackages = {"com.github.twogoods.sample.provider"})
public class DubboConfig {

    @Value("${version:default}")
    private String version;


    @Value("${dubbo.application.name}")
    private String serviceName;

    @Value("${dubbo.registry.address:zookeeper://127.0.0.1:2181}")
    private String address;

    @Value("${dubbo.protocol.port:8888}")
    private int port;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(serviceName);
        applicationConfig.setQosEnable(false);
        return applicationConfig;
    }


    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(port);
        return protocolConfig;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(address);
        registryConfig.setId("zk-registry");
        return registryConfig;
    }



    @Bean
    @DubboService
    public DemoService aService() {
        return new DemoServiceImpl(version);
    }
}
