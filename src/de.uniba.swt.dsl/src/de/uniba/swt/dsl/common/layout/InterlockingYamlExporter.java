package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.common.layout.models.Route;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.edge.StandardSwitchEdge;

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


        // generate signals
        indentLevel = 1;
        appendLine("- id: %s #route%s", route.getId(), route.getId());

        indentLevel++;
        appendLine("source: %s", route.getSrcSignal());
        appendLine("destination: %s", route.getDestSignal());

        // segment (blocks and points)
        appendLine("path:");
        indentLevel++;
        List<StandardSwitchEdge> points = new ArrayList<>();
        String cmtPath = route.getEdges().stream().map(AbstractEdge::getKey).collect(Collectors.joining(" -> "));
        appendLine("# %s", cmtPath);
        for (AbstractEdge edge : route.getEdges()) {
            // cache points
            if (edge.getEdgeType() == AbstractEdge.EdgeType.SingleSwitch) {
                points.add((StandardSwitchEdge)edge);
            }

            // render
            edge.getSegments().forEach(segmentElement -> {
                appendLine("- id: %s", segmentElement.getName());
            });
        }
        indentLevel--;

        // points
        appendLine("points:");
        indentLevel++;
        for (StandardSwitchEdge point : points) {
            appendLine("- id: %s", point.getPointElement().getName());
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
