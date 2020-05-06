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

package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.LengthUnit;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.util.ExtraBlockElement;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ExtraBlockElementYamlExporter extends AbstractElementYamlExporter<ExtraBlockElement> {
    @Override
    protected String getId(ExtraBlockElement element) {
        return element.getBlockElement().getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(ExtraBlockElement element) {
        List<Tuple<String, Object>> list = new ArrayList<>();
        List<String> overlaps = new ArrayList<>();

        double length = element.getBlockElement().getMainSeg().getLength().getValue();
        LengthUnit unit = element.getBlockElement().getMainSeg().getLength().getUnit();
        for (SegmentElement overlap : element.getBlockElement().getOverlaps()) {
            overlaps.add(overlap.getName());
            length += overlap.getLength().getValue();
        }

        list.add(Tuple.of("length", String.format("%.2f%s", length, unit.getLiteral().toLowerCase())));
        list.add(Tuple.of("main", element.getBlockElement().getMainSeg().getName()));
        list.add(Tuple.of("overlaps", overlaps));
        if (element.getDirection() != null) {
            list.add(Tuple.of("direction", element.getDirection().toString().toLowerCase()));
        }

        list.add(Tuple.of("trains", element.getBlockElement().getTrainTypes().stream().map(trainType -> trainType.getName().toLowerCase()).collect(Collectors.toList())));
        if (element.getSignals() != null) {
            list.add(Tuple.of("signals", element.getSignals()));
        }

        var limitKey = element.getBlockElement().getLimitKey();
        var speed = element.getBlockElement().getLimitSpeed();
        if (limitKey != null && speed != null) {
            var fmtSpeed = speed.getValue() + speed.getUnit().toString();
            list.add(Tuple.of(limitKey.getName(), fmtSpeed));
        }

        return list;
    }
}
