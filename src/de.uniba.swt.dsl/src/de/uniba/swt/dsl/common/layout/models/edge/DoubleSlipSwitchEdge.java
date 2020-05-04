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

package de.uniba.swt.dsl.common.layout.models.edge;

import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

public class DoubleSlipSwitchEdge extends AbstractPointEdge {

    public enum Aspect {
        Normal1,
        Normal2,
        Reverse1,
        Reverse2,
    }

    private Aspect aspect;

    public DoubleSlipSwitchEdge(PointElement pointElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
        super(pointElement, srcVertex, destVertex);
        this.aspect = aspect;
    }

    public Aspect getAspect() {
        return aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }

    @Override
    public EdgeType getEdgeType() {
        return EdgeType.DoubleSlipSwitch;
    }

    @Override
    public String getKey() {
        return (getPointElement().getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoubleSlipSwitchEdge)) return false;
        if (!super.equals(o)) return false;

        DoubleSlipSwitchEdge that = (DoubleSlipSwitchEdge) o;

        return aspect == that.aspect;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (aspect != null ? aspect.hashCode() : 0);
        return result;
    }

    @Override
    public String formatAspect() {
        return (aspect == Aspect.Normal1 || aspect == Aspect.Normal2)
                ? "normal"
                : "reverse";
    }
}
