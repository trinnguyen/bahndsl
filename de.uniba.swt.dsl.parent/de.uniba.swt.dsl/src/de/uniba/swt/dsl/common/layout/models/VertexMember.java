package de.uniba.swt.dsl.common.layout.models;

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

    public boolean isSegmentBlock(){
        var type = getType();
        return type == VertexMemberType.Block || type == VertexMemberType.Platform;
    }

    public boolean isPoint() {
        return getType() == VertexMemberType.Switch || getType() == VertexMemberType.Crossing;
    }
}

