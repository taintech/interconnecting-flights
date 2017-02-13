package com.taintech.interconnflights.model;

import java.util.List;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
public class InterConnection {
    private int stops;
    private List<Connection> legs;

    public InterConnection(int stops, List<Connection> legs) {
        this.stops = stops;
        this.legs = legs;
    }

    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
    }

    public List<Connection> getLegs() {
        return legs;
    }

    public void setLegs(List<Connection> legs) {
        this.legs = legs;
    }
}
