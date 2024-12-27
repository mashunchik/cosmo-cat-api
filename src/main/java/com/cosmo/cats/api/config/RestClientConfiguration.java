package com.cosmo.cats.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean("advisorClient")
    public RestClient advisorClient(@Value("${application.price-advisor-service.base-path}") String basePath) {
        return RestClient.builder()
                .baseUrl(basePath)
                .build();
    }
}
