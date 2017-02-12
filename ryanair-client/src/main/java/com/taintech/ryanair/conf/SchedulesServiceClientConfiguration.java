package com.taintech.ryanair.conf;

import com.taintech.ryanair.client.SchedulesServiceClient;
import com.taintech.ryanair.props.SchedulesServiceClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Author: Rinat Tainov
 * Date: 11/02/2017
 */
@Configuration
@EnableConfigurationProperties(SchedulesServiceClientProperties.class)
public class SchedulesServiceClientConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus status = response.getStatusCode();
                return (status == HttpStatus.BAD_GATEWAY || status == HttpStatus.GATEWAY_TIMEOUT || status == HttpStatus.INTERNAL_SERVER_ERROR);
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody()));

            }
        });
        return restTemplate;
    }

    @Bean
    public SchedulesServiceClient schedulesServiceClient(
            SchedulesServiceClientProperties schedulesServiceClientProperties,
            RestTemplate restTemplate) {
        return new SchedulesServiceClient(schedulesServiceClientProperties.getUrl(), restTemplate);
    }
}
