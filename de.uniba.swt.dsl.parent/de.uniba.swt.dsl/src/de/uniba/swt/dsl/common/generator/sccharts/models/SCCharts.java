package de.uniba.swt.dsl.common.generator.sccharts.models;

import java.util.List;

public class SCCharts {
    private List<RootState> rootStates;

    public SCCharts(List<RootState> rootStates) {
        this.rootStates = rootStates;
    }

    public List<RootState> getRootStates() {
        return rootStates;
    }

    public void setRootStates(List<RootState> rootStates) {
        this.rootStates = rootStates;
    }
}
