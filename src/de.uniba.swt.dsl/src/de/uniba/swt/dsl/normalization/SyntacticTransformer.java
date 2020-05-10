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
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyntacticTransformer {

    @Inject
    ArrayLookupTable arrayLookupTable;

    public static final String EXTERN_TABLE_GET_ROUTES = "interlocking_table_get_routes";

    public static final String EXTERN_TRAIN_SPEED_GETTER_NAME = "train_state_get_speed";

    public static final String EXTERN_TRAIN_SPEED_SETTER_NAME = "train_state_set_speed";

    public static final String EXTERN_STATE_GETTER_NAME = "track_state_get_value";

    public static final String EXTERN_STATE_SETTER_NAME = "track_state_set_value";

    private static final String EXTERN_CONFIG_FORMAT = "config_%s_%s_%s_value";

    public static final String EXTERN_CONFIG_POINT_POSITION = "config_get_point_position";

    public boolean isSetter(BehaviourExpr expr) {
        return expr instanceof BehaviourSetExpr || expr instanceof GrantRouteFuncExpr;
    }

    public boolean isArrayGetter(BehaviourExpr expr) {
        if (expr instanceof BehaviourGetExpr) {
            var getter = ((BehaviourGetExpr) expr).getGetExpr();
            if (getter instanceof GetConfigFuncExpr) {
                var getConfig = (GetConfigFuncExpr) getter;
                return getConfig.getProp().isArray();
            }

            return getter instanceof GetRoutesFuncExpr;
        }

        return false;
    }

    public OpExpression normalizeBehaviourExpr(BehaviourExpr expr) {

        // getter
        if (expr instanceof BehaviourGetExpr) {
            BehaviourSubGetExpr getter = ((BehaviourGetExpr) expr).getGetExpr();

            // find assignment
            String lhsArrayName = getArrayDeclName(expr);
            if (getter instanceof GetConfigFuncExpr) {
                return normalizeGetConfigFuncExpr((GetConfigFuncExpr) getter, lhsArrayName);
            }

            if (getter instanceof GetPointPositionFuncExpr) {
                var positionFunExpr = (GetPointPositionFuncExpr) getter;
                return createExternalFunctionCallExpr(EXTERN_CONFIG_POINT_POSITION, List.of(positionFunExpr.getRouteExpr(), positionFunExpr.getPointEpxr()));
            }

            if (getter instanceof GetRoutesFuncExpr) {
                var routesExpr = (GetRoutesFuncExpr) getter;
                return createExternalFunctionCallExpr(EXTERN_TABLE_GET_ROUTES, List.of(routesExpr.getSrcSignalExpr(), routesExpr.getDestSignalExpr(), arrayLookupTable.createArrayVarExpr(lhsArrayName)));
            }

            if (getter instanceof GetTrainSpeedFuncExpr) {
                var speedFuncExpr = (GetTrainSpeedFuncExpr) getter;
                return createExternalFunctionCallExpr(EXTERN_TRAIN_SPEED_GETTER_NAME, List.of(speedFuncExpr.getTrainExpr()));
            }

            if (getter instanceof GetTrackStateFuncExpr) {
                var trackStateFuncExpr = (GetTrackStateFuncExpr) getter;
                return createExternalFunctionCallExpr(EXTERN_STATE_GETTER_NAME, List.of(trackStateFuncExpr.getTrackExpr()));
            }

            return null;
        }

        // setter
        if (expr instanceof BehaviourSetExpr) {
            BehaviourSubSetExpr setter = ((BehaviourSetExpr) expr).getSetExpr();

            if (setter instanceof SetConfigFuncExpr) {
                return normalizeSetConfigFuncExpr((SetConfigFuncExpr)setter);
            }

            if (setter instanceof SetTrainSpeedFuncExpr) {
                var setSpeedFuncExpr = (SetTrainSpeedFuncExpr) setter;
                return createExternalFunctionCallExpr(EXTERN_TRAIN_SPEED_SETTER_NAME, List.of(setSpeedFuncExpr.getTrainExpr(), setSpeedFuncExpr.getSpeedExpr()));
            }

            if (setter instanceof SetTrackStateFuncExpr) {
                var setStateFunExpr = (SetTrackStateFuncExpr) setter;
                var rawState = convertTrackState(setStateFunExpr.getAspectExpr());
                if (rawState != null && !rawState.isEmpty())
                    return createExternalFunctionCallExpr(EXTERN_STATE_SETTER_NAME, List.of(setStateFunExpr.getTrackExpr(), createString(rawState)));
            }

            return null;
        }

        // grant route to train
        if (expr instanceof GrantRouteFuncExpr) {

            // prepare custom schema
            Tuple<SchemaElement, ElementProp> routeTrainSchema = createRouteTrainSchema();

            // normalize to set config function
            var setExpr = BahnFactory.eINSTANCE.createSetConfigFuncExpr();
            setExpr.setType(routeTrainSchema.getFirst());
            setExpr.setProp(routeTrainSchema.getSecond());
            setExpr.setConfigExpr(((GrantRouteFuncExpr) expr).getRouteExpr());
            setExpr.setValueExpr(((GrantRouteFuncExpr) expr).getTrainExpr());

            return normalizeSetConfigFuncExpr(setExpr);
        }

        // is route/segment (not) available/occupied
        if (expr instanceof EvaluateFuncExpr) {
            var evalExpr = (EvaluateFuncExpr) expr;

            // get route train config and check if not null
            if (evalExpr.isRouteAvailable()) {

                // output:
                //  get route.train train_id == ""

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
                opExpr.setLeftExpr(normalizeBehaviourExpr(getExpr));
                opExpr.setOp(evalExpr.isNot() ? OperatorType.NOT_EQUAL : OperatorType.EQUAL);
                opExpr.setRightExpr(createString(""));
                return opExpr;
            } else if (evalExpr.isSegmentOccupied()) {
                // output:
                //  extern is_segment_occupied(segment_id)
                var rawExpr = createExternalFunctionCallExpr("is_segment_occupied", List.of(evalExpr.getObjectExpr()));
                if (!evalExpr.isNot()) {
                    return rawExpr;
                }

                var unaryExpr = BahnFactory.eINSTANCE.createUnaryExpr();
                unaryExpr.setExpr(rawExpr);
                return unaryExpr;
            }
        }

        return null;
    }

    private String convertTrackState(TrackState trackState) {
        switch (trackState) {
            case STOP:
                return "stop";
            case CAUTION:
                return "caution";
            case CLEAR:
                return "clear";
            case NORMAL:
                return "normal";
            case REVERSE:
                return "reverse";
        }

        return null;
    }

    private static String getArrayDeclName(Expression expr) {
        if (expr.eContainer() instanceof VariableAssignment) {
            if (expr.eContainer().eContainer() instanceof VarDeclStmt) {
                var varDeclStmt = (VarDeclStmt) expr.eContainer().eContainer();
                if (varDeclStmt.getDecl().isArray())
                    return varDeclStmt.getDecl().getName();
            }

            if (expr.eContainer().eContainer() instanceof AssignmentStmt) {
                var assignmentStmt = (AssignmentStmt) expr.eContainer().eContainer();
                if (assignmentStmt.getReferenceExpr().getDecl().isArray()) {
                    if (!assignmentStmt.getReferenceExpr().isLength() && assignmentStmt.getReferenceExpr().getIndexExpr() == null) {
                        return assignmentStmt.getReferenceExpr().getDecl().getName();
                    }
                }
            }
        }

        return null;
    }

    /**
     * create the extern get config function
     * sample:
     *  config_get_scalar_string_value("route", route_id, "length")
     *  config_get_array_string_value("route", route_id, "points", arr_points)
     * @param expr expr
     * @return expr
     */
    private ExternalFunctionCallExpr normalizeGetConfigFuncExpr(GetConfigFuncExpr expr, String lhsArrayName) {
        String funcName = getConfigFunctionName(expr.getProp(), true);
        Collection<Expression> params = new ArrayList<>();
        params.add(createString(expr.getType().getName()));
        params.add(expr.getConfigExpr());
        params.add(createString(expr.getProp().getName()));
        if (expr.getProp().isArray()) {
            params.add(arrayLookupTable.createArrayVarExpr(lhsArrayName));
        }
        return createExternalFunctionCallExpr(funcName, params);
    }

    /**
     * create the extern get config function
     * sample:
     *  config_set_scalar_string_value("route", route_id, "train", train_id)
     * @param expr expr
     * @return expr
     */
    private static ExternalFunctionCallExpr normalizeSetConfigFuncExpr(SetConfigFuncExpr expr) {
        String funcName = getConfigFunctionName(expr.getProp(), false);
        Collection<Expression> params = List.of(createString(expr.getType().getName()),
                expr.getConfigExpr(),
                createString(expr.getProp().getName()),
                expr.getValueExpr());
        return createExternalFunctionCallExpr(funcName, params);
    }

    /**
     * generate getter/setter function name
     * sample:
     *  config_get_scalar_string_value
     *  config_get_array_string_value
     * @param prop schema property
     * @param isGetter getter or setter function
     * @return name
     */
    private static String getConfigFunctionName(ElementProp prop, boolean isGetter) {
        String type = "";
        switch (prop.getType()) {
            case BOOLEAN_TYPE:
                type = "bool";
                break;
            case INT_TYPE:
                type = "int";
                break;
            case FLOAT_TYPE:
                type = "float";
                break;
            default:
                type = "string";
                break;
        }

        // config_get_scalar_float_value
        return String.format(EXTERN_CONFIG_FORMAT,
                isGetter ? "get" : "set",
                prop.isArray() ? "array" : "scalar",
                type);
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
