package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.util.BahnConstants;

import java.util.Set;

public class CrossingVertexMember extends AbstractPointVertexMember {

    public CrossingVertexMember(PointElement point, String prop) {
        super(point);
        this.endpoint = convertToEndpoint(prop);
    }

    public CrossingVertexMember(PointElement point, CrossingEndpoint endpoint) {
        super(point);
        this.endpoint = endpoint;
    }

    public enum CrossingEndpoint {
        Down1,
        Down2,
        Up1,
        Up2
    }

    private CrossingEndpoint endpoint;

    public CrossingEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(CrossingEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.Crossing;
    }

    @Override
    public String getKey() {
        return generateKey(getEndpoint());
    }

    public Set<CrossingEndpoint> getConnectedEndpoints() {
        return (getEndpoint() == CrossingEndpoint.Up1 || getEndpoint() == CrossingEndpoint.Up2)
                ? Set.of(CrossingEndpoint.Down1, CrossingEndpoint.Down2)
                : Set.of(CrossingEndpoint.Up1, CrossingEndpoint.Up2);
    }

    public String generateKey(CrossingEndpoint endpoint) {
        return getName() + "." + endpoint.toString().toLowerCase();
    }

    private static CrossingEndpoint convertToEndpoint(String prop) {
        switch (prop.toLowerCase()) {
            case BahnConstants.CROSSING_DOWN_1:
                return CrossingEndpoint.Down1;
            case BahnConstants.CROSSING_DOWN_2:
                return CrossingEndpoint.Down2;
            case BahnConstants.CROSSING_UP_1:
                return CrossingEndpoint.Up1;
            default:
                return CrossingEndpoint.Up2;
        }
    }
}
