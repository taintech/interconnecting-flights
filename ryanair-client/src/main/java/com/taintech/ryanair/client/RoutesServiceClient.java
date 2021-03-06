package com.taintech.ryanair.client;

import com.taintech.ryanair.model.Route;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
@Service
public class RoutesServiceClient {

    private String url;
    private final RestTemplate restTemplate;

    public RoutesServiceClient(String url, RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public List<Route> getAvailableRoutes() {
        ResponseEntity<List<Route>> routeResponse =
                restTemplate.exchange(url,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Route>>() {
                        });
        return routeResponse.getBody();
    }
}
