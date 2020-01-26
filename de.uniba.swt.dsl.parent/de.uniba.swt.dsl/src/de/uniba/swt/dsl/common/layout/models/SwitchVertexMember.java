package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.bahn.BlockElement;

import java.util.HashSet;
import java.util.Set;

abstract class AbstractPointVertexMember extends AbstractBlockVertexMember {

    public AbstractPointVertexMember(BlockElement block) {
        super(block);
    }
}

public class SwitchVertexMember extends AbstractPointVertexMember {

    public enum PointEndpoint {
        Stem,
        Normal,
        Reverse
    }

    private PointEndpoint endpoint;

    public SwitchVertexMember(BlockElement block, String prop) {
        this(block, convertToPointEndpoint(prop));
    }

    public SwitchVertexMember(BlockElement block, PointEndpoint endpoint) {
        super(block);
        this.endpoint = endpoint;
    }

    public PointEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(PointEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.Switch;
    }

    @Override
    public String getKey() {
        return generateKey(getEndpoint());
    }

    public String generateKey(PointEndpoint endpoint) {
        return getName() + "." + endpoint.toString().toLowerCase();
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

    public Set<PointEndpoint> getConnectedEndpoints() {
        return this.getEndpoint() == PointEndpoint.Stem ?
                Set.of(SwitchVertexMember.PointEndpoint.Normal, PointEndpoint.Reverse) :
                Set.of(PointEndpoint.Stem);
    }
}
