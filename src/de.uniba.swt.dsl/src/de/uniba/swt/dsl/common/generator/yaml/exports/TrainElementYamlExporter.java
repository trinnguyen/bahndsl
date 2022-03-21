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

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.ArrayList;
import java.util.List;

class TrainElementYamlExporter extends AbstractElementYamlExporter<TrainElement> {
    @Override
    protected String getId(TrainElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(TrainElement element) {
        var list = new ArrayList<Tuple<String, Object>>();
        list.add(Tuple.of("dcc-address", element.getAddress()));
        list.add(Tuple.of("dcc-speed-steps", element.getSteps()));
        list.add(Tuple.of("calibration", element.getCalibrations()));
        list.add(Tuple.of("peripherals", element.getPeripherals()));

        // go through prop
        for (ConfigProp prop : element.getProps()) {
            if (prop.getValue() instanceof Length) {
                list.add(Tuple.of("length", CommonFormatter.formatLength((Length) prop.getValue())));
            } else if (prop.getValue() instanceof Weight) {
                list.add(Tuple.of("weight", CommonFormatter.formatWeight((Weight) prop.getValue())));
            } else if (prop.getValue() instanceof TrainTypeValue) {
                list.add(Tuple.of("type", ((TrainTypeValue)prop.getValue()).getType().getName().toLowerCase()));
            }
        }

        return list;
    }
}
