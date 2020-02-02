package de.uniba.swt.dsl.common.layout.models.vertex;

import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.util.BahnConstants;

import java.util.Set;

public class StandardSwitchVertexMember extends AbstractSwitchVertexMember {

    public enum Endpoint {
        Stem,
        Normal,
        Reverse
    }

    private Endpoint endpoint;

    public StandardSwitchVertexMember(PointElement point, String prop) {
        this(point, convertToPointEndpoint(prop));
    }

    public StandardSwitchVertexMember(PointElement point, Endpoint endpoint) {
        super(point);
        this.endpoint = endpoint;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.StandardSwitch;
    }

    @Override
    public String getKey() {
        return generateKey(getEndpoint());
    }

    public String generateKey(Endpoint endpoint) {
        return getName() + "." + endpoint.toString().toLowerCase();
    }

    private static Endpoint convertToPointEndpoint(String prop) {
        switch (prop.toLowerCase()) {
            case BahnConstants.STANDARD_SWITCH_STRAIGHT:
                return Endpoint.Normal;
            case BahnConstants.STANDARD_SWITCH_SIDE:
                return Endpoint.Reverse;
            default:
                return Endpoint.Stem;
        }
    }

    public Set<Endpoint> getConnectedEndpoints() {
        return this.getEndpoint() == Endpoint.Stem ?
                Set.of(Endpoint.Normal, Endpoint.Reverse) :
                Set.of(Endpoint.Stem);
    }
}
