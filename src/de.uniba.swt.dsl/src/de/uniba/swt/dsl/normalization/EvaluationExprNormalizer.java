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

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.Collection;
import java.util.List;

public class EvaluationExprNormalizer extends AbstractNormalizer {

    public static final String EXTERN_IS_SEGMENT_OCCUPIED = "is_segment_occupied";

    @Override
    protected Collection<Statement> processExpr(Expression expr) {
        // is route/segment (not) available/occupied
        if (expr instanceof EvaluateFuncExpr) {
            var evalExpr = (EvaluateFuncExpr) expr;

            // get route train config and check if not null
            if (evalExpr.isRouteAvailable()) {

                // output:
                // get config route.train train_id == ""

                // prepare custom schema
                Tuple<SchemaElement, ElementProp> routeTrainSchema = createRouteTrainSchema();
                var getter = BahnFactory.eINSTANCE.createGetConfigFuncExpr();
                getter.setType(routeTrainSchema.getFirst());
                getter.setProp(routeTrainSchema.getSecond());
                getter.setConfigExpr(evalExpr.getObjectExpr());

                BehaviourGetExpr getExpr = BahnFactory.eINSTANCE.createBehaviourGetExpr();
                getExpr.setGetExpr(getter);

                // create equality op
                var opExpr = BahnFactory.eINSTANCE.createOpExpression();
                opExpr.setLeftExpr(getExpr);
                opExpr.setOp(evalExpr.isNot() ? OperatorType.NOT_EQUAL : OperatorType.EQUAL);
                opExpr.setRightExpr(createString(""));

                BahnUtil.replaceEObject(expr, opExpr);
                return List.of();
            } else if (evalExpr.isSegmentOccupied()) {
                // output:
                //  extern is_segment_occupied(segment_id)
                var rawExpr = createExternalFunctionCallExpr(EXTERN_IS_SEGMENT_OCCUPIED, List.of(evalExpr.getObjectExpr()));
                if (!evalExpr.isNot()) {
                    BahnUtil.replaceEObject(expr, rawExpr);
                    return List.of();
                }

                var unaryExpr = BahnFactory.eINSTANCE.createUnaryExpr();
                unaryExpr.setExpr(rawExpr);
                BahnUtil.replaceEObject(expr, unaryExpr);

                return List.of();
            }
        }

        return null;
    }

    public static ExternalFunctionCallExpr createExternalFunctionCallExpr(String name, Collection<Expression> paramExprs) {
        var expr = BahnFactory.eINSTANCE.createExternalFunctionCallExpr();
        expr.setName(name);
        expr.getParams().addAll(paramExprs);
        return expr;
    }

    private static StringLiteral createString(String value) {
        var literal = BahnFactory.eINSTANCE.createStringLiteral();
        literal.setValue(value);
        return literal;
    }

    private static Tuple<SchemaElement, ElementProp> createRouteTrainSchema() {
        var routeElement = BahnFactory.eINSTANCE.createSchemaElement();
        routeElement.setName(BahnConstants.SET_CONFIG_ROUTE_TYPE);

        var trainProp = BahnFactory.eINSTANCE.createElementProp();
        trainProp.setType(DataType.STRING_TYPE);
        trainProp.setArray(false);
        trainProp.setName(BahnConstants.SET_CONFIG_TRAIN_NAME);
        routeElement.getProperties().add(trainProp);

        return Tuple.of(routeElement, trainProp);
    }
}
