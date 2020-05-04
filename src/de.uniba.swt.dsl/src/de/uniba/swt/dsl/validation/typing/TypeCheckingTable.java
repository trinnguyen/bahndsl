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

package de.uniba.swt.dsl.validation.typing;

import de.uniba.swt.dsl.bahn.*;
import org.apache.log4j.Logger;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class TypeCheckingTable {
    private static final Logger logger = Logger.getLogger(TypeCheckingTable.class);

    private final Map<Expression, ExprDataType> typeTable = new HashMap<>();

    public ExprDataType computeDataType(Expression expression) {
        return computeDataType(expression, HintDataType.NONE);
    }

    public ExprDataType computeDataType(Expression expression, HintDataType hintType) {
        if (hasDataType(expression))
            return lookup(expression);

        var dataType = TypeComputingHelper.computeExpr(expression, hintType);
        return insertToTable(expression, dataType);
    }

    private ExprDataType insertToTable(Expression expression, ExprDataType dataType) {
        typeTable.put(expression, dataType);
        return dataType;
    }

    public boolean isValidType(ExprDataType computedType, ExprDataType expectedType) {
        return computedType != null && computedType.equals(expectedType);
    }

    public void clear() {
        typeTable.clear();
    }

    public ExprDataType getDataType(VarDecl decl) {
        return TypeComputingHelper.getDataType(decl);
    }

    public boolean hasDataType(Expression expression) {
        return typeTable.containsKey(expression);
    }

    public ExprDataType lookup(Expression expr) {
        return typeTable.get(expr);
    }

    public boolean canComputeType(Expression expr) {
        return !(expr instanceof ExternalFunctionCallExpr);
    }
}
