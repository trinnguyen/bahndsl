package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.VarDecl;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class SCChartsTextualBuilder extends TextualBuilder {

    public String buildString(SCCharts model) {
        clear();
        for (var rootState : model.getRootStates()) {
            var text = new StateTextualBuilder(rootState).build();
            append(text).append(LINE_BREAK);
        }

        return build();
    }
}
