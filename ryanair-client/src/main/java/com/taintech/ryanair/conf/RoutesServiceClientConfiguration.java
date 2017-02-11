package com.taintech.ryanair.conf;

import com.taintech.ryanair.props.RoutesServiceProperties;
import com.taintech.ryanair.client.RoutesServiceClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: Rinat Tainov
 * Date: 11/02/2017
 */
@Configuration
@EnableConfigurationProperties(RoutesServiceProperties.class)
public class RoutesServiceClientConfiguration {

    @Bean
    public RoutesServiceClient routesService(RoutesServiceProperties properties,
                                             RestTemplateBuilder restTemplateBuilder){
        return new RoutesServiceClient(properties.getUrl(), restTemplateBuilder);
    }
}
