package de.uniba.swt.dsl.common.layout;

import com.google.common.graph.ValueGraph;
import de.uniba.swt.dsl.common.layout.models.SignalVertexMember;
import de.uniba.swt.dsl.common.layout.models.VertexMember;
import de.uniba.swt.dsl.common.layout.models.VertexMemberType;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.graph.LayoutVertex;
import de.uniba.swt.dsl.common.layout.models.graph.SwitchEdge;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DotExporter {
    public String render(String name, ValueGraph<LayoutVertex, AbstractEdge> graph) {
        StringBuilder builder = new StringBuilder();
        builder.append("graph G {").append("\n").append("rankdir=LR").append("\n");

        builder.append("\n");

        // nodes
        for (var node : graph.nodes()) {
            builder.append(generateNodeId(node)).append(" ");
            node.getMembers()
                    .stream()
                    .filter(m -> m.getType() == VertexMemberType.Signal)
                    .findFirst()
                    .ifPresentOrElse(member -> {
                        builder.append("[label=\"").append(member.getKey()).append("\", shape=proteinstab, color=red]");
            }, () -> {
                        builder.append("[label=\"\", shape=point]");
            });

            builder.append("\n");
        }

        builder.append("\n");

        // edges
        for (var edge : graph.edges()) {
            var edgeValue = graph.edgeValue(edge).get();
            builder.append(String.format("%s -- %s [label=\"%s\", color=%s];",
                    generateNodeId(edge.nodeU()),
                    generateNodeId(edge.nodeV()),
                    edgeValue.toString(),
                    getColor(edgeValue)));
            builder.append("\n");
        }

        builder.append("}");
        return builder.toString();
    }

    private String getColor(AbstractEdge edgeValue) {
        if (edgeValue.getEdgeType() == AbstractEdge.EdgeType.Switch) {
            return getPointColor(((SwitchEdge)edgeValue).getPointElement().getName());
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
                vertex.getMembers().stream().map(VertexMember::getKey).collect(Collectors.joining("-")) +
                "\"";
    }
}
