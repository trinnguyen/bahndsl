package de.uniba.swt.dsl.common.layout.validators.connectors;

import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.CrossingVertexMember;

import java.util.Objects;
import java.util.Set;

public class CrossingConnectorValidator extends AbstractConnectorValidator {

    private final Set<CrossingVertexMember.Endpoint> endpoints = Set.of(
            CrossingVertexMember.Endpoint.Down1,
            CrossingVertexMember.Endpoint.Down2,
            CrossingVertexMember.Endpoint.Up1,
            CrossingVertexMember.Endpoint.Up2
    );

    @Override
    public void validate(NetworkLayout networkLayout, AbstractVertexMember member) throws LayoutException {
        if (!(member instanceof CrossingVertexMember))
            return;

        CrossingVertexMember crossingVertexMember = (CrossingVertexMember) member;

        // find vertices
        var countItems = endpoints.stream()
                .map(e -> networkLayout.findVertex(crossingVertexMember.generateKey(e)))
                .filter(Objects::nonNull)
                .distinct().count();
        if (countItems != 4) {
            throw new LayoutException("Crossing must connect to 4 different blocks: " + member.getName());
        }
    }
}
