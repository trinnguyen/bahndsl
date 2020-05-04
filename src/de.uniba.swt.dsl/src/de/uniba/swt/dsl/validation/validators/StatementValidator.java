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

package de.uniba.swt.dsl.validation.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import de.uniba.swt.dsl.validation.typing.HintDataType;
import de.uniba.swt.dsl.validation.typing.HintDataTypeUtl;
import de.uniba.swt.dsl.validation.typing.TypeCheckingTable;
import de.uniba.swt.dsl.validation.util.ValidationException;
import org.eclipse.emf.ecore.EStructuralFeature;

import javax.inject.Inject;

public class StatementValidator {

    @Inject
    TypeCheckingTable typeCheckingTable;

    public void validateAssignment(VariableAssignment assignment) throws ValidationException {
        var declType = getDeclDataType(assignment);
        if (declType == null)
            return;

        checkExprType(declType, assignment.getExpr(), HintDataTypeUtl.from(declType.getDataType()), BahnPackage.Literals.VARIABLE_ASSIGNMENT__EXPR);
    }

    private ExprDataType getDeclDataType(VariableAssignment assignment) {
        if (assignment.eContainer() instanceof VarDeclStmt) {
            return typeCheckingTable.getDataType(((VarDeclStmt) assignment.eContainer()).getDecl());
        }

        if (assignment.eContainer() instanceof AssignmentStmt) {
            return typeCheckingTable.computeDataType(((AssignmentStmt) assignment.eContainer()).getReferenceExpr());
        }

        return null;
    }

    private void checkExprType(ExprDataType declType, Expression expr, HintDataType hintDataType, EStructuralFeature feature) throws ValidationException {
        if (typeCheckingTable.canComputeType(expr)) {
            ExprDataType exprType = typeCheckingTable.computeDataType(expr, hintDataType);
            checkTypes(declType, exprType, feature);
        }
    }

    private void checkTypes(ExprDataType expected, ExprDataType actual, EStructuralFeature feature) throws ValidationException {
        if (!expected.equals(actual)) {
            throw new ValidationException(String.format("Type Error: Expected type %s, actual type: %s", expected.displayTypeName(), actual.displayTypeName()), feature);
        }
    }
}


