package com.taintech.ryanair.model;

import java.util.List;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
public class Month {
    private int month;
    private List<Day> days;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Month month1 = (Month) o;

        return month == month1.month && (days != null ? days.equals(month1.days) : month1.days == null);
    }

    @Override
    public int hashCode() {
        int result = month;
        result = 31 * result + (days != null ? days.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Month{" +
                "month=" + month +
                ", days=" + days +
                '}';
    }
}
