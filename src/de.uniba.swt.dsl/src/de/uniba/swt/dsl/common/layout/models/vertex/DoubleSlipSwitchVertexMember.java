package de.uniba.swt.dsl.common.layout.models.vertex;

import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.util.BahnConstants;

import java.util.Set;

public class DoubleSlipSwitchVertexMember extends AbstractSwitchVertexMember {

    public DoubleSlipSwitchVertexMember(PointElement point, String prop) {
        super(point);
        this.endpoint = convertToEndpoint(prop);
    }

    public enum Endpoint {
        Down1,
        Down2,
        Up1,
        Up2
    }

    private final Endpoint endpoint;

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.DoubleSlipSwitch;
    }

    @Override
    public String getKey() {
        return generateKey(getEndpoint());
    }

    public Set<Endpoint> getConnectedEndpoints() {
        return (getEndpoint() == Endpoint.Up1 || getEndpoint() == Endpoint.Up2)
                ? Set.of(Endpoint.Down1, Endpoint.Down2)
                : Set.of(Endpoint.Up1, Endpoint.Up2);
    }

    public String generateKey(Endpoint endpoint) {
        return getName() + "." + endpoint.toString().toLowerCase();
    }

    private static Endpoint convertToEndpoint(String prop) {
        switch (prop.toLowerCase()) {
            case BahnConstants.DOUBLE_SLIP_SWITCH_DOWN_1:
                return Endpoint.Down1;
            case BahnConstants.DOUBLE_SLIP_SWITCH_DOWN_2:
                return Endpoint.Down2;
            case BahnConstants.DOUBLE_SLIP_SWITCH_UP_1:
                return Endpoint.Up1;
            default:
                return Endpoint.Up2;
        }
    }
}
