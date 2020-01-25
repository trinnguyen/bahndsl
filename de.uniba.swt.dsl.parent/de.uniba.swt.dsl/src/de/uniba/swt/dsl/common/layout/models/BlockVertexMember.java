package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.bahn.BlockElement;

public class BlockVertexMember extends AbstractBlockVertexMember {

    public BlockVertexMember createBlockMember(BlockEndpoint endpoint) {
        return new BlockVertexMember(getBlock(), endpoint);
    }

    public enum BlockEndpoint {
        Up,
        Down
    }

    private BlockEndpoint endpoint;

    public BlockVertexMember(BlockElement block, String prop) {
        this(block, convertToEndpoint(prop));
    }

    public BlockVertexMember(BlockElement block, BlockEndpoint endpoint) {
        super(block);
        this.endpoint = endpoint;
    }

    public BlockEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(BlockEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    private static BlockEndpoint convertToEndpoint(String prop) {
        if ("up".equals(prop.toLowerCase())) {
            return BlockEndpoint.Up;
        }
        return BlockEndpoint.Down;
    }

    @Override
    public String getKey() {
        return getName() + "." + getEndpoint().toString().toLowerCase();
    }
}
