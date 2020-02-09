package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.common.util.LogHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuperState extends State {
    private List<State> states = new ArrayList<>();
    private List<SVarDeclaration> declarations = new ArrayList<>();
    private Set<String> hostCodeReferences = new HashSet<>();

    public List<State> getStates() {
        return states;
    }

    public List<SVarDeclaration> getDeclarations() {
        return declarations;
    }

    public Set<String> getHostCodeReferences() {
        return hostCodeReferences;
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
