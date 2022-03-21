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

import de.uniba.swt.dsl.bahn.BoardFeatureElement;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.List;

class BoardFeatureElementYamlExporter extends AbstractElementYamlExporter<BoardFeatureElement> {

    @Override
    protected String getIdName() {
        return "number";
    }

    @Override
    protected String getId(BoardFeatureElement element) {
        return element.getNumber();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(BoardFeatureElement element) {
        return List.of(Tuple.of("value", element.getValue()));
    }
}
