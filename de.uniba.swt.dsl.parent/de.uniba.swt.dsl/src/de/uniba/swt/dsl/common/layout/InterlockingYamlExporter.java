package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.common.generator.sccharts.builder.TextualBuilder;
import de.uniba.swt.dsl.common.layout.models.Route;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.graph.BlockEdge;
import de.uniba.swt.dsl.common.layout.models.graph.SwitchEdge;

import java.util.*;
import java.util.stream.Collectors;

public class InterlockingYamlExporter {

    private static final String SPACE = "  ";
    private List<String> items = new LinkedList<>();
    private int indentLevel;

    public String generate(Collection<Route> routes) {
        // prepare
        prepare();

        // start
        appendLine("# Interlocking table");
        appendLine("interlocking-table:");
        for (var route : routes) {
            generateRoute(route);
        }

        return String.join("\n", items);
    }

    private void generateRoute(Route route) {

        // prepare
        List<SwitchEdge> points = new ArrayList<>();
        List<BlockEdge> blocks = new ArrayList<>();
        for (AbstractEdge edge : route.getEdges()) {
            if (edge.getEdgeType() == AbstractEdge.EdgeType.Switch) {
                points.add((SwitchEdge)edge);
                continue;
            }

            if (edge.getEdgeType() == AbstractEdge.EdgeType.Block) {
                blocks.add((BlockEdge)edge);
            }
        }

        // generate
        indentLevel = 1;
        appendLine("- id: %s", route.getId());

        indentLevel++;
        appendLine("source: %s", route.getSrcSignal());
        appendLine("destination: %s", route.getDestSignal());

        // segment
        appendLine("path:");
        indentLevel++;
        for (int i = 0; i < blocks.size(); i++) {
            var block = blocks.get(i);
            if (i == blocks.size() - 1) {
                appendLine(SPACE+ "end: %s", block.getBlockElement().getName());
            } else {
                appendLine("- id: %s", block.getBlockElement().getName());
            }
        }
        indentLevel--;

        // points
        appendLine("points:");
        indentLevel++;
        for (SwitchEdge point : points) {
            appendLine("- id: %s", point.getBlockElement().getName());
            indentLevel++;
            appendLine("position: %s", point.getAspect().toString().toLowerCase());
            indentLevel--;
        }
        indentLevel--;

        // flank signals
        appendLine("signals:");

        // conflicts
        appendLine("conflicts:");
        indentLevel++;
        for (String conflictRouteId : route.getConflictRouteIds()) {
            appendLine("- id: %s", conflictRouteId);
        }
    }

    private void prepare() {
        items.clear();
        indentLevel = 0;
    }

    private void appendLine(String text, Object... args) {
        items.add(SPACE.repeat(Math.max(0, indentLevel)) + String.format(text, args));
    }
}
