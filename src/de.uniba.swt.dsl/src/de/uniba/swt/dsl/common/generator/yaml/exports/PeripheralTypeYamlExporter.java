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

import de.uniba.swt.dsl.bahn.AspectElement;
import de.uniba.swt.dsl.bahn.SignalType;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class SignalTypeYamlExporter extends AbstractElementYamlExporter<SignalType> {

    @Override
    protected String getId(SignalType element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(SignalType element) {
        List<String> aspectIds = element.getItems() != null ? element.getItems().stream().map(AspectElement::getName).collect(Collectors.toList()) : new ArrayList<>();
        return List.of(Tuple.of("aspects", aspectIds));
    }
}