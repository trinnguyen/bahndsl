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

package de.uniba.swt.dsl.generator.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArgParseResult {

    private final Map<String, String> mapValues = new HashMap<>();
    private Set<Integer> consumedIndices;

    public void addOption(ArgOption option) {
        addOption(option, null);
    }

    public void addOption(ArgOption option, String value) {
        mapValues.put(option.getName(), value);
    }

    public boolean hasOption(String name) {
        return mapValues.containsKey(name);
    }

    public String getValue(String name, String defaultValue) {
        return mapValues.getOrDefault(name, defaultValue);
    }

    public void setConsumedIndices(Set<Integer> consumedIndices) {
        this.consumedIndices = consumedIndices;
    }

    public Set<Integer> getConsumedIndices() {
        return consumedIndices;
    }
}
