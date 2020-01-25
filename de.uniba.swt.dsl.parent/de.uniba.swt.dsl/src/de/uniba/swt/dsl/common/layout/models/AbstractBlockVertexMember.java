package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.bahn.BlockElement;

public abstract class AbstractBlockVertexMember extends VertexMember {
    private BlockElement block;

    public AbstractBlockVertexMember(BlockElement block) {
        this.block = block;
    }

    public BlockElement getBlock() {
        return block;
    }

    public void setBlock(BlockElement block) {
        this.block = block;
    }

    @Override
    public String getName() {
        return block.getName();
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.Block;
    }
}
