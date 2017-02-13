package com.taintech.interconnflights.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.DateTime;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
public class Connection {
    private String departureAirport;
    private String arrivalAirport;
    private String departureDateTime;
    private String arrivalDateTime;
    @JsonIgnore
    private DateTime departure;
    @JsonIgnore
    private DateTime arrival;

    public Connection(String departureAirport, String arrivalAirport, DateTime departure, DateTime arrival) {
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departure = departure;
        this.arrival = arrival;
        //TODO reuse pattern form controller
        this.departureDateTime = departure.toString("yyyy-MM-dd'T'HH:mm");
        this.arrivalDateTime = arrival.toString("yyyy-MM-dd'T'HH:mm");
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public String getDepartureDateTime() {
        return departureDateTime;
    }

    public String getArrivalDateTime() {
        return arrivalDateTime;
    }

    public DateTime getDeparture() {
        return departure;
    }

    public DateTime getArrival() {
        return arrival;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "departureAirport='" + departureAirport + '\'' +
                ", arrivalAirport='" + arrivalAirport + '\'' +
                ", departureDateTime='" + departureDateTime + '\'' +
                ", arrivalDateTime='" + arrivalDateTime + '\'' +
                ", departure=" + departure +
                ", arrival=" + arrival +
                '}';
    }
}
