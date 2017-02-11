package com.taintech.interconnflights.controller;

import com.taintech.interconnflights.model.Connection;
import com.taintech.interconnflights.model.InterConnection;
import com.taintech.ryanair.client.RoutesServiceClient;
import com.taintech.ryanair.client.SchedulesServiceClient;
import com.taintech.ryanair.conf.RoutesServiceClientConfiguration;
import com.taintech.ryanair.conf.SchedulesServiceClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

    @RequestMapping("/interconnections")
    public List<InterConnection> interconnections(
            @RequestParam(value="departure") String departure,
            @RequestParam(value="arrival") String arrival,
            @RequestParam(value="departureDateTime") String departureDateTime,
            @RequestParam(value="arrivalDateTime") String arrivalDateTime) {
        Connection inputConnection = new Connection(departure, arrival, departureDateTime, arrivalDateTime);
        log.info(inputConnection.getArrivalAirport()
                + ", " + inputConnection.getDepartureAirport()
                + ", " + inputConnection.getArrivalDateTime()
                + ", " + inputConnection.getDepartureDateTime());
        List<InterConnection> interConnections = new ArrayList<>();
        Connection connection = new Connection("DUB", "WRO", "2016-03-01T12:40", "2016-03-01T16:40");
        List<Connection> legs = new ArrayList<Connection>();
        legs.add(connection);
        interConnections.add(new InterConnection(0, legs));
        Connection connection1 = new Connection("DUB", "STN", "2016-03-01T06:25", "2016-03-01T07:35");
        Connection connection2 = new Connection("STN", "DUB", "2016-03-01T09:50", "2016-03-01T13:20");
        List<Connection> legs1 = new ArrayList<Connection>();
        legs1.add(connection1);
        legs1.add(connection2);
        interConnections.add(new InterConnection(1, legs1));
        return interConnections;
    }
}
