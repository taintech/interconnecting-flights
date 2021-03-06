package com.taintech.ryanair.conf;

import com.taintech.ryanair.client.SchedulesServiceClient;
import com.taintech.ryanair.errorhandler.SimpleErrorHandler;
import com.taintech.ryanair.props.SchedulesServiceClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
@Configuration
@EnableConfigurationProperties(SchedulesServiceClientProperties.class)
public class SchedulesServiceClientConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new SimpleErrorHandler());
        return restTemplate;
    }

    @Bean
    public SchedulesServiceClient schedulesServiceClient(
            SchedulesServiceClientProperties schedulesServiceClientProperties,
            RestTemplate restTemplate) {
        return new SchedulesServiceClient(schedulesServiceClientProperties.getUrl(), restTemplate);
    }
}
