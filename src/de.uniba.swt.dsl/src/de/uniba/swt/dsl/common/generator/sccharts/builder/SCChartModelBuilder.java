package de.uniba.swt.dsl.common.generator.sccharts.builder;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.sccharts.StateTable;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.BahnException;
import de.uniba.swt.dsl.common.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class SCChartModelBuilder {

    private StateTable stateTable = new StateTable();

    private Map<FuncDecl, RootState> mapFuncState = new HashMap<>();
    private List<RootStateBuilder> builders = new ArrayList<>();
    private Stack<SuperState> superStates = new Stack<>();

    public SCCharts createModel(RootModule rootModule) {

        mapFuncState.clear();

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
        for (RootStateBuilder builder : builders) {
            builder.build();
        }

        return new SCCharts(new ArrayList<>(mapFuncState.values()));
    }

}

