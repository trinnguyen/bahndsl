package de.uniba.swt.dsl.common.layout.models.graph;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.SegmentElement;
import de.uniba.swt.dsl.common.layout.models.BlockDirection;
import de.uniba.swt.dsl.common.layout.models.BlockVertexMember;

import java.util.ArrayList;
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
