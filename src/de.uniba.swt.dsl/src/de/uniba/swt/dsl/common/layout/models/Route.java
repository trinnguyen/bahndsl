package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Route {
    private String id;
    private String srcSignal;
    private String destSignal;
    private Stack<AbstractEdge> edges;
    private Set<String> conflictRouteIds = new HashSet<>();

    public Route(String srcSignal, String destSignal, Stack<AbstractEdge> edges) {
        this.srcSignal = srcSignal;
        this.destSignal = destSignal;
        this.edges = edges;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrcSignal() {
        return srcSignal;
    }

    public void setSrcSignal(String srcSignal) {
        this.srcSignal = srcSignal;
    }

    public String getDestSignal() {
        return destSignal;
    }

    public void setDestSignal(String destSignal) {
        this.destSignal = destSignal;
    }

    public Stack<AbstractEdge> getEdges() {
        return edges;
    }

    public void setEdges(Stack<AbstractEdge> edges) {
        this.edges = edges;
    }

    public Set<String> getConflictRouteIds() {
        return conflictRouteIds;
    }

    @Override
    public String toString() {
        var strEdge = edges.stream().map(Object::toString).collect(Collectors.joining(" -> "));
        return String.format("%s (%s - %s): %s\n\t\tconflicts: %s", id, srcSignal, destSignal, strEdge, getConflictRouteIds());
    }
}
