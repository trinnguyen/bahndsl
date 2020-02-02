package de.uniba.swt.dsl.common.layout.validators.connectors;

import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.DoubleSlipSwitchVertexMember;

import java.util.Objects;
import java.util.Set;

public class DoubleSlipSwitchConnectorValidator extends AbstractConnectorValidator {

    private final Set<DoubleSlipSwitchVertexMember.Endpoint> endpoints = Set.of(
            DoubleSlipSwitchVertexMember.Endpoint.Down1,
            DoubleSlipSwitchVertexMember.Endpoint.Down2,
            DoubleSlipSwitchVertexMember.Endpoint.Up1,
            DoubleSlipSwitchVertexMember.Endpoint.Up2
    );

    @Override
    public void validate(NetworkLayout networkLayout, AbstractVertexMember member) throws LayoutException {
        if (!(member instanceof DoubleSlipSwitchVertexMember))
            return;

        DoubleSlipSwitchVertexMember doubleSlipSwitchMember = (DoubleSlipSwitchVertexMember) member;

        // find vertices
        var countItems = endpoints.stream()
                .map(e -> networkLayout.findVertex(doubleSlipSwitchMember.generateKey(e)))
                .filter(Objects::nonNull)
                .distinct().count();
        if (countItems != 4) {
            throw new LayoutException("Crossing must connect to 4 different blocks: " + member.getName());
        }
    }
}
