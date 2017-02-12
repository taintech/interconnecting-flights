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
        return adaptMonth(2017, 6, new Edge("WRO", "WAW"),schedulesServiceClient.getSchedules("WRO", "WAW", 2017, 6)).toString();
    }

    @GetMapping("/explore")
    public String exploreTimeShift() {
        List<Connection> suspects = new ArrayList<>();
        for (Route route: routesServiceClient.getAvailableRoutes()) {
            Month month = schedulesServiceClient.getSchedules(route.getAirportFrom(), route.getAirportTo(), 2017, 3);
            List<Connection> connections = adaptMonth(2017, 3, convertRoute(route), month);
            for(Connection suspect: connections) {
                if (suspect.getDepartureDateTime().getHourOfDay()==23){
                    suspects.add(suspect);
                }
            }
        }
        return suspects.toString();
    }

    @GetMapping(value = "/interconnections")
    public List<Path> interconnections(
            @RequestParam(value="departure") String departure,
            @RequestParam(value="arrival") String arrival,
            @RequestParam(value="departureDateTime") String departureDateTime,
            @RequestParam(value="arrivalDateTime") String arrivalDateTime) {
        List<Route> availableRoutes = routesServiceClient.getAvailableRoutes();
        RoutesGraph routesGraph = new RoutesGraph();
        for (Route route: availableRoutes){
            routesGraph.connect(new Edge(route.getAirportFrom(), route.getAirportTo()));
        }
        List<Path> paths = routesGraph.maxOneConnectionPaths(departure, arrival);
        DateTimeFormatter patternFormat = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm")
                .toFormatter();
        DateTime departureDT = patternFormat.parseDateTime(departureDateTime);
        DateTime arrivalDT = patternFormat.parseDateTime(arrivalDateTime);
        log.info("depDT: " + departureDT.toString());
        log.info("arrDT: " + arrivalDT.toString());
//        schedulesServiceClient.getSchedules()


        return paths;
    }

    private InterConnection getInterConnection(Path path, DateTime departure, DateTime arrival) {
        return null;
    }

    private List<Flight> getFlights(Edge edge, DateTime departure, DateTime arrival){
        schedulesServiceClient.getSchedules(edge.getStart(), edge.getEnd(), departure.getYear(), departure.getMonthOfYear());
        return null;
    }

    private List<Connection> adaptMonth(int year, int month, Edge edge, Month monthSchedules){
        List<Connection> connections = new ArrayList<>();
        for (Day day: monthSchedules.getDays()) {
            for (Flight flight: day.getFlights()) {
                int departureHour = Integer.parseInt((flight.getArrivalTime().split(":"))[0]);
                int departureMinute = Integer.parseInt((flight.getArrivalTime().split(":"))[1]);
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

    private Edge convertRoute(Route route) {
        return new Edge(route.getAirportFrom(), route.getAirportFrom());
    }
}
