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

public class ArgOption {
    private String name;
    private String desc;
    private boolean hasValue;
    private String paramName;

    public ArgOption(String name, String desc) {
        this(name, desc, false, null);
    }

    public ArgOption(String name, String desc, boolean hasValue, String paramName) {
        this.name = name;
        this.desc = desc;
        this.hasValue = hasValue;
        this.paramName = paramName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isHasValue() {
        return hasValue;
    }

    public void setHasValue(boolean hasValue) {
        this.hasValue = hasValue;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getFormattedName() {
        return "-" + name;
    }

    public String getFormattedKeyValue() {
        var builder = new StringBuilder().append(getFormattedName());
        if (hasValue) {
            builder.append(" ").append("<").append(paramName).append(">");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return getFormattedKeyValue();
    }
}
