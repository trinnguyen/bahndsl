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

import de.uniba.swt.dsl.bahn.CompositionSignalElement;
import de.uniba.swt.dsl.bahn.RegularSignalElement;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CompositionSignalElementYamlExporter extends AbstractElementYamlExporter<CompositionSignalElement> {

    @Override
    protected String getId(CompositionSignalElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(CompositionSignalElement element) {
        List<Tuple<String, Object>> items = new ArrayList<>();
        for (RegularSignalElement signal : element.getSignals()) {
            items.add(Tuple.of(signal.getType().getName(), signal.getName()));
        }
        return items;
    }
}
