package com.taintech.ryanair.model;

import java.util.List;

/**
 * Author: Rinat Tainov
 * Date: 11/02/2017
 */
public class Day {
    private int day;
    private List<Flight> flights;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Day day1 = (Day) o;

        return day == day1.day && (flights != null ? flights.equals(day1.flights) : day1.flights == null);
    }

    @Override
    public int hashCode() {
        int result = day;
        result = 31 * result + (flights != null ? flights.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Day{" +
                "day=" + day +
                ", flights=" + flights +
                '}';
    }
}
