package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import java.util.*;

public class SCChartModelBuilder {

    private Map<FuncDecl, RootState> mapFuncState = new HashMap<>();
    private List<RootStateBuilder> builders = new ArrayList<>();
    private Stack<SuperState> superStates = new Stack<>();

    public SCCharts createModel(RootModule rootModule) {

        mapFuncState.clear();
        builders.clear();

        // create models
        for (ModuleProperty property : rootModule.getProperties()) {
            if (property instanceof FuncDecl) {
                FuncDecl funcDecl = (FuncDecl) property;
                RootStateBuilder rootStateBuilder = new RootStateBuilder(mapFuncState, superStates, funcDecl);
                mapFuncState.put(funcDecl, rootStateBuilder.getRootState());
                builders.add(rootStateBuilder);
            }
        }

        // update model
        List<RootState> rootStates = new ArrayList<>();
        for (RootStateBuilder builder : builders) {
            builder.build();
            rootStates.add(builder.getRootState());
        }

        return new SCCharts(rootStates);
    }

}

