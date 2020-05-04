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

import de.uniba.swt.dsl.bahn.DataType;
import de.uniba.swt.dsl.bahn.FuncDecl;
import de.uniba.swt.dsl.bahn.RefVarDecl;
import de.uniba.swt.dsl.common.generator.sccharts.StateTable;
import de.uniba.swt.dsl.common.generator.sccharts.models.RootState;
import de.uniba.swt.dsl.common.generator.sccharts.models.SuperState;
import de.uniba.swt.dsl.common.util.BahnUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class RootStateBuilder extends SuperStateBuilder {
    public RootStateBuilder(FuncDecl funcDecl) {
        super(new HashMap<>(), new Stack<>(), new StateTable("S_" + funcDecl.getName()), new RootState(funcDecl.getName()), funcDecl.getStmtList());

        // input
        if (funcDecl.getParamDecls() != null) {
            for (RefVarDecl paramDecl : funcDecl.getParamDecls()) {
                superState.getDeclarations().add(convertDeclaration(paramDecl.getType(), paramDecl.getName(), paramDecl.isArray() ? DEFAULT_PARAM_ARRAY_SIZE : 0, true, false));
            }
        }

        // output
        if (funcDecl.isReturn()) {
            superState.getDeclarations().add(convertDeclaration(funcDecl.getReturnType(), SCChartsUtil.VAR_OUTPUT_NAME, funcDecl.isReturnArray() ? DEFAULT_PARAM_ARRAY_SIZE : 0, false, true));
            superState.getDeclarations().add(convertDeclaration(DataType.BOOLEAN_TYPE, SCChartsUtil.VAR_HAS_RETURN_NAME, 0, false, false));
        }

        if (BahnUtil.hasBreakStmt(funcDecl.getStmtList())) {
            superState.getDeclarations().add(convertDeclaration(DataType.BOOLEAN_TYPE, SCChartsUtil.VAR_HAS_BREAK, 0, false, false));
        }
    }

    public RootState getRootState() {
        return (RootState) superState;
    }
}
