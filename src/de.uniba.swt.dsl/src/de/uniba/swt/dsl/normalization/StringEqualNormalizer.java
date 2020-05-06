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
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;

import java.util.Collection;
import java.util.List;

public class StringEqualNormalizer extends AbstractNormalizer {

    public final static String ExternStringEqualsFuncName = "string_equals";

    // public final static String ExternStringConcatFuncName = "string_concat";

    @Inject
    TypeCheckingTable typeCheckingTable;

    @Override
    protected Collection<Statement> processExpr(Expression expr) {
        if (expr instanceof OpExpression) {
            var opExpr = (OpExpression) expr;
            if (opExpr.getLeftExpr() != null
                    && opExpr.getRightExpr() != null) {
                var typeLeft = typeCheckingTable.computeDataType(opExpr.getLeftExpr());
                var typeRight = typeCheckingTable.computeDataType(opExpr.getLeftExpr());

                // verify 2 sides are string
                if (typeLeft != null && typeLeft.isScalarString() && typeRight != null && typeRight.isScalarString()) {

                    String externName = getExternName(opExpr.getOp());
                    if (externName != null && !externName.isBlank()) {
                        PrimaryExpr externExpr = SyntacticTransformer.createExternalFunctionCallExpr(externName, List.of(opExpr.getLeftExpr(), opExpr.getRightExpr()));

                        // create unary if not equal
                        if (opExpr.getOp() == OperatorType.NOT_EQUAL) {
                            var unaryExpr = BahnFactory.eINSTANCE.createUnaryExpr();
                            unaryExpr.setExpr(externExpr);
                            externExpr = unaryExpr;
                        }

                        // replace
                        BahnUtil.replaceEObject(expr, externExpr);

                        return List.of();
                    }

                }
            }
        }

        return null;
    }

    private String getExternName(OperatorType op) {
        if (op == OperatorType.EQUAL || op == OperatorType.NOT_EQUAL) {
            return ExternStringEqualsFuncName;
        }

//        if (op == OperatorType.PLUS) {
//            return ExternStringConcatFuncName;
//        }

        return null;
    }
}
