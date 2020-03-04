package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyntacticSugarNormalizer {

    @Inject
    private TemporaryVarGenerator tempVarGenerator;

    private static final String EXTERN_TABLE_GET_ROUTES = "interlocking_table_get_routes";

    private static final String EXTERN_STATE_GETTER_NAME = "track_state_get_value";

    private static final String EXTERN_STATE_SETTER_NAME = "track_state_set_value";

    private static final String EXTERN_CONFIG_FORMAT = "config_%s_%s_%s_value";

    private static final String TYPE_POINT = "point";

    private static final String TYPE_SIGNAL = "signal";

    public OpExpression normalizeBehaviourExpr(BehaviourExpr expr) {

        // clear
        tempVarGenerator.reset();

        // getter
        if (expr instanceof BehaviourGetExpr) {
            var getter = ((BehaviourGetExpr) expr).getGetExpr();

            if (getter instanceof GetConfigFuncExpr) {
                return normalizeGetConfigFuncExpr((GetConfigFuncExpr) getter);
            }

            if (getter instanceof GetRoutesFuncExpr) {
                GetRoutesFuncExpr routesExpr = (GetRoutesFuncExpr) getter;
                return createExternalFunctionCallExpr(EXTERN_TABLE_GET_ROUTES, List.of(routesExpr.getSrcSignalExpr(), routesExpr.getDestSignalExpr(), routesExpr.getBinding()));
            }

            if (getter instanceof GetTrackStateFuncExpr) {
                var trackStateFuncExpr = (GetTrackStateFuncExpr) getter;

                if (trackStateFuncExpr.isSignal()) {
                    return createExternalFunctionCallExpr(EXTERN_STATE_GETTER_NAME, List.of(createString(TYPE_SIGNAL), trackStateFuncExpr.getTrackExpr()));
                }

                if (trackStateFuncExpr.isPoint()) {
                    return createExternalFunctionCallExpr(EXTERN_STATE_GETTER_NAME, List.of(createString(TYPE_POINT), trackStateFuncExpr.getTrackExpr()));
                }

            }

            return null;
        }

        // setter
        if (expr instanceof BehaviourSetExpr) {
            var setter = ((BehaviourSetExpr) expr).getSetExpr();

            if (setter instanceof SetConfigFuncExpr) {
                return normalizeSetConfigFuncExpr((SetConfigFuncExpr)setter);
            }

            if (setter instanceof SetTrackStateFuncExpr) {
                var setStateFunExpr = (SetTrackStateFuncExpr) setter;
                if (setStateFunExpr.isSignal()) {
                    return createExternalFunctionCallExpr(EXTERN_STATE_SETTER_NAME, List.of(createString(TYPE_SIGNAL), setStateFunExpr.getTrackExpr(), setStateFunExpr.getAspectExpr()));
                }

                if (setStateFunExpr.isPoint()) {
                    String raw = setStateFunExpr.getPointAspectType().getName().toLowerCase();
                    return createExternalFunctionCallExpr(EXTERN_STATE_SETTER_NAME, List.of(createString(TYPE_POINT), setStateFunExpr.getTrackExpr(), createString(raw)));
                }
            }

            return null;
        }

        // wait
        if (expr instanceof WaitFuncExpr) {
            //TODO generate wait expr
            return expr;
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

    /**
     * create the extern get config function
     * sample:
     *  config_get_scalar_string_value("route", route_id, "length")
     *  config_get_array_string_value("route", route_id, "points", arr_points)
     * @param expr expr
     * @return expr
     */
    private ExternalFunctionCallExpr normalizeGetConfigFuncExpr(GetConfigFuncExpr expr) {
        String funcName = getConfigFunctionName(expr.getProp(), true);
        Collection<Expression> params = new ArrayList<>();
        params.add(createString(expr.getType().getName()));
        params.add(expr.getConfigExpr());
        params.add(createString(expr.getProp().getName()));
        return createExternalFunctionCallExpr(funcName, params);
    }

    /**
     * create the extern get config function
     * sample:
     *  config_set_scalar_string_value("route", route_id, "train", train_id)
     * @param expr expr
     * @return expr
     */
    private ExternalFunctionCallExpr normalizeSetConfigFuncExpr(SetConfigFuncExpr expr) {
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
    private String getConfigFunctionName(ElementProp prop, boolean isGetter) {
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

    private static ExternalFunctionCallExpr createExternalFunctionCallExpr(String name, Collection<Expression> paramExprs) {
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

    private Tuple<SchemaElement, ElementProp> createRouteTrainSchema() {
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
