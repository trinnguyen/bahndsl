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

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.layout.models.BlockDirection;
import de.uniba.swt.dsl.common.layout.models.vertex.BlockVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BlockEdge extends AbstractEdge {

    public BlockEdge(BlockElement blockElement, LayoutVertex srcVertex, LayoutVertex destVertex) {
        super(srcVertex, destVertex);
        this.blockElement = blockElement;
    }

    private BlockElement blockElement;

    public BlockElement getBlockElement() {
        return blockElement;
    }

    public void setBlockElement(BlockElement blockElement) {
        this.blockElement = blockElement;
    }

    @Override
    public EdgeType getEdgeType() {
        return EdgeType.Block;
    }

    @Override
    public String getKey() {
        return blockElement.getName().toLowerCase();
    }

    @Override
    public List<SegmentElement> getSegments() {
        List<SegmentElement> segments = new ArrayList<>(blockElement.getOverlaps().size() + 1);
        if (blockElement.getOverlaps().size() > 0)
        {
            segments.add(blockElement.getOverlaps().get(0));
        }
        segments.add(blockElement.getMainSeg());
        for (int i = 1; i < blockElement.getOverlaps().size(); i++) {
            segments.add(blockElement.getOverlaps().get(i));
        }

        // reverse if another direction
        if (getDirection() == BlockDirection.UpDown) {
            Collections.reverse(segments);
        }
        return segments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockEdge blockEdge = (BlockEdge) o;

        return Objects.equals(blockElement, blockEdge.blockElement);
    }

    @Override
    public int hashCode() {
        return blockElement != null ? blockElement.hashCode() : 0;
    }

    @Override
    public String toString() {
        return blockElement.getName();
    }

    public BlockVertexMember.BlockEndpoint getSrcEndpoint() {
        return findBlockVertexMember(getSrcVertex()).getEndpoint();
    }

    public BlockVertexMember.BlockEndpoint getDestEndpoint() {
        return findBlockVertexMember(getSrcVertex()).getEndpoint();
    }

    public BlockDirection getDirection() {
        return getSrcEndpoint() == BlockVertexMember.BlockEndpoint.Up
                ? BlockDirection.UpDown
                : BlockDirection.DownUp;
    }

    private BlockVertexMember findBlockVertexMember(LayoutVertex vertex) {
        return vertex.getMembers()
                .stream()
                .filter(m -> m instanceof BlockVertexMember && blockElement.equals(((BlockVertexMember) m).getBlock()))
                .map(m -> ((BlockVertexMember) m))
                .findFirst().orElseThrow();
    }
}
