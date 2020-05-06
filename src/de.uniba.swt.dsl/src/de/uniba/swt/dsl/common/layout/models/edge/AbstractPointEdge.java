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
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

import java.util.List;
import java.util.Objects;

public abstract class AbstractPointEdge extends AbstractEdge {
    private PointElement pointElement;

    public AbstractPointEdge(PointElement pointElement, LayoutVertex srcVertex, LayoutVertex destVertex) {
        super(srcVertex, destVertex);
        this.pointElement = pointElement;
    }

    public PointElement getPointElement() {
        return pointElement;
    }

    public void setPointElement(PointElement pointElement) {
        this.pointElement = pointElement;
    }

    @Override
    public List<SegmentElement> getSegments() {
        return List.of(getPointElement().getMainSeg());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPointEdge)) return false;
        if (!super.equals(o)) return false;

        AbstractPointEdge that = (AbstractPointEdge) o;

        return Objects.equals(pointElement, that.pointElement);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (pointElement != null ? pointElement.hashCode() : 0);
        return result;
    }

    public abstract String formatAspect();
}
