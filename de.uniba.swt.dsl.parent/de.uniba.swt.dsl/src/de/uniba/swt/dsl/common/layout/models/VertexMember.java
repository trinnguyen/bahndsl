package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.SignalElement;
import org.eclipse.emf.ecore.EObject;

import java.util.Objects;

public abstract class VertexMember {

    protected VertexMember() {
    }

    public abstract String getName();

    public abstract VertexMemberType getType();

    public abstract String getKey();

    @Override
    public String toString() {
        return getKey() + "(" + getType() + ")";
    }
}

