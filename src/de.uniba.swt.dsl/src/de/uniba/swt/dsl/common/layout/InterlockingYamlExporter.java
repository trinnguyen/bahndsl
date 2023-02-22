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

import de.uniba.swt.dsl.bahn.LengthUnit;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.bahn.SignalElement;
import de.uniba.swt.dsl.common.layout.models.Route;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractPointEdge;
import de.uniba.swt.dsl.common.layout.models.edge.BlockEdge;
import de.uniba.swt.dsl.common.layout.models.vertex.SignalVertexMember;
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
        // Route ID
        appendLine("- id: %d #route%s", route.getId(), route.getId());

        // Source and destination signals, and route orientation
        increaseLevel();
        appendLine("source: %s", route.getSrcSignal());
        appendLine("destination: %s", route.getDestSignal());
        appendLine("orientation: %s", route.getStartingOrientation().toString().toLowerCase());

        // Route path (segments and signals)
        appendLine("path:");
        increaseLevel();
        String pathDescription = route.getEdges().stream().map(AbstractEdge::getKey).collect(Collectors.joining(" -> "));
        appendLine("# %s", pathDescription);

        double length = 0;
        LengthUnit unit = LengthUnit.METRE;
        List<Object> segmentsAndSignals = route.getOrderedSegmentsAndSignals();
        for (Object item : segmentsAndSignals) {
            if (item instanceof SegmentElement) {
                SegmentElement segment = (SegmentElement) item;
                appendLine("- id: %s", segment.getName());

                length += segment.getLength().getValue();
                unit = segment.getLength().getUnit();
            } else if (item instanceof SignalVertexMember) {
                SignalVertexMember signal = (SignalVertexMember) item;
                appendLine("- id: %s", signal.getName());
            }
        }

        decreaseLevel();

        // Block sections
        appendLine("sections:");
        increaseLevel();
        route.getBlocks().stream().map(BlockEdge::getKey).forEach(blockId -> appendLine("- id: %s", blockId));
        decreaseLevel();

        // Route length
        appendLine("length: %.2f%s", length, unit.getLiteral().toLowerCase());

        // Signals
        appendLine("signals:");
        increaseLevel();
        route.getSignals().stream().forEach(signal -> appendLine("- id: %s", signal));
        decreaseLevel();

        // Point aspects
        appendLine("points:");
        increaseLevel();
        List<AbstractPointEdge> points = route.getPoints();
        for (AbstractPointEdge point : points) {
            appendLine("- id: %s", point.getPointElement().getName());
            increaseLevel();
            appendLine("position: %s", point.formatAspect());
            decreaseLevel();
        }
        decreaseLevel();

        // Conflicting routes
        appendLine("conflicts:");
        increaseLevel();
        route.getConflictRouteIds().forEach(conflict -> appendLine("- id: %d", conflict));
        decreaseLevel();

        // decrease obj level
        decreaseLevel();
    }
}
