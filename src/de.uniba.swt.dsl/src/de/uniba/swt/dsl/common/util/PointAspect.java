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

import de.uniba.swt.dsl.bahn.PointAspectType;

import java.util.Objects;

public class PointAspect {
    private final PointAspectType aspectType;
    private final String hexValue;

    public PointAspect(PointAspectType aspectType, String hexValue) {
        this.aspectType = aspectType;
        this.hexValue = hexValue;
    }

    public PointAspectType getAspectType() {
        return aspectType;
    }

    public String getHexValue() {
        return hexValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointAspect)) return false;

        PointAspect that = (PointAspect) o;

        if (aspectType != that.aspectType) return false;
        return Objects.equals(hexValue, that.hexValue);
    }

    @Override
    public int hashCode() {
        int result = aspectType != null ? aspectType.hashCode() : 0;
        result = 31 * result + (hexValue != null ? hexValue.hashCode() : 0);
        return result;
    }
}
