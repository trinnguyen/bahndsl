package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.VarDecl;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class SCChartsTextualBuilder extends TextualBuilder {

    @Inject
    StateTextualBuilder stateBuilder;

    public String buildString(RootState rootState) {
        clear();

        // append hostcode
        appendLine("#hostcode \"#include \\\"bahn_data_util.h\\\"\"");
        append(stateBuilder.buildString(rootState)).append(LINE_BREAK);

        return build();
    }
}
