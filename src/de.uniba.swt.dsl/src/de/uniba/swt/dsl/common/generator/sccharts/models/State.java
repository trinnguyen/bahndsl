package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.bahn.Expression;
import de.uniba.swt.dsl.common.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class State {
    private String id;
    private boolean isInitial;
    private boolean isFinal;
    private List<Transition> outgoingTransitions = new ArrayList<>();
    private String label;
    private RootState referenceState;
    private List<Expression> referenceBindingExprs = new ArrayList<>();

    public State() {
    }

    public State(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public List<Transition> getOutgoingTransitions() {
        return outgoingTransitions;
    }

    public void setOutgoingTransitions(List<Transition> outgoingTransitions) {
        this.outgoingTransitions = outgoingTransitions;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public RootState getReferenceState() {
        return referenceState;
    }

    public void setReferenceState(RootState referenceState) {
        this.referenceState = referenceState;
    }

    public List<Expression> getReferenceBindingExprs() {
        return referenceBindingExprs;
    }

    public void setReferenceBindingExprs(List<Expression> referenceBindingExprs) {
        this.referenceBindingExprs = referenceBindingExprs;
    }

    @Override
    public String toString() {
        return "State{" +
                "id='" + id + '\'' +
                ", isInitial=" + isInitial +
                ", isFinal=" + isFinal +
                ", outgoingTransitions=" + LogHelper.printObject(outgoingTransitions) +
                ", label='" + label + '\'' +
                ", referenceState=" + referenceState +
                ", referenceBindingExprs=" + referenceBindingExprs +
                '}';
    }
}
