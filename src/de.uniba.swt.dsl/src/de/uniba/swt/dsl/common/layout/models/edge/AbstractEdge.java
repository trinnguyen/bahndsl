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

import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

import java.util.List;
import java.util.Objects;

public abstract class AbstractEdge {
    public enum EdgeType {
        Block,
        Platform,
        SingleSwitch,
        DoubleSlipSwitch,
        Crossing
    }

    private LayoutVertex srcVertex;
    private LayoutVertex destVertex;

    public AbstractEdge(LayoutVertex srcVertex, LayoutVertex destVertex) {
        this.srcVertex = srcVertex;
        this.destVertex = destVertex;
    }

    public LayoutVertex getSrcVertex() {
        return srcVertex;
    }

    public void setSrcVertex(LayoutVertex srcVertex) {
        this.srcVertex = srcVertex;
    }

    public LayoutVertex getDestVertex() {
        return destVertex;
    }

    public void setDestVertex(LayoutVertex destVertex) {
        this.destVertex = destVertex;
    }

    public abstract EdgeType getEdgeType();

    public boolean isSegmentBlock() {
        return getEdgeType() == EdgeType.Block
                || getEdgeType() == EdgeType.Platform;
    }

    public boolean isPoint() {
        return getEdgeType() == EdgeType.SingleSwitch
                || getEdgeType() == EdgeType.DoubleSlipSwitch;
    }

    public abstract String getKey();

    public abstract List<SegmentElement> getSegments();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractEdge)) return false;

        AbstractEdge that = (AbstractEdge) o;

        if (!Objects.equals(srcVertex, that.srcVertex)) return false;
        return Objects.equals(destVertex, that.destVertex);
    }

    @Override
    public int hashCode() {
        int result = srcVertex != null ? srcVertex.hashCode() : 0;
        result = 31 * result + (destVertex != null ? destVertex.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
