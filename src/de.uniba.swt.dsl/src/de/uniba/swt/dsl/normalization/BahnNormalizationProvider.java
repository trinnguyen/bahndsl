package de.uniba.swt.dsl.normalization;

import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.*;
import org.eclipse.xtext.EcoreUtil2;

import java.util.Collection;
import java.util.List;

@Singleton
public class BahnNormalizationProvider {

    private static final String EXTERN_TABLE_GET_ROUTES = "interlocking_table_get_routes";

    private static final String EXTERN_STATE_GETTER_NAME = "track_state_get_value";

    private static final String EXTERN_STATE_SETTER_NAME = "track_state_get_value";

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
                normalize(((IterationStmt) stmt).getReferenceExpr());
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
        if (expr instanceof GetFuncExpr) {
            // get signal sig1 -> get_signal_aspect(sig1)
            // get point point1 -> get_point_aspect(point1)
            // get routes from sig1 to sig2 -> get_route_from_to(sig1, sign2)
            GetFuncExpr getFuncExpr = (GetFuncExpr)expr;

            if (getFuncExpr.isPoint()) {
                return createExternalFunctionCallExpr(EXTERN_STATE_GETTER_NAME, List.of(createString(TYPE_POINT), getFuncExpr.getExpr()));
            }

            if (getFuncExpr.isSignal()) {
                return createExternalFunctionCallExpr(EXTERN_STATE_GETTER_NAME, List.of(createString(TYPE_SIGNAL), getFuncExpr.getExpr()));
            }

            if (getFuncExpr.isRoute()) {
                return createExternalFunctionCallExpr(EXTERN_TABLE_GET_ROUTES, List.of(getFuncExpr.getSrcSignalExpr(), getFuncExpr.getDestSignalExpr()));
            }

            return null;
        }

        // setter
        if (expr instanceof SetAspectFuncExpr) {
            SetAspectFuncExpr setAspectFuncExpr = (SetAspectFuncExpr) expr;
            if (setAspectFuncExpr.isPoint()) {
                return createExternalFunctionCallExpr(EXTERN_STATE_SETTER_NAME, List.of(createString(TYPE_POINT), setAspectFuncExpr.getExpr()));
            }

            if (setAspectFuncExpr.isSignal()) {
                return createExternalFunctionCallExpr(EXTERN_STATE_SETTER_NAME, List.of(createString(TYPE_SIGNAL), setAspectFuncExpr.getExpr()));
            }
        }

        if (expr instanceof WaitFuncExpr) {
            //TODO generate wait expr
            return expr;
        }

        if (expr instanceof GrantRouteFuncExpr) {
            //TODO generate route.train = train
            return expr;
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
