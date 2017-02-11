package com.taintech.interconnflights.graph;

import java.util.List;

/**
 * Author: Rinat Tainov
 * Date: 11/02/2017
 */
public class Path {
    List<Edge> edges;

    public Path(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        return edges != null ? edges.equals(path.edges) : path.edges == null;
    }

    @Override
    public int hashCode() {
        return edges != null ? edges.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Path{" +
                "edges=" + edges +
                '}';
    }
}
