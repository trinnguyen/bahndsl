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

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import de.uniba.swt.dsl.common.layout.models.edge.*;
import de.uniba.swt.dsl.common.layout.models.graph.LayoutGraph;
import de.uniba.swt.dsl.common.layout.models.vertex.*;
import de.uniba.swt.dsl.common.util.LogHelper;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class NetworkLayout implements LayoutGraph {
    private final static Logger logger = Logger.getLogger(NetworkLayout.class);

    private final List<LayoutVertex> vertices = new ArrayList<>();
    private final Map<String, LayoutVertex> mapVertices = new TreeMap<>();
    private final Map<String, BlockDirection> blockDirectionMap = new TreeMap<>();

    public NetworkLayout() {
    }

    public void clear() {
        vertices.clear();
        mapVertices.clear();
    }

    public List<LayoutVertex> getVertices() {
        return vertices;
    }

    public void addMembersToVertex(List<AbstractVertexMember> members, LayoutVertex vertex) {
        members.forEach(member -> {
            if (vertex.addIfNeeded(member)) {
                mapVertices.put(member.getKey(), vertex);
            }
        });
    }

    public LayoutVertex addNewVertex() {
        var vertex = new LayoutVertex();
        vertices.add(vertex);
        return vertex;
    }

    public LayoutVertex findVertex(AbstractVertexMember member) {
        var key = member.getKey();
        if (mapVertices.containsKey(key)) {
            return mapVertices.get(key);
        }

        return null;
    }

    public LayoutVertex findVertex(String memberKey) {
        if (mapVertices.containsKey(memberKey)) {
            return mapVertices.get(memberKey);
        }

        return null;
    }

    public boolean hasVertex(String memberKey) {
        return mapVertices.containsKey(memberKey);
    }

    @Override
    public String toString() {
        return "NetworkLayout {" + "\n" +
                "vertices=" + LogHelper.printObject(vertices) + "\n" +
                '}';
    }

    @Override
    public Set<LayoutVertex> adjacentVertices(LayoutVertex vertex) {
        return incidentEdges(vertex).stream().map(AbstractEdge::getDestVertex).collect(Collectors.toSet());
    }

    @Override
    public Set<AbstractEdge> incidentEdges(LayoutVertex vertex) {
        return vertex.getMembers()
                .stream()
                .filter(member -> member.getType() != VertexMemberType.Signal)
                .map(member -> findEdges(vertex, member))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public ValueGraph<LayoutVertex, AbstractEdge> generateGraph() {
        MutableValueGraph<LayoutVertex, AbstractEdge> graph = ValueGraphBuilder
                .undirected()
                .allowsSelfLoops(false)
                .build();

        Set<String> visitedEdges = new HashSet<>();
        Set<String> visitedVertices = new HashSet<>();
        for (LayoutVertex vertex : getVertices()) {
            dfsGraph(graph, visitedVertices, visitedEdges, vertex);
        }
        return graph;
    }

    private void dfsGraph(MutableValueGraph<LayoutVertex, AbstractEdge> graph, Set<String> visitedVertices, Set<String> visitedEdges, LayoutVertex vertex) {
        if (visitedVertices.contains(vertex.getId()))
            return;

        visitedVertices.add(vertex.getId());
        for (AbstractEdge edge : incidentEdges(vertex)) {
            // check if visited
            if (visitedEdges.contains(edge.getKey()))
                continue;
            visitedEdges.add(edge.getKey());

            // put
            graph.putEdgeValue(edge.getSrcVertex(), edge.getDestVertex(), edge);

            // go deep
            dfsGraph(graph, visitedVertices, visitedEdges, edge.getDestVertex());
        }
    }

    private Set<AbstractEdge> findEdges(LayoutVertex vertex, AbstractVertexMember member) {
        // segment block: platform, block
        if (member.isSegmentBlock()) {
            var blockMember = (BlockVertexMember) member;
            var newEndpoint = blockMember.getEndpoint() == BlockVertexMember.BlockEndpoint.Up ?
                    BlockVertexMember.BlockEndpoint.Down :
                    BlockVertexMember.BlockEndpoint.Up;
            var dest = findVertex(blockMember.generateKey(newEndpoint));
            return Set.of(new BlockEdge(blockMember.getBlock(), vertex, dest));
        }

        // point: switch
        if (member.getType() == VertexMemberType.StandardSwitch) {
            var switchMember = (StandardSwitchVertexMember) member;
            var connectedEndpoints = switchMember.getConnectedEndpoints();
            return connectedEndpoints.stream()
                    .map(e -> {
                        var v = findVertex(switchMember.generateKey(e));
                        return new StandardSwitchEdge(switchMember.getPointElement(),
                                getSwitchAspect(switchMember.getEndpoint(), e),
                                vertex,
                                v);
                    })
                    .collect(Collectors.toSet());
        }

        // point: doubleSlipSwitch
        if (member.getType() == VertexMemberType.DoubleSlipSwitch) {
            var doubleSlipSwitchMember = (DoubleSlipSwitchVertexMember) member;
            var connectedEndpoints = doubleSlipSwitchMember.getConnectedEndpoints();
            return connectedEndpoints.stream()
                    .map(e -> {
                        var v = findVertex(doubleSlipSwitchMember.generateKey(e));
                        return new DoubleSlipSwitchEdge(doubleSlipSwitchMember.getPointElement(),
                                getDoubleSlipSwitchAspect(doubleSlipSwitchMember.getEndpoint(), e),
                                vertex,
                                v);
                    })
                    .collect(Collectors.toSet());
        }

        // crossing
        if (member.getType() == VertexMemberType.Crossing) {
            var crossingMem = (CrossingVertexMember) member;
            var v = findVertex(crossingMem.generateKey(crossingMem.getConnectedEndpoint()));
            return Set.of(new CrossingEdge(crossingMem.getCrossing(),
                    getCrossingAspect(crossingMem.getEndpoint()),
                    vertex,
                    v));
        }

        throw new RuntimeException("Member is not supported for edge: " + member);
    }

    private CrossingEdge.Aspect getCrossingAspect(CrossingVertexMember.Endpoint src) {
        switch (src) {
            case Down1:
            case Up2:
                return CrossingEdge.Aspect.Line1;
            default:
                return CrossingEdge.Aspect.Line2;
        }
    }

    private DoubleSlipSwitchEdge.Aspect getDoubleSlipSwitchAspect(DoubleSlipSwitchVertexMember.Endpoint src, DoubleSlipSwitchVertexMember.Endpoint dst) {
        switch (src) {
            case Down1:
                if (dst == DoubleSlipSwitchVertexMember.Endpoint.Up2)
                    return DoubleSlipSwitchEdge.Aspect.Normal1;
                if (dst == DoubleSlipSwitchVertexMember.Endpoint.Up1)
                    return DoubleSlipSwitchEdge.Aspect.Reverse1;
            case Down2:
                if (dst == DoubleSlipSwitchVertexMember.Endpoint.Up1)
                    return DoubleSlipSwitchEdge.Aspect.Normal2;
                if (dst == DoubleSlipSwitchVertexMember.Endpoint.Up2)
                    return DoubleSlipSwitchEdge.Aspect.Reverse2;
            case Up1:
                if (dst == DoubleSlipSwitchVertexMember.Endpoint.Down2)
                    return DoubleSlipSwitchEdge.Aspect.Normal2;
                if (dst == DoubleSlipSwitchVertexMember.Endpoint.Down1)
                    return DoubleSlipSwitchEdge.Aspect.Reverse1;
            case Up2:
                if (dst == DoubleSlipSwitchVertexMember.Endpoint.Down1)
                    return DoubleSlipSwitchEdge.Aspect.Normal1;
                if (dst == DoubleSlipSwitchVertexMember.Endpoint.Down2)
                    return DoubleSlipSwitchEdge.Aspect.Reverse2;
        }

        throw new RuntimeException("Invalid doubleSlipSwitch aspect connector: " + src + " -- " + dst);
    }

    private StandardSwitchEdge.Aspect getSwitchAspect(StandardSwitchVertexMember.Endpoint srcEndpoint, StandardSwitchVertexMember.Endpoint destEndPoint) {
        var set = Set.of(srcEndpoint, destEndPoint);
        if (set.contains(StandardSwitchVertexMember.Endpoint.Stem)) {
            if (set.contains(StandardSwitchVertexMember.Endpoint.Normal)) {
                return StandardSwitchEdge.Aspect.Normal;
            }

            return StandardSwitchEdge.Aspect.Reverse;
        }

        return null;
    }

    public void addMissingBlockVertices() {
        Set<LayoutVertex> set = new HashSet<>();
        for (var vertex : this.getVertices()) {
            for (var member : vertex.getMembers()) {
                if (member.isSegmentBlock()) {
                    var blockVertexMember = (BlockVertexMember) member;
                    var endpoint = blockVertexMember.getEndpoint() == BlockVertexMember.BlockEndpoint.Up ?
                            BlockVertexMember.BlockEndpoint.Down :
                            BlockVertexMember.BlockEndpoint.Up;

                    if (!hasVertex(blockVertexMember.generateKey(endpoint))) {
                        var newVertex = new LayoutVertex();
                        set.add(newVertex);

                        this.addMembersToVertex(List.of(blockVertexMember.generateMember(endpoint)), newVertex);
                    }
                }
            }
        }
        this.getVertices().addAll(set);
    }

    public BlockDirection getBlockDirection(String name) {
        return blockDirectionMap.getOrDefault(name, BlockDirection.Bidirectional);
    }

    public void setBlockDirection(String name, BlockDirection direction) {
        blockDirectionMap.put(name, direction);
    }

    public boolean hasEdge() {
        return getVertices().size() >= 2;
    }
}
