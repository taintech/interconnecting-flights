package com.taintech.interconnflights.controller;

import com.taintech.interconnflights.graph.Edge;
import com.taintech.interconnflights.graph.Path;
import com.taintech.interconnflights.model.InterConnection;
import com.taintech.interconnflights.graph.RoutesGraph;
import com.taintech.ryanair.client.RoutesServiceClient;
import com.taintech.ryanair.client.SchedulesServiceClient;
import com.taintech.ryanair.conf.RoutesServiceClientConfiguration;
import com.taintech.ryanair.conf.SchedulesServiceClientConfiguration;
import com.taintech.ryanair.model.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: Rinat Tainov
 * Date: 11/02/2017
 */
@Import({RoutesServiceClientConfiguration.class, SchedulesServiceClientConfiguration.class})
@RestController
public class InterconnectingFlightsController {

    private static final Logger log = LoggerFactory.getLogger(InterconnectingFlightsController.class);

    private RoutesServiceClient routesServiceClient;
    private SchedulesServiceClient schedulesServiceClient;

    @Autowired
    public InterconnectingFlightsController(RoutesServiceClient routesServiceClient, SchedulesServiceClient schedulesServiceClient) {
        this.routesServiceClient = routesServiceClient;
        this.schedulesServiceClient = schedulesServiceClient;
    }

    @GetMapping("/")
    public String home() {
        return routesServiceClient.getAvailableRoutes().toString();
    }

    @GetMapping("/wrowaw")
    public String fligths() {
        return schedulesServiceClient.getSchedules("WRO", "WAW", 2017, 6).toString();
    }

    @GetMapping(value = "/interconnections")
    public List<InterConnection> interconnections(
            @RequestParam(value="departure") String departure,
            @RequestParam(value="arrival") String arrival,
            @RequestParam(value="departureDateTime") String departureDateTime,
            @RequestParam(value="arrivalDateTime") String arrivalDateTime) {
        List<Route> availableRoutes = routesServiceClient.getAvailableRoutes();
        RoutesGraph routesGraph = new RoutesGraph();
        for (Route route: availableRoutes){
            routesGraph.connect(new Edge(route.getAirportFrom(), route.getAirportFrom()));
        }
        List<Path> paths = routesGraph.possiblePaths(departure, arrival, 1);

        return null;
    }
}
