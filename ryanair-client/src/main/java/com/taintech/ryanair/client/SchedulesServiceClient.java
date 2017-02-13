package com.taintech.ryanair.client;

import com.taintech.ryanair.model.Month;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
@Service
public class SchedulesServiceClient {

    private String rootUrl;
    private final RestTemplate restTemplate;

    public SchedulesServiceClient(String rootUrl, RestTemplate restTemplate) {
        this.rootUrl = rootUrl;
        this.restTemplate = restTemplate;
    }

    public Month getSchedules(String airportFrom, String airportTo, int year, int month) {
        return restTemplate.getForObject(
                rootUrl + "/{from}/{to}/years/{year}/months/{month}",
                Month.class,
                airportFrom,
                airportTo,
                year,
                month);
    }
}
