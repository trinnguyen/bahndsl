package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.util.BahnConstants;

import java.util.Set;

abstract class AbstractPointVertexMember extends VertexMember {

    private PointElement pointElement;

    public AbstractPointVertexMember(PointElement pointElement) {
        this.pointElement = pointElement;
    }

    public PointElement getPointElement() {
        return pointElement;
    }

    public void setPointElement(PointElement pointElement) {
        this.pointElement = pointElement;
    }

    @Override
    public String getName() {
        return pointElement.getName();
    }
}

public class SwitchVertexMember extends AbstractPointVertexMember {

    public enum PointEndpoint {
        Stem,
        Normal,
        Reverse
    }

    private PointEndpoint endpoint;

    public SwitchVertexMember(PointElement point, String prop) {
        this(point, convertToPointEndpoint(prop));
    }

    public SwitchVertexMember(PointElement point, PointEndpoint endpoint) {
        super(point);
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
            case BahnConstants.SWITCH_STRAIGHT:
                return PointEndpoint.Normal;
            case BahnConstants.SWITCH_SIDE:
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
