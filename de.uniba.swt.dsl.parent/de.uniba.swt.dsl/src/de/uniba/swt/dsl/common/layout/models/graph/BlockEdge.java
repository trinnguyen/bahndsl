package de.uniba.swt.dsl.common.layout.models.graph;

import de.uniba.swt.dsl.bahn.BlockElement;

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
    public String toString() {
        return blockElement.getName();
    }
}
