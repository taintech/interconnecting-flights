package com.taintech.interconnflights.service;

import com.taintech.interconnflights.model.Connection;
import com.taintech.interconnflights.model.InterConnection;
import com.taintech.interconnflights.model.graph.Edge;
import com.taintech.interconnflights.model.graph.Path;
import com.taintech.interconnflights.model.graph.RoutesGraph;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: Rinat Tainov
 * Date: 13/02/2017
 */

@Import({RoutesServiceClientConfiguration.class, SchedulesServiceClientConfiguration.class})
@Service
public class InterconnectionsService {

    private RoutesServiceClient routesServiceClient;
    private SchedulesServiceClient schedulesServiceClient;

    @Autowired
    public InterconnectionsService(RoutesServiceClient routesServiceClient, SchedulesServiceClient schedulesServiceClient) {
        this.routesServiceClient = routesServiceClient;
        this.schedulesServiceClient = schedulesServiceClient;
    }

    public List<Path> getConnectionPaths(String departure, String arrival) {
        return buildRoutesGraph(routesServiceClient.getAvailableRoutes())
                .getMaxOneConnectionPaths(new Edge(departure, arrival));
    }

    public List<InterConnection> getInterconnections(Connection request) {
        List<Route> availableRoutes = routesServiceClient.getAvailableRoutes();
        RoutesGraph routesGraph = buildRoutesGraph(availableRoutes);
        List<InterConnection> directInterconnections = getDirectInterconnections(routesGraph, request);
        List<InterConnection> oneStopInterconnections = getOneStopInterconnections(routesGraph, request);
        return Stream.concat(directInterconnections.stream(), oneStopInterconnections.stream()).collect(Collectors.toList());
    }

    private List<InterConnection> getOneStopInterconnections(RoutesGraph routesGraph,
                                                             Connection request) {
        List<Path> oneStopPaths = routesGraph.getOneConnectionPaths(new Edge(request.getDepartureAirport(), request.getArrivalAirport()));
        if (oneStopPaths.isEmpty() || oneStopPaths.get(0).getEdges().size() < 2)
            return Collections.emptyList();
        else {
            List<InterConnection> allInterConnections = new ArrayList<>();
            for (Path path : oneStopPaths) {
                Edge edgeOne = path.getEdges().get(0);
                List<InterConnection> edgeOneInterconnections = getEdgeInterconnectionsWithDateFix(edgeOne, request.getDeparture(), request.getArrival());
                Edge edgeTwo = path.getEdges().get(1);
                List<InterConnection> edgeTwoInterconnections = getEdgeInterconnectionsWithDateFix(edgeTwo, request.getDeparture(), request.getArrival());
                allInterConnections.addAll(joinInterconnections(edgeOneInterconnections, edgeTwoInterconnections));
            }
            return allInterConnections;
        }
    }

    private List<InterConnection> joinInterconnections(List<InterConnection> edgeOneInterconnections, List<InterConnection> edgeTwoInterconnections) {
        List<InterConnection> interConnections = new ArrayList<>();
        for (InterConnection interConnectionOne : edgeOneInterconnections) {
            for (Connection connOne : interConnectionOne.getLegs()) {
                for (InterConnection interConnectionTwo : edgeTwoInterconnections) {
                    for (Connection connTwo : interConnectionTwo.getLegs()) {
                        if (connOne.getArrival().isBefore(connTwo.getDeparture())
                                && Hours.hoursBetween(connOne.getArrival(), connTwo.getDeparture()).getHours() >= 2) {
                            interConnections.add(new InterConnection(1, Arrays.asList(connOne, connTwo)));
                        }
                    }
                }
            }
        }
        return interConnections;
    }

    private List<InterConnection> getDirectInterconnections(RoutesGraph routesGraph,
                                                            Connection request) {
        List<Path> directPaths = routesGraph.getDirectPaths(new Edge(request.getDepartureAirport(), request.getArrivalAirport()));
        if (directPaths.isEmpty() || directPaths.get(0).getEdges().isEmpty())
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
        while (arrivalDateTime.isAfter(requestDate)) {
            interConnections.addAll(getEdgeInterconnections(departureDateTime, arrivalDateTime, edge, requestDate));
            requestDate = requestDate.plusMonths(1);
        }
        return interConnections;
    }

    private List<InterConnection> getEdgeInterconnections(DateTime departure, DateTime arrival, Edge edge, DateTime requestDate) {
        List<Connection> connections = requestMonthConnections(requestDate, edge);
        List<InterConnection> interConnections = new ArrayList<>();
        for (Connection conn : connections) {
            if (conn.getDeparture().isAfter(departure) && conn.getArrival().isBefore(arrival))
                interConnections.add(new InterConnection(0, Collections.singletonList(conn)));
        }
        return interConnections;
    }

    private List<Connection> requestMonthConnections(DateTime requestDate, Edge edge) {
        Month monthSchedules = schedulesServiceClient.getSchedules(edge.getStart(), edge.getEnd(), requestDate.getYear(), requestDate.getMonthOfYear());
        return convertMonthToConnections(monthSchedules, requestDate, edge);
    }

    private List<Connection> convertMonthToConnections(Month monthSchedules, DateTime requestDate, Edge edge) {
        List<Connection> connections = new ArrayList<>();
        if (monthSchedules.getDays() != null) {
            for (Day day : monthSchedules.getDays()) {
                for (Flight flight : day.getFlights()) {
                    Connection conn = new Connection(
                            edge.getStart(),
                            edge.getEnd(),
                            String.format("%s-%02dT%s", requestDate.toString("yyyy-MM"), day.getDay(), flight.getDepartureTime()),
                            String.format("%s-%02dT%s", requestDate.toString("yyyy-MM"), day.getDay(), flight.getArrivalTime()));
                    connections.add(conn);
                }
            }
        }
        return connections;
    }

    private RoutesGraph buildRoutesGraph(List<Route> availableRoutes) {
        RoutesGraph routesGraph = new RoutesGraph();
        for (Route route : availableRoutes) {
            routesGraph.connect(new Edge(route.getAirportFrom(), route.getAirportTo()));
        }
        return routesGraph;
    }
}
