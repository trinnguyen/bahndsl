package de.uniba.swt.dsl.common.layout.models.vertex;

import de.uniba.swt.dsl.bahn.BlockElement;

public class BlockVertexMember extends AbstractBlockVertexMember {

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
    public VertexMemberType getType() {
        return VertexMemberType.Block;
    }

    @Override
    public String getKey() {
        return generateKey(getEndpoint());
    }

    public String generateKey(BlockEndpoint endpoint) {
        return getName() + "." + endpoint.toString().toLowerCase();
    }

    public BlockVertexMember generateMember(BlockEndpoint endpoint) {
        return new BlockVertexMember(this.getBlock(), endpoint);
    }
}
