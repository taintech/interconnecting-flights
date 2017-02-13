package com.taintech.interconnflights.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
public class Connection {

    private static final String ISO_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm";
    private static final DateTimeFormatter PATTERN_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern(ISO_DATE_PATTERN)
            .toFormatter();

    private String departureAirport;
    private String arrivalAirport;
    private String departureDateTime;
    private String arrivalDateTime;
    @JsonIgnore
    private DateTime departure;
    @JsonIgnore
    private DateTime arrival;

    public Connection(String departureAirport, String arrivalAirport, String departureDateTime, String arrivalDateTime) {
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        this.departure = PATTERN_FORMATTER.parseDateTime(departureDateTime);
        this.arrival = PATTERN_FORMATTER.parseDateTime(arrivalDateTime);
    }

    public Connection(String departureAirport, String arrivalAirport, DateTime departure, DateTime arrival) {
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departure = departure;
        this.arrival = arrival;
        this.departureDateTime = departure.toString(PATTERN_FORMATTER);
        this.arrivalDateTime = arrival.toString(PATTERN_FORMATTER);
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
