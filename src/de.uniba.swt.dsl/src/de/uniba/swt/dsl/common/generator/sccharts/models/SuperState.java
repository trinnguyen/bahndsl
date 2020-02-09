package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.common.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class SuperState extends State {
    private List<State> states = new ArrayList<>();
    private List<LocalAction> localActions = new ArrayList<>();
    private List<SVarDeclaration> declarations = new ArrayList<>();

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public List<LocalAction> getLocalActions() {
        return localActions;
    }

    public void setLocalActions(List<LocalAction> localActions) {
        this.localActions = localActions;
    }

    public List<SVarDeclaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<SVarDeclaration> declarations) {
        this.declarations = declarations;
    }

    @Override
    public String toString() {
        return "SuperState{" +
                "states=" + LogHelper.printObject(states) +
                "\n, localActions=" + LogHelper.printObject(localActions) +
                "\n, declarations=" + LogHelper.printObject(declarations) +
                '}';
    }
}
