/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author liwenqiang
 */
@SpringBootApplication
public class AbeilleGatewayApplication {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    public static void main(String[] args) {
        SpringApplication.run(AbeilleGatewayApplication.class, args);
    }

}

