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

import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractPointEdge;
import de.uniba.swt.dsl.common.layout.models.edge.BlockEdge;
import de.uniba.swt.dsl.common.layout.models.vertex.SignalVertexMember;

import java.util.*;
import java.util.stream.Collectors;

public class Route {
    private int id;
    private String srcSignal;
    private String destSignal;
    private Stack<AbstractEdge> edges;
    private Orientation startingOrientation;    // Orientation that a train needs to be in at the start of the route
    private boolean[] hasConflicts;

    public Route(String srcSignal, String destSignal, Stack<AbstractEdge> edges, Orientation startingOrientation) {
        this.srcSignal = srcSignal;
        this.destSignal = destSignal;
        this.edges = edges;
        this.startingOrientation = startingOrientation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public List<SegmentElement> getSegments() {
        return getEdges().stream()
                .map(AbstractEdge::getSegments)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<AbstractPointEdge> getPoints() {
        return getEdges().stream()
                .filter(AbstractPointEdge.class::isInstance)
                .map(AbstractPointEdge.class::cast)
                .collect(Collectors.toList());
    }

    public List<BlockEdge> getBlocks() {
        return getEdges().stream()
                .filter(BlockEdge.class::isInstance)
                .map(BlockEdge.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Returns the key names of all signals along the route,
     * including the source and destination signals.
     */
    public List<String> getSignals() {
        List<String> signals = new ArrayList<>();
        signals.add(getSrcSignal());

        List<BlockEdge> blockEdges = getEdges().stream().filter(BlockEdge.class::isInstance)
                .map(BlockEdge.class::cast).collect(Collectors.toList());

        for (BlockEdge blockEdge : blockEdges) {
            blockEdge.getDestVertex().getMembers().stream()
                    .filter(SignalVertexMember.class::isInstance)
                    .findFirst().ifPresent(signal -> signals.add(signal.getKey()));
        }

        return signals;
    }

    /**
     * Returns the segments and signals along the route.
     * Source and destination signals are excluded.
     * Objects are of type AbstractEdge or SignalVertexMember
     */
    public List<Object> getOrderedSegmentsAndSignals() {
        List<Object> segmentsAndSignals = new ArrayList<>();

        for (AbstractEdge edge : getEdges()) {
            segmentsAndSignals.addAll(edge.getSegments());

            if (edge instanceof BlockEdge) {
                // Get the signal that is at the end of the block
                var blockEdge = (BlockEdge) edge;
                blockEdge.getDestVertex().getMembers().stream()
                        .filter(SignalVertexMember.class::isInstance)
                        .findFirst().ifPresent(segmentsAndSignals::add);
            }
        }

        // Remove destination signal
        if (segmentsAndSignals.get(segmentsAndSignals.size() - 1) instanceof SignalVertexMember) {
            segmentsAndSignals.remove(segmentsAndSignals.size() - 1);
        }

        return segmentsAndSignals;
    }

    public Orientation getStartingOrientation() { return startingOrientation; }

    public void setStartingOrientation(Orientation startingOrientation) { this.startingOrientation = startingOrientation; }

    public void setHasConflicts(boolean[] newHasConflictWithRoutes) {
        hasConflicts = new boolean[newHasConflictWithRoutes.length];
        System.arraycopy(newHasConflictWithRoutes, 0, hasConflicts, 0, newHasConflictWithRoutes.length);
    }

    public List<Integer> getConflictRouteIds() {
        List<Integer> conflictRouteIds = new ArrayList<>();
        for (var i = 0; i < hasConflicts.length; ++i) {
            if (hasConflicts[i]) {
                // List index is the id of the conflicting route.
                conflictRouteIds.add(i);
            }
        }
        return conflictRouteIds;
    }

    @Override
    public String toString() {
        var strEdge = edges.stream().map(Object::toString).collect(Collectors.joining(" -> "));
        return String.format("%d (%s - %s): %s\n" +
                "\t\t%d conflicts\n", id, srcSignal, destSignal, strEdge, hasConflicts.length);
    }
}
