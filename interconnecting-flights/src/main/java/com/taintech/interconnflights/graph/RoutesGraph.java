package com.taintech.interconnflights.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Rinat Tainov
 * Date: 11/02/2017
 */
public class RoutesGraph {
    private HashMap<String, Set<String>> map;

    public RoutesGraph() {
        map = new HashMap<String, Set<String>>();
    }

    public void connect(Edge edge) {
        if (map.containsKey(edge.getStart())) {
            map.get(edge.getStart()).add(edge.getEnd());
        } else {
            Set<String> routes = new HashSet<>();
            routes.add(edge.getEnd());
            map.put(edge.getStart(), routes);
        }
    }

    public List<Path> possiblePaths(String startNode, String endNode, int maxDistance) {
        //TODO
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoutesGraph that = (RoutesGraph) o;

        return map != null ? map.equals(that.map) : that.map == null;
    }

    @Override
    public int hashCode() {
        return map != null ? map.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RoutesGraph{" +
                "map=" + map +
                '}';
    }
}
