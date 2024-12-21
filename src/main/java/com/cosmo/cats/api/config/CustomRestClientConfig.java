package com.cosmo.cats.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class CustomRestClientConfig {

  @Value("${application.rest-client.response-timeout}")
  private int responseTimeout;

  private ClientHttpRequestFactory createRequestFactory() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setReadTimeout(responseTimeout);
    factory.setConnectTimeout(responseTimeout);
    return factory;
  }

  @Bean(name = "customRestClient")
  public RestClient customRestClient() {
    return RestClient.builder()
            .requestFactory(createRequestFactory())
            .build();
  }
}