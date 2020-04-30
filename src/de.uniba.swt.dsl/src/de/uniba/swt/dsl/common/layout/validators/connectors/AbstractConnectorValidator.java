package de.uniba.swt.dsl.common.layout.validators.connectors;

import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.validation.ValidationErrors;

public abstract class AbstractConnectorValidator {
    public abstract void validate(NetworkLayout networkLayout, AbstractVertexMember member) throws LayoutException;

    protected void ensureEndpoints(String name, int expected, int actual) throws LayoutException {
        if (expected != actual) {
            throw new LayoutException(String.format(ValidationErrors.MissingLayoutEndpointFormat, name, expected));
        }
    }
}
