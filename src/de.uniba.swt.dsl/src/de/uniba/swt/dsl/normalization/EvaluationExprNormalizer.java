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
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.Collection;
import java.util.List;

public class EvaluationExprNormalizer extends AbstractNormalizer {

    public static final String EXTERN_IS_SEGMENT_OCCUPIED = "is_segment_occupied";
    public static final String EXTERN_IS_TYPE_SEGMENT = "is_type_segment";
    public static final String EXTERN_IS_TYPE_SIGNAL = "is_type_signal";

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
                Tuple<SchemaElement, ElementProp> routeTrainSchema = SyntacticTransformer.createRouteTrainSchema();
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
                opExpr.setRightExpr(SyntacticTransformer.createString(""));

                BahnUtil.replaceEObject(expr, opExpr);
                return List.of();
            } else if (evalExpr.isSegmentOccupied() || evalExpr.isTypeSegment() || evalExpr.isTypeSignal()) {
                // output:
                // extern is_segment_occupied(segment_id)
                // extern is_type_segment(item_id)
                // extern is_type_signal(item_id)

                String externFunctionCall = null;
                if (evalExpr.isSegmentOccupied()) {
                    externFunctionCall = EXTERN_IS_SEGMENT_OCCUPIED;
                } else if (evalExpr.isTypeSegment()) {
                    externFunctionCall = EXTERN_IS_TYPE_SEGMENT;
                } else if (evalExpr.isTypeSignal()) {
                    externFunctionCall = EXTERN_IS_TYPE_SIGNAL;
                }

                ExternalFunctionCallExpr rawExpr = SyntacticTransformer.createExternalFunctionCallExpr(externFunctionCall, List.of(evalExpr.getObjectExpr()));
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

}
