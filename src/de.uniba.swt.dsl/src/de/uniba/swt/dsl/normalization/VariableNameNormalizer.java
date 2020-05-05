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

import java.util.Collection;

public class VariableNameNormalizer extends AbstractNormalizer {

    @Override
    public void normalizeFunc(FuncDecl funcDecl) {
        for (ParamDecl paramDecl : funcDecl.getParamDecls()) {
            renameVariable(paramDecl);
        }
        super.normalizeFunc(funcDecl);
    }

    @Override
    protected Collection<Statement> normalizeStmt(Statement stmt) {
        if (stmt instanceof VarDeclStmt) {
            var varDeclStmt = (VarDeclStmt) stmt;
            renameVariable(varDeclStmt.getDecl());
        }
        return super.normalizeStmt(stmt);
    }

    @Override
    protected Collection<Statement> processExpr(Expression expr) {
        return null;
    }

    /**
     * Add underscore as prefix for all variable to prevent usage of SCCharts keywords
     * @param varDecl
     */
    private void renameVariable(RefVarDecl varDecl) {
        varDecl.setName("_" + varDecl.getName());
    }
}
