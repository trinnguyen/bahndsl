package de.uniba.swt.dsl.common.layout.validators.connectors;

import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.BlockVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.CrossingVertexMember;
import de.uniba.swt.dsl.validation.ValidationErrors;

import java.util.Objects;
import java.util.Set;

public class BlockConnectorValidator extends AbstractConnectorValidator {
    private final Set<BlockVertexMember.BlockEndpoint> endpoints = Set.of(
            BlockVertexMember.BlockEndpoint.Down,
            BlockVertexMember.BlockEndpoint.Up
    );

    @Override
    public void validate(NetworkLayout networkLayout, AbstractVertexMember member) throws LayoutException {
        if (!(member instanceof BlockVertexMember))
            return;
        BlockVertexMember blockMember = (BlockVertexMember) member;

        // find vertices
        var countItems = endpoints.stream()
                .map(e -> networkLayout.findVertex(blockMember.generateKey(e)))
                .filter(Objects::nonNull)
                .distinct().count();

        ensureEndpoints(member.getName(), 2, (int) countItems);
    }
}
