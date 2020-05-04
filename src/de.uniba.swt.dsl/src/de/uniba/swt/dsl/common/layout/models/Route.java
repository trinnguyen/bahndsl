/*
 *
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

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
    private final Set<String> conflictRouteIds = new HashSet<>();
    private List<String> immediateSignals;

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

    public List<String> getImmediateSignals() {
        return immediateSignals;
    }

    private String formatImmediateSignals() {
        return String.format("\t\timmediate signals: %s", getImmediateSignals());
    }

    @Override
    public String toString() {
        var strEdge = edges.stream().map(Object::toString).collect(Collectors.joining(" -> "));
        return String.format("%s (%s - %s): %s\n" +
                "\t\tconflicts: %s\n", id, srcSignal, destSignal, strEdge, getConflictRouteIds());
    }
}
