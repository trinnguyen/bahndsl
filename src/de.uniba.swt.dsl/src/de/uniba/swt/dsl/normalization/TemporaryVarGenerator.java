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

import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.BahnFactory;
import de.uniba.swt.dsl.bahn.Expression;
import de.uniba.swt.dsl.bahn.VarDecl;
import de.uniba.swt.dsl.bahn.VarDeclStmt;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

@Singleton
public class TemporaryVarGenerator {

    private String functionName = "";

    private int counter = 1;

    public void resetFunc(String name) {
        functionName = name.toLowerCase();
        counter = 1;
    }

    public VarDeclStmt createTempVarStmt(ExprDataType dataType) {
        String name = nextTempVarName();
        return BahnUtil.createVarDeclStmt(name, dataType, null);
    }

    public String nextTempVarName() {
        String prefix = "t";
        return String.format("_%s_%s%d", functionName, prefix, counter++);
    }
}
