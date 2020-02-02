package de.uniba.swt.dsl.common.layout.validators.connectors;

import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.BlockVertexMember;

public class BlockConnectorValidator extends AbstractConnectorValidator {
    @Override
    public void validate(NetworkLayout networkLayout, AbstractVertexMember member) throws LayoutException {
        if (!(member instanceof BlockVertexMember))
            return;
        BlockVertexMember blockMember = (BlockVertexMember) member;

        var vertex = networkLayout.findVertex(member);
        var endpoint = blockMember.getEndpoint() == BlockVertexMember.BlockEndpoint.Up ?
                BlockVertexMember.BlockEndpoint.Down :
                BlockVertexMember.BlockEndpoint.Up;
        var tmpVertex = networkLayout.findVertex(blockMember.generateKey(endpoint));

        if (vertex == null || tmpVertex == null || vertex.isConflict(tmpVertex, blockMember.getName())) {
            throw new LayoutException("Block must connect to 2 different blocks: " + member.getName());
        }
    }
}
