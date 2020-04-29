package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.bahn.Expression;
import de.uniba.swt.dsl.common.util.LogHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuperState extends State {
    private List<State> states = new ArrayList<>();
    private List<SVarDeclaration> declarations = new ArrayList<>();
    private Set<String> hostCodeReferences = new HashSet<>();
    private String joinTargetId;
    private final List<LocalAction> localActions = new ArrayList<>();
    private List<Expression> referenceBindingExprs = new ArrayList<>();

    public SuperState(SuperState other, List<Expression> params) {
        super(other);
        this.states = other.states;
        this.declarations = other.declarations;
        this.hostCodeReferences = other.hostCodeReferences;
        this.joinTargetId = other.joinTargetId;

        // update params
        if (params != null)
            this.referenceBindingExprs.addAll(params);
    }

    public List<State> getStates() {
        return states;
    }

    public List<SVarDeclaration> getDeclarations() {
        return declarations;
    }

    public Set<String> getHostCodeReferences() {
        return hostCodeReferences;
    }

    public String getJoinTargetId() {
        return joinTargetId;
    }

    public void setJoinTargetId(String joinTargetId) {
        this.joinTargetId = joinTargetId;
    }

    public SuperState(String id) {
        super(id);
    }

    public List<Expression> getReferenceBindingExprs() {
        return referenceBindingExprs;
    }

    public List<LocalAction> getLocalActions() {
        return localActions;
    }

    @Override
    public String toString() {
        return "SuperState{" +
                "states=" + states +
                ", declarations=" + declarations +
                ", hostCodeReferences=" + hostCodeReferences +
                '}';
    }
}
