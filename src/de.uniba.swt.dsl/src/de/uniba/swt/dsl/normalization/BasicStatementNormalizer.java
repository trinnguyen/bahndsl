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

package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BasicStatementNormalizer extends AbstractNormalizer {

    @Inject
    TemporaryVarGenerator temporaryVarGenerator;

    @Override
    protected List<Statement> processExpr(Expression expr) {
        if (expr instanceof RegularFunctionCallExpr) {
            return normalizeRegularFunction((RegularFunctionCallExpr) expr);
        }

        return null;
    }

    private List<Statement> normalizeRegularFunction(RegularFunctionCallExpr funcExpr) {
        // process all params
        var normalizedParams = normalizeExprs(funcExpr.getParams());

        // stop if direct assignment
        boolean stop = !funcExpr.getDecl().isReturn()
                || funcExpr.eContainer() instanceof VariableAssignment
                || funcExpr.eContainer() instanceof FunctionCallStmt;
        if (stop) {
            return normalizedParams;
        }

        // result
        List<Statement> result = new ArrayList<>();
        if (normalizedParams != null) {
            result.addAll(normalizedParams);
        }

        // create variable reference
        var tempVarStmt = refactorUsingTemporaryVar(funcExpr, new ExprDataType(funcExpr.getDecl().getReturnType(), funcExpr.getDecl().isReturnArray()));
        result.add(tempVarStmt);

        return result;
    }
}

