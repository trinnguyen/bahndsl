package de.uniba.swt.dsl.common.layout.models.vertex;

/**
 * Represent a vertex member containing element and property
 */
public abstract class AbstractVertexMember {

    protected AbstractVertexMember() {
    }

    /**
     * element name
     * @return
     */
    public abstract String getName();

    public abstract VertexMemberType getType();

    /**
     * element name and property
     * @return
     */
    public abstract String getKey();

    public boolean isSegmentBlock(){
        var type = getType();
        return type == VertexMemberType.Block;
    }
}

