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
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

/**
 * Validate required functions used for SWTBahn platform
 */
public class SwtBahnFuncValidator {

    private static String varNamePrefix(boolean normalized) {
        return normalized ? "_" : "";
    }

    /**
     * Check whether request_route and drive_route are existed
     * @param bahnModel model
     */
    public static Tuple<Boolean, Boolean> hasRequestAndDriveRoute(BahnModel bahnModel, boolean normalized) {
        if (bahnModel == null)
            return Tuple.of(false, false);

        boolean hasRequestRoute = false;
        boolean hasDriveRoute = false;
        for (var c : bahnModel.getComponents()) {
            if (c instanceof FuncDecl) {
                FuncDecl decl = (FuncDecl) c;
                if (hasRequestRoute && hasDriveRoute)
                    break;

                if (!hasRequestRoute && isRequestRoute(decl, normalized)) {
                    hasRequestRoute = true;
                    continue;
                }

                if (!hasDriveRoute && isDriveRoute(decl, normalized))
                    hasDriveRoute = true;
            }
        }

        return Tuple.of(hasRequestRoute, hasDriveRoute);
    }

    public static boolean isRequestRoute(FuncDecl funcDecl, boolean normalized) {
        return match(generateRequestRouteFuncDecl(normalized), funcDecl);
    }

    public static boolean isDriveRoute(FuncDecl funcDecl, boolean normalized) {
        return match(generateDriveRouteFuncDecl(normalized), funcDecl);
    }

    private static boolean match(FuncDecl expected, FuncDecl actual) {
        if (!expected.getName().equals(actual.getName()))
            return false;

        if (!createType(expected).equals(createType(actual)))
            return false;

        if (expected.getParamDecls().size() != actual.getParamDecls().size())
            return false;

        for (int i = 0; i < expected.getParamDecls().size(); i++) {
            var expectedParam = expected.getParamDecls().get(i);
            var actualParam = actual.getParamDecls().get(i);

            if (!expectedParam.getName().equals(actualParam.getName()))
                return false;

            var expectedType = new ExprDataType(expectedParam.getType(), expectedParam.isArray());
            var actualType = new ExprDataType(actualParam.getType(), actualParam.isArray());
            if (!expectedType.equals(actualType))
                return false;
        }

        return true;
    }

    private static FuncDecl generateRequestRouteFuncDecl(boolean normalized) {
        var decl = BahnFactory.eINSTANCE.createFuncDecl();
        decl.setName(BahnConstants.REQUEST_ROUTE_FUNC_NAME);
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, false, varNamePrefix(normalized) + "src_signal_id"));
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, false, varNamePrefix(normalized) + "dst_signal_id"));
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, false, varNamePrefix(normalized) + "train_id"));
        decl.setReturn(true);
        decl.setReturnType(DataType.STRING_TYPE);
        decl.setReturnArray(false);
        return decl;
    }

    public static FuncDecl generateDriveRouteFuncDecl(boolean normalized) {
        var decl = BahnFactory.eINSTANCE.createFuncDecl();
        decl.setName(BahnConstants.DRIVE_ROUTE_FUNC_NAME);
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, false, varNamePrefix(normalized) + "route_id"));
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, false, varNamePrefix(normalized) + "train_id"));
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, true, varNamePrefix(normalized) + "segment_ids"));
        if (normalized) {
            decl.getParamDecls().add(createParam(DataType.INT_TYPE, false, varNamePrefix(normalized) + "_segment_ids_cnt"));
        }
        decl.setStmtList(BahnFactory.eINSTANCE.createStatementList());
        decl.setReturn(false);
        decl.setReturnArray(false);
        return decl;
    }

    private static ExprDataType createType(FuncDecl decl) {
        if (!decl.isReturn())
            return ExprDataType.Void;

        return new ExprDataType(decl.getReturnType(), decl.isReturnArray());
    }

    private static ParamDecl createParam(DataType dataType, boolean isArray, String name) {
        var paramDecl = BahnFactory.eINSTANCE.createParamDecl();
        paramDecl.setName(name);
        paramDecl.setType(dataType);
        paramDecl.setArray(isArray);
        return paramDecl;
    }
}
