package de.uniba.swt.dsl.common.layout.models.graph;

import de.uniba.swt.dsl.bahn.BlockElement;

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
}
