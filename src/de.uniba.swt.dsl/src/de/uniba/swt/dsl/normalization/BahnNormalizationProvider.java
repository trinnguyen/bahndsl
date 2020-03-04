package de.uniba.swt.dsl.normalization;

import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.StringUtil;
import org.eclipse.xtext.EcoreUtil2;

import java.util.Collection;
import java.util.List;

@Singleton
public class BahnNormalizationProvider {

    private static final String EXTERN_TABLE_GET_ROUTES = "interlocking_table_get_routes";

    private static final String EXTERN_STATE_GETTER_NAME = "track_state_get_value";

    private static final String EXTERN_STATE_SETTER_NAME = "track_state_get_value";

    private static final String EXTERN_CONFIG_SET_SCALAR_STRING = "config_set_scalar_string_value";

    private static final String PROP_TRAIN = "train";

    private static final String TYPE_ROUTE = "route";

    private static final String TYPE_POINT = "point";

    private static final String TYPE_SIGNAL = "signal";

    private static final String TYPE_SEGMENT = "segment";

    public BahnNormalizationProvider() {
    }

    public void normalize(RootModule rootModule) {
        for (ModuleProperty property : rootModule.getProperties()) {
            if (property instanceof FuncDecl) {
                normalize(((FuncDecl) property).getStmtList());
            }
        }
    }

    private void normalize(StatementList stmtList) {
        for (Statement stmt : stmtList.getStmts()) {
            if (stmt instanceof SelectionStmt) {
                normalize(((SelectionStmt) stmt).getExpr());
                normalize(((SelectionStmt) stmt).getThenStmts());
                if (((SelectionStmt) stmt).getElseStmts() != null)
                    normalize(((SelectionStmt) stmt).getElseStmts());
                continue;
            }

            if (stmt instanceof IterationStmt) {
                normalize(((IterationStmt) stmt).getExpr());
                normalize(((IterationStmt) stmt).getStmts());
                continue;
            }

            if (stmt instanceof VarDeclStmt && ((VarDeclStmt) stmt).getAssignment() != null) {
                normalize(((VarDeclStmt) stmt).getAssignment());
                continue;
            }

            if (stmt instanceof AssignmentStmt) {
                normalize(((AssignmentStmt) stmt).getAssignment());
                continue;
            }

            if (stmt instanceof FunctionCallStmt) {
                normalize(((FunctionCallStmt) stmt).getExpr());
                continue;
            }

            if (stmt instanceof ReturnStmt) {
                normalize(((ReturnStmt) stmt).getExpr());
            }
        }
    }

    private void normalize(VariableAssignment assignment) {
        normalize(assignment.getExpr());
    }

    /**
     * TODO
     * Normalize syntactic sugar exprs
     * GetFuncExpr | WaitFuncExpr | GrantRouteFuncExpr | SetAspectFuncExpr
     * @param expr expression
     */
    private void normalize(Expression expr) {
        if (expr instanceof BehaviourExpr) {
            Expression replacementExpr = convertSyntacticExpr(expr);
            if (replacementExpr != null)
                EcoreUtil2.replace(expr, replacementExpr);
        }
    }

    private StringLiteral createString(String value) {
        var literal = BahnFactory.eINSTANCE.createStringLiteral();
        literal.setValue(value);
        return literal;
    }

    private Expression convertSyntacticExpr(Expression expr) {
        // getter
        if (expr instanceof BehaviourGetExpr) {
            BehaviourGetExpr getterExpr = (BehaviourGetExpr) expr;

            if (getterExpr.getGetExpr() instanceof GetTrackStateFuncExpr) {
                GetTrackStateFuncExpr trackStateFuncExpr = (GetTrackStateFuncExpr) getterExpr.getGetExpr();

                if (trackStateFuncExpr.isSignal()) {
                    return createExternalFunctionCallExpr(EXTERN_STATE_GETTER_NAME, List.of(createString(TYPE_SIGNAL), trackStateFuncExpr.getTrackExpr()));
                }

                if (trackStateFuncExpr.isPoint()) {
                    return createExternalFunctionCallExpr(EXTERN_STATE_GETTER_NAME, List.of(createString(TYPE_POINT), trackStateFuncExpr.getTrackExpr()));
                }

            }

            if (getterExpr.getGetExpr() instanceof GetRoutesFuncExpr) {
                GetRoutesFuncExpr routesExpr = (GetRoutesFuncExpr) getterExpr.getGetExpr();
                return createExternalFunctionCallExpr(EXTERN_TABLE_GET_ROUTES, List.of(routesExpr.getSrcSignalExpr(), routesExpr.getDestSignalExpr(), routesExpr.getBinding()));
            }

            if (getterExpr.getGetExpr() instanceof GetConfigFuncExpr) {

            }

            return null;
        }

        // setter
        if (expr instanceof BehaviourSetExpr) {
            BehaviourSetExpr setExpr = (BehaviourSetExpr) expr;
            if (setExpr.getSetExpr() instanceof SetTrackStateFuncExpr) {
                SetTrackStateFuncExpr setStateFunExpr = (SetTrackStateFuncExpr) setExpr.getSetExpr();
                if (setStateFunExpr.isSignal()) {
                    return createExternalFunctionCallExpr(EXTERN_STATE_SETTER_NAME, List.of(createString(TYPE_SIGNAL), setStateFunExpr.getTrackExpr()));
                }

                if (setStateFunExpr.isPoint()) {
                    return createExternalFunctionCallExpr(EXTERN_STATE_SETTER_NAME, List.of(createString(TYPE_POINT), setStateFunExpr.getTrackExpr()));
                }
            }

            return null;
        }

        if (expr instanceof WaitFuncExpr) {
            //TODO generate wait expr
            return expr;
        }

        if (expr instanceof GrantRouteFuncExpr) {
            GrantRouteFuncExpr grantRouteFuncExpr = (GrantRouteFuncExpr) expr;
            return createExternalFunctionCallExpr(EXTERN_CONFIG_SET_SCALAR_STRING, List.of(
                    createString(TYPE_ROUTE),
                    grantRouteFuncExpr.getRouteExpr(),
                    createString(PROP_TRAIN),
                    grantRouteFuncExpr.getTrainExpr()));
        }

        return null;
    }

    private static ExternalFunctionCallExpr createExternalFunctionCallExpr(String name, Collection<Expression> paramExprs) {
        var expr = BahnFactory.eINSTANCE.createExternalFunctionCallExpr();
        expr.setName(name);
        expr.getParams().addAll(paramExprs);
        return expr;
    }
}
