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

package de.uniba.swt.dsl.common.util;

import java.util.LinkedList;
import java.util.List;

public class YamlExporter {

    private static final String SPACE = "  ";
    protected List<String> items = new LinkedList<>();
    protected int indentLevel;

    protected void reset() {
        items.clear();
        indentLevel = 0;
    }

    public void appendLine(String text, Object... args) {
        items.add(SPACE.repeat(Math.max(0, indentLevel)) + String.format(text, args));
    }

    public void increaseLevel()
    {
        indentLevel++;
    }

    public void decreaseLevel()
    {
        indentLevel--;
    }

    protected String build()
    {
        return String.join("\n", items);
    }

    @Override
    public String toString() {
        return build();
    }
}
