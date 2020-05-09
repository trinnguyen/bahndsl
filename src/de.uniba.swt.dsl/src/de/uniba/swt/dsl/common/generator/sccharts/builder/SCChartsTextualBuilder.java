/*
 *
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.VarDecl;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.StringUtil;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class SCChartsTextualBuilder extends TextualBuilder {

    @Inject
    StateTextualBuilder stateBuilder;

    public String buildString(RootState rootState, List<RootState> refStates) {
        clear();

        // code naming is disabled since it is not supported in KIELER v1.1.0 (bug)
        // append(BahnUtil.generateCodeNaming(rootState.getId()));
        appendLine("#hostcode \"#include \\\"bahn_data_util.h\\\"\"");
        // appendLine("#resource \"bahn_data_util.h\"");
        // appendLine("#resource \"bahn_data_util.c\"");

        // rename the root state to capitalize to prevent generated SCCharts model
        // Bug in KIERLER Tool v1.1.0 for statebased lean (template)
        rootState.setId(StringUtil.capitalize(rootState.getId()));

        appendLine(stateBuilder.buildString(rootState));
        if (refStates != null) {
            for (RootState refState : refStates) {
                appendLine(stateBuilder.buildString(refState));
            }
        }
        return build();
    }
}
