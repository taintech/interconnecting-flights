package com.taintech.ryanair.errorhandler;

import com.taintech.ryanair.conf.SchedulesServiceClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.UnexpectedException;

/**
 * Author: Rinat Tainov
 * Date: 13/02/2017
 */
public class SimpleErrorHandler implements ResponseErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(SchedulesServiceClientConfiguration.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus status = response.getStatusCode();
        return (status == HttpStatus.BAD_GATEWAY || status == HttpStatus.GATEWAY_TIMEOUT || status == HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody()));
        String line;
        while((line = bufferedReader.readLine()) != null){
            log.error(line);
        }
        throw new UnexpectedException("Unexpected exception in RyanAir service!");
    }
}
