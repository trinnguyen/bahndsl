package de.uniba.swt.dsl.normalization;

import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.*;
import org.eclipse.xtext.EcoreUtil2;

import java.util.Collection;
import java.util.List;

@Singleton
public class BahnNormalizationProvider {

    private FuncDecl getSignalAspectFuncDecl;

    private FuncDecl getPointAspectFuncDecl;

    private FuncDecl setSignalAspectFuncDecl;

    private FuncDecl setPointAspectFuncDecl;

    private FuncDecl getRouteFuncDecl;

    public BahnNormalizationProvider() {
        getSignalAspectFuncDecl = createFuncDecl("get_signal_aspect",
                List.of(createVarParam("inSignal", DataType.OBJECT_TYPE, false)));

        getPointAspectFuncDecl = createFuncDecl("get_point_aspect",
                List.of(createVarParam("inPoint", DataType.OBJECT_TYPE, false)));

        setSignalAspectFuncDecl = createFuncDecl("set_signal_aspect",
                List.of(createVarParam("inSignal", DataType.OBJECT_TYPE, false)));

        setPointAspectFuncDecl = createFuncDecl("set_point_aspect",
                List.of(createVarParam("inPoint", DataType.OBJECT_TYPE, false)));

        getRouteFuncDecl = createFuncDecl("get_route_from_to",
                List.of(createVarParam("inSignalFrom", DataType.OBJECT_TYPE, false),
                        createVarParam("inSignalTo", DataType.OBJECT_TYPE, false)));
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
                normalize(((SelectionStmt) stmt).getElseStmts());
                continue;
            }

            if (stmt instanceof IterationStmt) {
                normalize(((IterationStmt) stmt).getReferenceExpr());
                normalize(((IterationStmt) stmt).getStmts());
                continue;
            }

            if (stmt instanceof VarDeclStmt) {
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

    private Expression convertSyntacticExpr(Expression expr) {
        if (expr instanceof GetFuncExpr) {
            // get signal sig1 -> get_signal_aspect(sig1)
            // get point point1 -> get_point_aspect(point1)
            // get route from sig1 to sig2 -> get_route_from_to(sig1, sign2)
            GetFuncExpr getFuncExpr = (GetFuncExpr)expr;

            if (getFuncExpr.isPoint()) {
                return createFuncCallExpr(getPointAspectFuncDecl, List.of(getFuncExpr.getExpr()));
            }

            if (getFuncExpr.isSignal()) {
                return createFuncCallExpr(getSignalAspectFuncDecl, List.of(getFuncExpr.getExpr()));
            }

            if (getFuncExpr.isRoute()) {
                return createFuncCallExpr(getRouteFuncDecl, List.of(getFuncExpr.getSrcSignalExpr(), getFuncExpr.getDestSignalExpr()));
            }

            return null;
        }

        if (expr instanceof SetAspectFuncExpr) {
            SetAspectFuncExpr setAspectFuncExpr = (SetAspectFuncExpr) expr;
            if (setAspectFuncExpr.isPoint()) {
                return createFuncCallExpr(setPointAspectFuncDecl, List.of(setAspectFuncExpr.getExpr()));
            }

            if (setAspectFuncExpr.isSignal()) {
                return createFuncCallExpr(setSignalAspectFuncDecl, List.of(setAspectFuncExpr.getExpr()));
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

    private static FunctionCallExpr createFuncCallExpr(FuncDecl funcDecl, Collection<Expression> paramExprs) {
        var expr = BahnFactory.eINSTANCE.createFunctionCallExpr();
        expr.setDecl(funcDecl);
        expr.getParams().addAll(paramExprs);
        return expr;
    }

    private static ParamDecl createVarParam(String name, DataType dataType, boolean isArray) {
        var decl = BahnFactory.eINSTANCE.createParamDecl();
        decl.setName(name);
        decl.setType(dataType);
        decl.setArray(isArray);
        return decl;
    }

    private static FuncDecl createFuncDecl(String name, Collection<ParamDecl> paramDecls) {
        var decl = BahnFactory.eINSTANCE.createFuncDecl();
        decl.setName(name);
        decl.getParamDecls().addAll(paramDecls);
        return decl;
    }
}
