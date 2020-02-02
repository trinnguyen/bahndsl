package de.uniba.swt.dsl.common.layout.models.vertex;

import de.uniba.swt.dsl.bahn.CrossingElement;
import de.uniba.swt.dsl.common.util.BahnConstants;

public class CrossingVertexMember extends AbstractVertexMember {
    private final CrossingElement crossing;
    private final Endpoint endpoint;

    public CrossingVertexMember(CrossingElement crossing, String prop) {
        this.crossing = crossing;
        this.endpoint = convertToEndpoint(prop);
    }

    private static Endpoint convertToEndpoint(String prop) {
        switch (prop.toLowerCase()) {
            case BahnConstants.CROSSING_DOWN_1:
                return Endpoint.Down1;
            case BahnConstants.CROSSING_DOWN_2:
                return Endpoint.Down2;
            case BahnConstants.CROSSING_UP_1:
                return Endpoint.Up1;
            default:
                return Endpoint.Up2;
        }
    }

    public CrossingElement getCrossing() {
        return crossing;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public String getName() {
        return crossing.getName();
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.Crossing;
    }

    @Override
    public String getKey() {
        return generateKey(getEndpoint());
    }

    public Endpoint getConnectedEndpoint() {
        switch (getEndpoint()) {
            case Down1:
                return Endpoint.Up2;
            case Down2:
                return Endpoint.Up1;
            case Up1:
                return Endpoint.Down2;
            default:
                return Endpoint.Down1;
        }
    }

    public String generateKey(Endpoint endpoint) {
        return getName() + "." + endpoint.toString().toLowerCase();
    }

    public enum Endpoint {
        Down1,
        Down2,
        Up1,
        Up2
    }
}
