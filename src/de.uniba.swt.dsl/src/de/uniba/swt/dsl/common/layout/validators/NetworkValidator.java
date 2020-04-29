package de.uniba.swt.dsl.common.layout.validators;

import de.uniba.swt.dsl.common.layout.models.CompositeLayoutException;
import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.VertexMemberType;
import de.uniba.swt.dsl.common.layout.validators.connectors.*;

import java.util.*;

public class NetworkValidator {

    private final Set<String> cacheElements = new HashSet<>();
    private final GraphStrongConnectedChecker graphValidator = new GraphStrongConnectedChecker();
    private NetworkLayout networkLayout;
    private final Map<VertexMemberType, AbstractConnectorValidator> validators;

    public NetworkValidator() {
        validators = Map.of(
                VertexMemberType.Block, new BlockConnectorValidator(),
                VertexMemberType.StandardSwitch, new StandardSwitchConnectorValidator(),
                VertexMemberType.DoubleSlipSwitch, new DoubleSlipSwitchConnectorValidator(),
                VertexMemberType.Crossing, new CrossingConnectorValidator());
    }

    public void checkWelformness(NetworkLayout networkLayout) throws CompositeLayoutException {
        this.networkLayout = networkLayout;

        // 1. validate connectors
        validateConnectors();

        // 2. ensure all vertices are reachable
        if (!graphValidator.isStrongConnected(networkLayout))
            throw new CompositeLayoutException("Network layout is not strongly connected");
    }

    private void validateConnectors() throws CompositeLayoutException {
        cacheElements.clear();
        List<LayoutException> errors = new ArrayList<>();
        for (var vertex : networkLayout.getVertices()) {
            for (var member : vertex.getMembers()) {

                if (cacheElements.contains(member.getName()))
                    continue;

                if (validators.containsKey(member.getType())) {
                    try {
                        validators.get(member.getType()).validate(networkLayout, member);
                    } catch (LayoutException exception) {
                        errors.add(exception);
                    }
                }

                cacheElements.add(member.getName());
            }
        }

        // throw errors
        if (errors.size() > 0) {
            throw new CompositeLayoutException(errors);
        }
    }
}
