package com.taintech.interconnflights.controller;

import com.taintech.interconnflights.model.Connection;
import com.taintech.interconnflights.model.InterConnection;
import com.taintech.interconnflights.model.graph.Path;
import com.taintech.interconnflights.service.InterconnectionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */

@RestController
public class InterconnectingFlightsController {

    private static final Logger log = LoggerFactory.getLogger(InterconnectingFlightsController.class);

    private InterconnectionsService interconnectionsService;

    @Autowired
    public InterconnectingFlightsController(InterconnectionsService interconnectionsService) {
        this.interconnectionsService = interconnectionsService;
    }

    @GetMapping(value = "/paths")
    public List<Path> paths(
            @RequestParam(value = "departure") String departure,
            @RequestParam(value = "arrival") String arrival) {
        return interconnectionsService.getConnectionPaths(departure, arrival);
    }

    @GetMapping(value = "/interconnections")
    public List<InterConnection> interconnections(
            @RequestParam(value = "departure") String departure,
            @RequestParam(value = "arrival") String arrival,
            @RequestParam(value = "departureDateTime") String departureDateTime,
            @RequestParam(value = "arrivalDateTime") String arrivalDateTime) {
        Connection request = new Connection(departure, arrival, departureDateTime, arrivalDateTime);
        return interconnectionsService.getInterconnections(request);
    }
}
