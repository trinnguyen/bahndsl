package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;

import java.util.Stack;
import java.util.stream.Collectors;

public class Route {
    private String srcSignal;
    private String destSignal;
    private Stack<AbstractEdge> edges;

    public Route(String srcSignal, String destSignal, Stack<AbstractEdge> edges) {
        this.srcSignal = srcSignal;
        this.destSignal = destSignal;
        this.edges = edges;
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

    @Override
    public String toString() {
        var strEdge = edges.stream().map(Object::toString).collect(Collectors.joining(" -> "));
        return String.format("%s - %s: %s", srcSignal, destSignal, strEdge);
    }
}
