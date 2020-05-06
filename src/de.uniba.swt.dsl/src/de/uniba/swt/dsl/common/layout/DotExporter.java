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

package de.uniba.swt.dsl.common.layout;

import com.google.common.graph.ValueGraph;
import de.uniba.swt.dsl.common.layout.models.*;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.edge.BlockEdge;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;
import de.uniba.swt.dsl.common.layout.models.edge.StandardSwitchEdge;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.VertexMemberType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DotExporter {
    public String render(NetworkLayout networkLayout, ValueGraph<LayoutVertex, AbstractEdge> graph) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph G {").append("\n").append("rankdir=LR").append("\n");

        builder.append("\n");

        // nodes
        for (var node : graph.nodes()) {
            builder.append(generateNodeId(node)).append(" ");
            node.getMembers()
                    .stream()
                    .filter(m -> m.getType() == VertexMemberType.Signal)
                    .findFirst()
                    .ifPresentOrElse(member -> {
                        // signal
                        builder.append("[label=\"").append(member.getKey()).append("\", shape=proteinstab, color=red]");
            }, () -> {
                        builder.append("[label=\"\", shape=point]");
            });

            builder.append("\n");
        }

        builder.append("\n");

        StringBuilder directedBuilder = new StringBuilder();
        StringBuilder undirectedBuilder = new StringBuilder();
        // edges
        for (var edge : graph.edges()) {
            AbstractEdge edgeValue = graph.edgeValue(edge).get();

            var srcNode = edgeValue.getSrcVertex();
            var desNode = edgeValue.getDestVertex();
            boolean isDirected = false;

            // build directed if needed
            if (edgeValue instanceof BlockEdge) {
                var blockEdge = (BlockEdge) edgeValue;
                var direction = networkLayout.getBlockDirection(blockEdge.getBlockElement().getName());

                // swap
                if (direction != BlockDirection.Bidirectional) {
                    if (blockEdge.getDirection() != direction) {
                        srcNode = edgeValue.getDestVertex();
                        desNode = edgeValue.getSrcVertex();
                    }
                    isDirected = true;
                }
            }

            // build str
            String msg = String.format("\t%s -> %s [label=\"%s\", color=%s]\n",
                    generateNodeId(srcNode),
                    generateNodeId(desNode),
                    edgeValue.toString(),
                    getColor(edgeValue));

            // undirected
            if (isDirected) {
                directedBuilder.append(msg);
            } else {
                undirectedBuilder.append(msg);
            }
        }

        // build directed
        if (directedBuilder.length() > 0) {
            builder.append("subgraph grdirected {\n");
            builder.append(directedBuilder);
            builder.append("}\n");
        }

        // build undirected
        builder.append("subgraph undirected {\n");
        builder.append("\tedge [dir=none]\n");
        builder.append(undirectedBuilder);
        builder.append("}\n");

        builder.append("}");
        return builder.toString();
    }

    private String getColor(AbstractEdge edgeValue) {
        if (edgeValue.getEdgeType() == AbstractEdge.EdgeType.SingleSwitch) {
            return getPointColor(((StandardSwitchEdge)edgeValue).getPointElement().getName());
        }

        return "black";
    }

    Map<String, String> mapColors = new HashMap<>();
    private int lastInt = 0;
    String[] colors = new String[] {"orange", "orange4", "orangered", "orchid", "magenta1", "lightpink3", "purple3"};
    private String getPointColor(String name) {
        if (mapColors.containsKey(name)) {
            return mapColors.get(name);
        } else {
            String color = colors[lastInt++ % colors.length];
            mapColors.put(name, color);
            return color;
        }
    }

    private String generateNodeId(LayoutVertex vertex) {
        return "\"" +
                vertex.getMembers().stream().map(AbstractVertexMember::getKey).collect(Collectors.joining("-")) +
                "\"";
    }
}
