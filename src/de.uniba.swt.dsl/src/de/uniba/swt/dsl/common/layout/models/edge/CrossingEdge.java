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

import de.uniba.swt.dsl.bahn.CrossingElement;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

import java.util.List;

public class CrossingEdge extends AbstractEdge {

    private final CrossingElement crossing;
    private final Aspect aspect;

    public CrossingEdge(CrossingElement crossingElement, Aspect aspect, LayoutVertex srcVertex, LayoutVertex destVertex) {
        super(srcVertex, destVertex);
        this.crossing = crossingElement;
        this.aspect = aspect;
    }

    public Aspect getAspect() {
        return aspect;
    }

    public CrossingElement getCrossing() {
        return crossing;
    }

    @Override
    public EdgeType getEdgeType() {
        return EdgeType.Crossing;
    }

    @Override
    public String getKey() {
        return (getCrossing().getName() + "." + getAspect().toString()).toLowerCase();
    }

    @Override
    public List<SegmentElement> getSegments() {
        return List.of(crossing.getMainSeg());
    }

    public enum Aspect {
        Line1, // down1 -> up2
        Line2 // down2 -> up1
    }
}
