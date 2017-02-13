package com.taintech.ryanair.conf;

import com.taintech.ryanair.props.RoutesServiceClientProperties;
import com.taintech.ryanair.client.RoutesServiceClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
@Configuration
@EnableConfigurationProperties(RoutesServiceClientProperties.class)
public class RoutesServiceClientConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RoutesServiceClient routesServiceClient(RoutesServiceClientProperties properties, RestTemplate restTemplate) {
        return new RoutesServiceClient(properties.getUrl(), restTemplate);
    }
}
