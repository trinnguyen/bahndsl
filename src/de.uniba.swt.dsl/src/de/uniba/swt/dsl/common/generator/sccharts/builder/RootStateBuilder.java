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

import de.uniba.swt.dsl.bahn.BahnFactory;
import de.uniba.swt.dsl.bahn.FuncDecl;
import de.uniba.swt.dsl.bahn.RefVarDecl;
import de.uniba.swt.dsl.common.generator.sccharts.StateTable;
import de.uniba.swt.dsl.common.generator.sccharts.models.RootState;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

import java.util.Stack;

public class RootStateBuilder extends SuperStateBuilder {
    public RootStateBuilder(FuncDecl funcDecl) {
        super(new Stack<>(), new StateTable("S_" + funcDecl.getName()), new RootState(funcDecl.getName()), funcDecl.getStmtList());

        // input
        if (funcDecl.getParamDecls() != null) {
            for (RefVarDecl paramDecl : funcDecl.getParamDecls()) {
                superState.getDeclarations().add(convertDeclaration(paramDecl, true, false));
            }
        }

        // output
        if (funcDecl.isReturn()) {
            var outVar = createNewVarDecl(SCChartsUtil.VAR_OUTPUT_NAME, new ExprDataType(funcDecl.getReturnType(), funcDecl.isReturnArray()));
            superState.getDeclarations().add(convertDeclaration(outVar, false, true));

            var hasReturnVar = createNewVarDecl(SCChartsUtil.VAR_HAS_RETURN_NAME, ExprDataType.ScalarBool);
            superState.getDeclarations().add(convertDeclaration(hasReturnVar, false, false));
        }

        if (BahnUtil.hasBreakStmt(funcDecl.getStmtList())) {
            var hasBreakVar = createNewVarDecl(SCChartsUtil.VAR_HAS_BREAK, ExprDataType.ScalarBool);
            superState.getDeclarations().add(convertDeclaration(hasBreakVar, false, false));
        }
    }

    private RefVarDecl createNewVarDecl(String name, ExprDataType dataType) {
        var decl = BahnFactory.eINSTANCE.createVarDecl();
        decl.setName(name);
        decl.setType(dataType.getDataType());
        decl.setArray(dataType.isArray());
        return decl;
    }

    public RootState getRootState() {
        return (RootState) superState;
    }
}
