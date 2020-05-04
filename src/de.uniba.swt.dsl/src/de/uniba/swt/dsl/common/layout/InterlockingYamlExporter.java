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
import de.uniba.swt.dsl.common.layout.models.Route;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractEdge;
import de.uniba.swt.dsl.common.layout.models.edge.AbstractPointEdge;
import de.uniba.swt.dsl.common.layout.models.edge.BlockEdge;
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
        List<String> blockIds = new ArrayList<>();
        String cmtPath = route.getEdges().stream().map(AbstractEdge::getKey).collect(Collectors.joining(" -> "));
        appendLine("# %s", cmtPath);
        for (AbstractEdge edge : route.getEdges()) {
            // cache points
            if (edge instanceof AbstractPointEdge) {
                points.add((AbstractPointEdge)edge);
            }

            // cache block
            if (edge instanceof BlockEdge) {
                blockIds.add(edge.getKey());
            }

            // render
            for (SegmentElement segment : edge.getSegments()) {
                appendLine("- id: %s", segment.getName());
                length += segment.getLength().getValue();
                unit = segment.getLength().getUnit();
            }
        }
        decreaseLevel();

        // sections
        appendLine("sections:");
        increaseLevel();
        for (String blockId : blockIds) {
            appendLine("- id: %s", blockId);
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
