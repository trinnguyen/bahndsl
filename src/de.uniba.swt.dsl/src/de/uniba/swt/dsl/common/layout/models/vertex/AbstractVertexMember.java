package de.uniba.swt.dsl.common.layout.models.vertex;

public abstract class AbstractVertexMember {

    protected AbstractVertexMember() {
    }

    public abstract String getName();

    public abstract VertexMemberType getType();

    public abstract String getKey();

    public boolean isSegmentBlock(){
        var type = getType();
        return type == VertexMemberType.Block;
    }
}

