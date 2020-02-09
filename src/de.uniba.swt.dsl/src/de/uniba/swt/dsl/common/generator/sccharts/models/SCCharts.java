package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.common.util.LogHelper;

import java.util.List;

public class SCCharts {
    private List<RootState> rootStates;

    public SCCharts(List<RootState> rootStates) {
        this.rootStates = rootStates;
    }

    public List<RootState> getRootStates() {
        return rootStates;
    }

    @Override
    public String toString() {
        return "SCCharts{" +
                "rootStates=" + LogHelper.printObject(rootStates) +
                '}';
    }
}
