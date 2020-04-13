package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.DataType;
import de.uniba.swt.dsl.bahn.FuncDecl;
import de.uniba.swt.dsl.bahn.RefVarDecl;
import de.uniba.swt.dsl.common.generator.sccharts.StateTable;
import de.uniba.swt.dsl.common.generator.sccharts.models.RootState;
import de.uniba.swt.dsl.common.generator.sccharts.models.SuperState;
import de.uniba.swt.dsl.common.util.BahnUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class RootStateBuilder extends SuperStateBuilder {
    public RootStateBuilder(FuncDecl funcDecl) {
        super(new HashMap<>(), new Stack<>(), new StateTable("S_" + funcDecl.getName()), new RootState(funcDecl.getName()), funcDecl.getStmtList());

        // input
        if (funcDecl.getParamDecls() != null) {
            for (RefVarDecl paramDecl : funcDecl.getParamDecls()) {
                superState.getDeclarations().add(convertDeclaration(paramDecl.getType(), paramDecl.getName(), paramDecl.isArray() ? DEFAULT_PARAM_ARRAY_SIZE : 0, true, false));
            }
        }

        // output
        if (funcDecl.isReturn()) {
            superState.getDeclarations().add(convertDeclaration(funcDecl.getReturnType(), SCChartsUtil.VAR_OUTPUT_NAME, funcDecl.isReturnArray() ? DEFAULT_PARAM_ARRAY_SIZE : 0, false, true));
            superState.getDeclarations().add(convertDeclaration(DataType.BOOLEAN_TYPE, SCChartsUtil.VAR_HAS_RETURN_NAME, 0, false, false));
        }

        if (BahnUtil.hasBreakStmt(funcDecl.getStmtList())) {
            superState.getDeclarations().add(convertDeclaration(DataType.BOOLEAN_TYPE, SCChartsUtil.VAR_HAS_BREAK, 0, false, false));
        }
    }

    public RootState getRootState() {
        return (RootState) superState;
    }
}
