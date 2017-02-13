package com.taintech.ryanair.model;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
public class Flight {
    private int number;
    private String departureTime;
    private String arrivalTime;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flight flight = (Flight) o;

        return number == flight.number
                && (departureTime != null ? departureTime.equals(flight.departureTime) : flight.departureTime == null)
                && (arrivalTime != null ? arrivalTime.equals(flight.arrivalTime) : flight.arrivalTime == null);
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + (departureTime != null ? departureTime.hashCode() : 0);
        result = 31 * result + (arrivalTime != null ? arrivalTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "number=" + number +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                '}';
    }
}
