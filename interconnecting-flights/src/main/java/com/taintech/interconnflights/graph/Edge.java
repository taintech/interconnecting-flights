package com.taintech.interconnflights.graph;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
public class Edge {
    private String start;
    private String end;

    public Edge(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return (start != null ? start.equals(edge.start) : edge.start == null)
                && (end != null ? end.equals(edge.end) : edge.end == null);
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
