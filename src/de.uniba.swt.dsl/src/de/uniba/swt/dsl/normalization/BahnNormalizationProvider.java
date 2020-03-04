package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.StringUtil;
import org.eclipse.xtext.EcoreUtil2;

import java.util.Collection;
import java.util.List;

@Singleton
public class BahnNormalizationProvider {

    @Inject
    SyntacticSugarNormalizer syntacticSugarNormalizer;

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

    private void normalize(Expression expr) {
        if (expr instanceof BehaviourExpr) {
            Expression replacementExpr = syntacticSugarNormalizer.normalizeBehaviourExpr((BehaviourExpr)expr);
            if (replacementExpr != null)
                EcoreUtil2.replace(expr, replacementExpr);
        }
    }
}
