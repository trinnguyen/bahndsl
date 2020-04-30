package de.uniba.swt.dsl.common.layout.validators.connectors;

import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.CrossingVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.StandardSwitchVertexMember;
import de.uniba.swt.dsl.validation.ValidationErrors;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class StandardSwitchConnectorValidator extends AbstractConnectorValidator {

    private final Set<StandardSwitchVertexMember.Endpoint> endpoints = Set.of(
            StandardSwitchVertexMember.Endpoint.Stem,
            StandardSwitchVertexMember.Endpoint.Normal,
            StandardSwitchVertexMember.Endpoint.Reverse
    );

    @Override
    public void validate(NetworkLayout networkLayout, AbstractVertexMember member) throws LayoutException {
        if (!(member instanceof StandardSwitchVertexMember))
            return;

        var switchMember = (StandardSwitchVertexMember) member;

        // find vertices
        var countItems = endpoints.stream()
                .map(e -> networkLayout.findVertex(switchMember.generateKey(e)))
                .filter(Objects::nonNull)
                .distinct().count();
        ensureEndpoints(member.getName(), 3, (int) countItems);
    }
}
