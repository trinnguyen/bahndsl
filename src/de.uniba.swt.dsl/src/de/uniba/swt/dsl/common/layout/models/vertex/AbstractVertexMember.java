package de.uniba.swt.dsl.common.layout.models.vertex;

public abstract class AbstractVertexMember {

    protected AbstractVertexMember() {
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
        return getType() == VertexMemberType.StandardSwitch || getType() == VertexMemberType.DoubleSlipSwitch;
    }
}

