package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.LengthUnit;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.layout.models.Route;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractPointEdge;
import de.uniba.swt.dsl.common.util.YamlExporter;

import java.util.*;
import java.util.stream.Collectors;

public class InterlockingYamlExporter extends YamlExporter {

    public String generate(Collection<Route> routes) {
        // prepare
        reset();

        // start
        appendLine("# Interlocking table");
        appendLine("interlocking-table:");
        for (var route : routes) {
            increaseLevel();
            generateRoute(route);
            decreaseLevel();
        }

        return build();
    }

    private void generateRoute(Route route) {
        // generate signals
        appendLine("- id: %s #route%s", route.getId(), route.getId());

        increaseLevel();
        appendLine("source: %s", route.getSrcSignal());
        appendLine("destination: %s", route.getDestSignal());

        // segment (blocks and points)
        double length = 0;
        LengthUnit unit = LengthUnit.METRE;
        appendLine("path:");
        increaseLevel();
        List<AbstractPointEdge> points = new ArrayList<>();
        String cmtPath = route.getEdges().stream().map(AbstractEdge::getKey).collect(Collectors.joining(" -> "));
        appendLine("# %s", cmtPath);
        for (AbstractEdge edge : route.getEdges()) {
            // cache points
            if (edge instanceof AbstractPointEdge) {
                points.add((AbstractPointEdge)edge);
            }

            // render
            for (SegmentElement segment : edge.getSegments()) {
                appendLine("- id: %s", segment.getName());
                length += segment.getLength().getValue();
                unit = segment.getLength().getUnit();
            }
        }
        decreaseLevel();

        // length
        appendLine("length: %.2f%s", length, unit.getLiteral().toLowerCase());

        // points
        appendLine("points:");
        increaseLevel();
        for (AbstractPointEdge point : points) {
            appendLine("- id: %s", point.getPointElement().getName());
            increaseLevel();
            appendLine("position: %s", point.formatAspect());
            decreaseLevel();
        }
        decreaseLevel();

        // flank signals
        appendLine("signals:");

        // conflicts
        appendLine("conflicts:");
        increaseLevel();
        for (String conflictRouteId : route.getConflictRouteIds()) {
            appendLine("- id: %s", conflictRouteId);
        }
        decreaseLevel();

        // decrease obj level
        decreaseLevel();
    }
}
