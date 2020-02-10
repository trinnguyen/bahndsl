package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.FuncDecl;
import de.uniba.swt.dsl.bahn.RefVarDecl;
import de.uniba.swt.dsl.common.generator.sccharts.StateTable;
import de.uniba.swt.dsl.common.generator.sccharts.models.RootState;
import de.uniba.swt.dsl.common.generator.sccharts.models.SuperState;

import java.util.Map;
import java.util.Stack;

public class RootStateBuilder extends SuperStateBuilder {
    public RootStateBuilder(Map<FuncDecl, RootState> mapFuncState, Stack<SuperState> superStates, FuncDecl funcDecl) {
        super(mapFuncState, superStates, new StateTable(), new RootState(funcDecl.getName()), funcDecl.getStmtList());

        // input
        if (funcDecl.getParamDecls() != null) {
            for (RefVarDecl paramDecl : funcDecl.getParamDecls()) {
                superState.getDeclarations().add(convertDeclaration(paramDecl.getType(), paramDecl.getName(), paramDecl.isArray() ? DEFAULT_PARAM_ARRAY_SIZE : 0, true, false));
            }
        }

        // output
        if (funcDecl.isReturn()) {
            superState.getDeclarations().add(convertDeclaration(funcDecl.getReturnType(), VAR_OUTPUT_NAME, funcDecl.isReturnArray() ? DEFAULT_PARAM_ARRAY_SIZE : 0, false, true));
        }
    }

    public RootState getRootState() {
        return (RootState) superState;
    }
}
