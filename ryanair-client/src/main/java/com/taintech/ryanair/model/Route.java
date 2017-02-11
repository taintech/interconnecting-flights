package com.taintech.ryanair.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Author: Rinat Tainov
 * Date: 11/02/2017
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Route {

    private String airportFrom;
    private String airportTo;

    public String getAirportFrom() {
        return airportFrom;
    }

    public void setAirportFrom(String airportFrom) {
        this.airportFrom = airportFrom;
    }

    public String getAirportTo() {
        return airportTo;
    }

    public void setAirportTo(String airportTo) {
        this.airportTo = airportTo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        return (airportFrom != null ? airportFrom.equals(route.airportFrom) : route.airportFrom == null)
                && (airportTo != null ? airportTo.equals(route.airportTo) : route.airportTo == null);
    }

    @Override
    public int hashCode() {
        int result = airportFrom != null ? airportFrom.hashCode() : 0;
        result = 31 * result + (airportTo != null ? airportTo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Route{" +
                "airportFrom='" + airportFrom + '\'' +
                ", airportTo='" + airportTo + '\'' +
                '}';
    }
}