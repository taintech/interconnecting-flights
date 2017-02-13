package com.taintech.interconnflights.graph;

import java.util.*;

/**
 * Author: Rinat Tainov
 * Date: 12/02/2017
 */
public class RoutesGraph {
    private HashMap<String, Set<String>> map;

    public RoutesGraph() {
        map = new HashMap<String, Set<String>>();
    }

    public void connect(Edge edge) {
        if (map.containsKey(edge.getStart())) {
            Set<String> connections = map.get(edge.getStart());
            connections.add(edge.getEnd());
            map.put(edge.getStart(), connections);
        } else {
            Set<String> routes = new HashSet<>();
            routes.add(edge.getEnd());
            map.put(edge.getStart(), routes);
        }
    }

    public List<Path> directPaths(String startNode, String endNode) {
        List<Edge> directEdge = findEdges(Collections.singleton(startNode), endNode);
        return directEdge.isEmpty()?Collections.emptyList():Collections.singletonList(new Path(directEdge));
    }

    public List<Path> maxOneConnectionPaths(String startNode, String endNode) {
        List<Path> paths = new ArrayList<>();
        List<Path> directPaths = directPaths(startNode, endNode);
        paths.addAll(directPaths);
        for (Edge endEdge: findEdges(map.get(startNode), endNode)){
            Edge startEdge = new Edge(startNode, endEdge.getStart());
            paths.add(new Path(Arrays.asList(startEdge, endEdge)));
        }
        return paths;
    }

    private List<Edge> findEdges(Set<String> startNodes, String endNode){
        List<Edge> edges = new ArrayList<>();
        for (String node: startNodes) {
            if (map.get(node).contains(endNode)){
                edges.add(new Edge(node, endNode));
            }
        }
        return edges;
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
