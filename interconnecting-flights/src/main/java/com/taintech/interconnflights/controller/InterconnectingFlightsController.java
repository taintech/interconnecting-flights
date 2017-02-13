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
import org.joda.time.Hours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return buildRoutesGraph(routesServiceClient.getAvailableRoutes()).getMaxOneConnectionPaths(new Edge(departure, arrival));
    }

    @GetMapping(value = "/interconnections")
    public List<InterConnection> interconnections(
            @RequestParam(value="departure") String departure,
            @RequestParam(value="arrival") String arrival,
            @RequestParam(value="departureDateTime") String departureDateTime,
            @RequestParam(value="arrivalDateTime") String arrivalDateTime) {
        Connection request = new Connection(departure, arrival, departureDateTime, arrivalDateTime);
        List<Route> availableRoutes = routesServiceClient.getAvailableRoutes();
        RoutesGraph routesGraph = buildRoutesGraph(availableRoutes);
        List<InterConnection> directInterconnections = getDirectInterconnections(routesGraph, request);
        List<InterConnection> oneStopInterconnections = getOneStopInterconnections(routesGraph, request);
        return  Stream.concat(directInterconnections.stream(), oneStopInterconnections.stream()).collect(Collectors.toList());
    }

    private List<InterConnection> getOneStopInterconnections(RoutesGraph routesGraph,
                                                             Connection request) {
        List<Path> oneStopPaths = routesGraph.getOneConnectionPaths(new Edge(request.getDepartureAirport(), request.getArrivalAirport()));
        if(oneStopPaths.isEmpty() || oneStopPaths.get(0).getEdges().size()<2)
            return Collections.emptyList();
        else {
            List<InterConnection> allInterConnections = new ArrayList<>();
            for(Path path: oneStopPaths){
                List<InterConnection> interConnections = new ArrayList<>();
                Edge edgeOne = path.getEdges().get(0);
                List<InterConnection> edgeOneInterconnections = getEdgeInterconnectionsWithDateFix(edgeOne, request.getDeparture(), request.getArrival());
                Edge edgeTwo = path.getEdges().get(1);
                List<InterConnection> edgeTwoInterconnections = getEdgeInterconnectionsWithDateFix(edgeTwo, request.getDeparture(), request.getArrival());
                for(InterConnection interConnectionOne: edgeOneInterconnections){
                    for(Connection connOne: interConnectionOne.getLegs()){
                        for(InterConnection interConnectionTwo: edgeTwoInterconnections) {
                            for(Connection connTwo: interConnectionTwo.getLegs()) {
                                if(connOne.getArrival().isBefore(connTwo.getDeparture())
                                        && Hours.hoursBetween(connOne.getArrival(), connTwo.getDeparture()).getHours()>=2){
                                    interConnections.add(new InterConnection(1, Arrays.asList(connOne, connTwo)));
                                }
                            }
                        }
                    }
                }
                allInterConnections.addAll(interConnections);
            }
            return allInterConnections;
        }
    }

    private List<InterConnection> getDirectInterconnections(RoutesGraph routesGraph,
                                                            Connection request) {
        List<Path> directPaths = routesGraph.getDirectPaths(new Edge(request.getDepartureAirport(), request.getArrivalAirport()));
        if(directPaths.isEmpty()||directPaths.get(0).getEdges().isEmpty())
            return Collections.emptyList();
        else {
            return getEdgeInterconnectionsWithDateFix(directPaths.get(0).getEdges().get(0), request.getDeparture(), request.getArrival());
        }
    }

    private List<InterConnection> getEdgeInterconnectionsWithDateFix(Edge edge,
                                                                     DateTime departureDateTime,
                                                                     DateTime arrivalDateTime) {
        List<InterConnection> interConnections = new ArrayList<>();
        DateTime requestDate = new DateTime(departureDateTime).withDayOfMonth(1).withTimeAtStartOfDay();
        while(arrivalDateTime.isAfter(requestDate)) {
            interConnections.addAll(getEdgeInterconnections(departureDateTime, arrivalDateTime, edge, requestDate));
            requestDate = requestDate.plusMonths(1);
        }
        return interConnections;
    }

    private List<InterConnection> getEdgeInterconnections(DateTime departure, DateTime arrival, Edge edge, DateTime requestDate) {
        List<Connection> connections = getMonthConnections(requestDate.getYear(), requestDate.getMonthOfYear(), edge);
        List<InterConnection> interConnections = new ArrayList<>();
        for (Connection conn: connections){
            if (conn.getDeparture().isAfter(departure) && conn.getArrival().isBefore(arrival))
                interConnections.add(new InterConnection(0, Collections.singletonList(conn)));
        }
        return interConnections;
    }

    private List<Connection> getMonthConnections(int year, int month, Edge edge){
        Month monthSchedules = schedulesServiceClient.getSchedules(edge.getStart(), edge.getEnd(), year, month);
        List<Connection> connections = new ArrayList<>();
        if(monthSchedules.getDays()!=null) {
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
