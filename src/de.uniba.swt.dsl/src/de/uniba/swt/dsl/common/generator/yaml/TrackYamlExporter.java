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

package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

class TrackYamlExporter extends AbstractBidibYamlExporter {
    @Override
    protected String getHeaderComment() {
        return "Track configuration";
    }

    @Override
    protected void exportContent(RootModule rootModule) {
        Map<String, List<ModuleProperty>> map = buildMap(rootModule);

        appendLine("boards:");
        increaseLevel();
        for (Map.Entry<String, List<ModuleProperty>> entry : map.entrySet()) {
            exportBoard(entry.getKey(), entry.getValue());
        }
        decreaseLevel();
    }

    private void exportBoard(String boardName, List<ModuleProperty> properties) {
        appendLine("- id: %s", boardName);
        increaseLevel();

        List<Object> pointItems = new ArrayList<>();
        List<RegularSignalElement> signalsItems = new ArrayList<>();
        List<RegularSignalElement> peripherals = new ArrayList<>();
        for (ModuleProperty property : properties) {
            if (property instanceof SegmentsProperty) {
                exportSection("segments:", ((SegmentsProperty) property).getItems());
            } else if (property instanceof SignalsProperty) {
                signalsItems.addAll(regularSignalsFrom((SignalsProperty) property));
            } else if (property instanceof PointsProperty) {
                pointItems.addAll(((PointsProperty) property).getItems());
            } else if (property instanceof PeripheralsProperty) {
                peripherals.addAll(regularSignalsFrom((PeripheralsProperty) property));
            }
        }

        // add peripherals to signals or point depend on the current board, signals has higher priority
        if (signalsItems.size() > 0) {
            signalsItems.addAll(peripherals);
        } else {
            if (pointItems.size() > 0) {
                pointItems.addAll(peripherals);
            }
        }

        // exports
        if (signalsItems.size() > 0) {
            exportSection("signals-board:", signalsItems);
        }

        if (pointItems.size() > 0) {
            exportSection("points-board:", pointItems);
        }

        decreaseLevel();
    }

    private Collection<? extends RegularSignalElement> regularSignalsFrom(SignalsProperty property) {
        return property.getItems().stream().filter(s -> s instanceof RegularSignalElement).map(s -> (RegularSignalElement)s).collect(Collectors.toList());
    }

    private Collection<? extends RegularSignalElement> regularSignalsFrom(PeripheralsProperty property) {
        return property.getItems().stream().filter(s -> s instanceof RegularSignalElement).map(s -> (RegularSignalElement)s).collect(Collectors.toList());
    }

    private Map<String, List<ModuleProperty>> buildMap(RootModule rootModule) {
        Map<String, List<ModuleProperty>> map = new HashMap<>();
        for (ModuleProperty property : rootModule.getProperties()) {
            String boardName = null;
            if (property instanceof SegmentsProperty) {
                boardName = ((SegmentsProperty) property).getBoard().getName();
            } else if (property instanceof SignalsProperty) {
                boardName = ((SignalsProperty) property).getBoard().getName();
            } else if (property instanceof PeripheralsProperty) {
                boardName = ((PeripheralsProperty) property).getBoard().getName();
            } else if (property instanceof PointsProperty) {
                boardName = ((PointsProperty) property).getBoard().getName();
            }

            // check
            if (StringUtil.isNotEmpty(boardName)) {
                if (!map.containsKey(boardName)) {
                    var set = new ArrayList<ModuleProperty>();
                    set.add(property);
                    map.put(boardName, set);
                } else {
                    map.get(boardName).add(property);
                }
            }
        }
        return map;
    }
}

