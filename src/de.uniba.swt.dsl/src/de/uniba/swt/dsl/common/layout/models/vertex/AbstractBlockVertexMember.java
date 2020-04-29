package de.uniba.swt.dsl.common.layout.models.vertex;

import de.uniba.swt.dsl.bahn.BlockElement;

public abstract class AbstractBlockVertexMember extends AbstractVertexMember {
    private final BlockElement block;

    public AbstractBlockVertexMember(BlockElement block) {
        this.block = block;
    }

    public BlockElement getBlock() {
        return block;
    }

    @Override
    public String getName() {
        return block.getName();
    }
}
