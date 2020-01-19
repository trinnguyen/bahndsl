package de.uniba.swt.dsl.common.generator.sccharts;

import de.uniba.swt.dsl.bahn.*;

public class SCChartsNormalizer {

    /**
     * Normalize the before generating SCCharts models
     *
     * Support:
     * 1. Use temporary variable assignment for function call
     *      var r = get_route()
     *      int a = 3 + get_max() => int tmp = get_max(); int a = 3 + tmp
     *      return get_max() => int tmp = get_max(); return tmp
     * 2. Convert VarDeclStmt with Expr into VarDecl without Expression and AssignmentStmt
     *
     * @param rootModule
     * @return
     */
    public RootModule normalizeModule(RootModule rootModule) {
        for (var p : rootModule.getProperties()) {
            if (p instanceof FuncDecl) {
//                normalizeFuncDecl((FuncDecl)p);
            }
        }

        return rootModule;
    }
}
