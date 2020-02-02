package de.uniba.swt.dsl.common.layout.validators.connectors;

import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.StandardSwitchVertexMember;

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

        StandardSwitchVertexMember standardSwitchMember = (StandardSwitchVertexMember) member;

        // find vertices
        var items = endpoints.stream()
                .map(e -> networkLayout.findVertex(standardSwitchMember.generateKey(e)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (items.size() == 3) {
            // ensure no conflict
            boolean isConflict = items.get(0).isConflict(items.get(1), member.getName())
                    || items.get(0).isConflict(items.get(2), member.getName())
                    || items.get(1).isConflict(items.get(2), member.getName());
            if (!isConflict)
                return;
        }

        throw new LayoutException("Point must connect to 3 different blocks: " + member.getName());
    }
}
