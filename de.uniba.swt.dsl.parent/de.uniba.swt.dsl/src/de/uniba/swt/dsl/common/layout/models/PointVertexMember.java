package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.bahn.BlockElement;

public class PointVertexMember extends AbstractBlockVertexMember {

    public enum PointEndpoint {
        Stem,
        Normal,
        Reverse
    }

    private PointEndpoint endpoint;

    public PointVertexMember(BlockElement block, String prop) {
        this(block, convertToPointEndpoint(prop));
    }

    public PointVertexMember(BlockElement block, PointEndpoint endpoint) {
        super(block);
        this.endpoint = endpoint;
    }

    public PointVertexMember createMember(PointEndpoint endpoint) {
        return new PointVertexMember(getBlock(), endpoint);
    }

    public PointEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(PointEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.Point;
    }

    @Override
    public String getKey() {
        return getName() + "." + getEndpoint().toString().toLowerCase();
    }

    private static PointEndpoint convertToPointEndpoint(String prop) {
        switch (prop.toLowerCase()) {
            case "straight":
                return PointEndpoint.Normal;
            case "side":
                return PointEndpoint.Reverse;
            default:
                return PointEndpoint.Stem;
        }
    }
}
