package com.taintech.interconnflights.controller;

import com.taintech.interconnflights.graph.Edge;
import com.taintech.interconnflights.graph.Path;
import com.taintech.interconnflights.graph.RoutesGraph;
import com.taintech.interconnflights.model.Connection;
import com.taintech.interconnflights.model.InterConnection;
import com.taintech.ryanair.client.RoutesServiceClient;
import com.taintech.ryanair.client.SchedulesServiceClient;
import com.taintech.ryanair.conf.RoutesServiceClientConfiguration;
import com.taintech.ryanair.conf.SchedulesServiceClientConfiguration;
import com.taintech.ryanair.model.Day;
import com.taintech.ryanair.model.Flight;
import com.taintech.ryanair.model.Month;
import com.taintech.ryanair.model.Route;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
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

    @GetMapping(value = "/paths")
    public List<Path> paths(
            @RequestParam(value="departure") String departure,
            @RequestParam(value="arrival") String arrival) {
        return buildRoutesGraph(routesServiceClient.getAvailableRoutes()).maxOneConnectionPaths(departure, arrival);
    }

    @GetMapping(value = "/interconnections")
    public List<InterConnection> interconnections(
            @RequestParam(value="departure") String departure,//TODO refactor request params into model
            @RequestParam(value="arrival") String arrival,
            @RequestParam(value="departureDateTime") String departureDateTime,
            @RequestParam(value="arrivalDateTime") String arrivalDateTime) {
        List<Route> availableRoutes = routesServiceClient.getAvailableRoutes();
        RoutesGraph routesGraph = buildRoutesGraph(availableRoutes);
        //TODO move out
        DateTimeFormatter patternFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm")
                .toFormatter();
        DateTime departureDT = patternFormat.parseDateTime(departureDateTime);
        DateTime arrivalDT = patternFormat.parseDateTime(arrivalDateTime);
        List<InterConnection> directInterconnections = getDirectInterconnections(routesGraph, departure, arrival, departureDT, arrivalDT);

        return directInterconnections;
    }

    private List<InterConnection> getOneStopInterconnections(RoutesGraph routesGraph,
                                                             String departure,
                                                             String arrival,
                                                             DateTime departureDT,
                                                             DateTime arrivalDT) {
        return null;
    }

    private List<InterConnection> getDirectInterconnections(RoutesGraph routesGraph,
                                                            String departure,
                                                            String arrival,
                                                            DateTime departureDT,
                                                            DateTime arrivalDT) {
        List<Path> pathList = routesGraph.directPaths(departure, arrival);
        if(pathList.isEmpty())
            return Collections.emptyList();
        else {
            Path directPath = pathList.get(0);
            List<InterConnection> interConnections = new ArrayList<>();
            while(arrivalDT.isAfter(departureDT)) {
                interConnections.addAll(getDirectPathInterconnections(departureDT, arrivalDT, directPath));
                departureDT = departureDT.plusMonths(1);
            }
            return interConnections;
        }
    }

    private List<InterConnection> getDirectPathInterconnections(DateTime departure, DateTime arrival, Path directPath) {
        Edge edge = directPath.getEdges().get(0);
        List<Connection> connections = getMonthConnections(departure.getYear(), departure.getMonthOfYear(), edge);
        List<InterConnection> interConnections = new ArrayList<>();
        for (Connection conn: connections){
            if (conn.getArrival().isBefore(arrival))
                interConnections.add(new InterConnection(0, Collections.singletonList(conn)));
        }
        return interConnections;
    }

    private List<Connection> getMonthConnections(int year, int month, Edge edge){
        Month monthSchedules = schedulesServiceClient.getSchedules(edge.getStart(), edge.getEnd(), year, month);
        List<Connection> connections = new ArrayList<>();
        for (Day day: monthSchedules.getDays()) {
            for (Flight flight: day.getFlights()) {
                //TODO needs adapter
                int departureHour = Integer.parseInt((flight.getDepartureTime().split(":"))[0]);
                int departureMinute = Integer.parseInt((flight.getDepartureTime().split(":"))[1]);
                int arrivalHour = Integer.parseInt((flight.getArrivalTime().split(":"))[0]);
                int arrivalMinute = Integer.parseInt((flight.getArrivalTime().split(":"))[1]);
                Connection conn = new Connection(edge.getStart(),
                        edge.getEnd(),
                        new DateTime(year, month, day.getDay(), departureHour, departureMinute),
                        new DateTime(year, month, day.getDay(), arrivalHour, arrivalMinute));
                connections.add(conn);
            }
        }
        return connections;
    }

    private RoutesGraph buildRoutesGraph(List<Route> availableRoutes) {
        RoutesGraph routesGraph = new RoutesGraph();
        for (Route route: availableRoutes){
            routesGraph.connect(new Edge(route.getAirportFrom(), route.getAirportTo()));
        }
        return routesGraph;
    }
}
