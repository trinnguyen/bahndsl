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

package de.uniba.swt.dsl.common.generator.sccharts.builder;

public class TextualBuilder {

    public final static String LINE_BREAK = System.lineSeparator();

    private final static String SPACE = " ";

    private StringBuilder builder = new StringBuilder();

    private int indentLevel = 0;

    private String indent = "";

    public TextualBuilder increaseIndent() {
        indentLevel++;
        updateIndent();
        return this;
    }

    public TextualBuilder decreaseIndent() {
        indentLevel = Math.max(0, indentLevel - 1);
        updateIndent();
        return this;
    }

    public TextualBuilder append(String text) {
        builder.append(text).append(indent);
        return this;
    }

    public TextualBuilder appendLine(String text) {
        builder.append(LINE_BREAK).append(indent).append(text);
        return this;
    }

    public TextualBuilder clear() {
        builder = new StringBuilder();
        indentLevel = 0;
        updateIndent();
        return this;
    }

    public String build() {
        return builder.toString();
    }

    private void updateIndent() {
        indent = SPACE.repeat(Math.max(0, indentLevel * 2));
    }
}
