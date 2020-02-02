package de.uniba.swt.dsl.common.layout.validators.connectors;

import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;

public abstract class AbstractConnectorValidator {
    public abstract void validate(NetworkLayout networkLayout, AbstractVertexMember member) throws LayoutException;
}
