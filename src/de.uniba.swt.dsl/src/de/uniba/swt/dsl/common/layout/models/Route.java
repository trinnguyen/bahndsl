package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.edge.BlockEdge;
import de.uniba.swt.dsl.common.layout.models.vertex.SignalVertexMember;

import java.util.*;
import java.util.stream.Collectors;

public class Route {
    private String id;
    private String srcSignal;
    private String destSignal;
    private Stack<AbstractEdge> edges;
    private Set<String> conflictRouteIds = new HashSet<>();
    private final List<String> immediateSignals;


    public Route(String srcSignal, String destSignal, Stack<AbstractEdge> edges, List<String> immediateSignals) {
        this.srcSignal = srcSignal;
        this.destSignal = destSignal;
        this.edges = edges;
        this.immediateSignals = immediateSignals;
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

    public List<String> getImmediateSignals() {
        return immediateSignals;
    }

    @Override
    public String toString() {
        var strEdge = edges.stream().map(Object::toString).collect(Collectors.joining(" -> "));
        return String.format("%s (%s - %s): %s\n" +
                "\t\tconflicts: %s\n" +
                "\t\timmediate signals: %s", id, srcSignal, destSignal, strEdge, getConflictRouteIds(), getImmediateSignals());
    }
}
