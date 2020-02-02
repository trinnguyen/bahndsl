package de.uniba.swt.dsl.common.layout.models.vertex;

import de.uniba.swt.dsl.bahn.PointElement;

public abstract class AbstractSwitchVertexMember extends AbstractVertexMember {

    private PointElement pointElement;

    public AbstractSwitchVertexMember(PointElement pointElement) {
        this.pointElement = pointElement;
    }

    public PointElement getPointElement() {
        return pointElement;
    }

    public void setPointElement(PointElement pointElement) {
        this.pointElement = pointElement;
    }

    @Override
    public String getName() {
        return pointElement.getName();
    }
}
