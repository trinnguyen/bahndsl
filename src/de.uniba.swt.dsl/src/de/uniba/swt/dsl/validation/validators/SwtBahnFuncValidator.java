/*
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BahnDSL.  If not, see <https://www.gnu.org/licenses/>.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 */

package de.uniba.swt.dsl.validation.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

import java.util.List;

/**
 * Validate required functions used for SWTBahn platform
 */
public class SwtBahnFuncValidator {

    /**
     * Check whether request_route and drive_route are existed
     * @param bahnModel model
     */
    public Tuple<Boolean, Boolean> hasRequestAndDriveRoute(BahnModel bahnModel) {
        if (bahnModel == null)
            return Tuple.of(false, false);

        boolean hasRequestRoute = false;
        boolean hasDriveRoute = false;
        for (var c : bahnModel.getComponents()) {
            if (c instanceof FuncDecl) {
                FuncDecl decl = (FuncDecl) c;
                if (hasRequestRoute && hasDriveRoute)
                    break;

                if (!hasRequestRoute && isRequestRoute(decl)) {
                    hasRequestRoute = true;
                    continue;
                }

                if (!hasDriveRoute && isDriveRoute(decl))
                    hasDriveRoute = true;
            }
        }

        return Tuple.of(hasRequestRoute, hasDriveRoute);
    }

    public boolean isRequestRoute(FuncDecl funcDecl) {
        return match(generateRequestRouteFuncDecl(), funcDecl);
    }

    public boolean isDriveRoute(FuncDecl funcDecl) {
        return match(generateDriveRouteFuncDecl(), funcDecl);
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

    private FuncDecl generateRequestRouteFuncDecl() {
        var decl = BahnFactory.eINSTANCE.createFuncDecl();
        decl.setName(BahnConstants.REQUEST_ROUTE_FUNC_NAME);
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, false, "src_signal_id"));
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, false, "dst_signal_id"));
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, false, "train_id"));
        decl.setReturn(true);
        decl.setReturnType(DataType.STRING_TYPE);
        decl.setReturnArray(false);
        return decl;
    }

    public FuncDecl generateDriveRouteFuncDecl() {
        var decl = BahnFactory.eINSTANCE.createFuncDecl();
        decl.setName(BahnConstants.DRIVE_ROUTE_FUNC_NAME);
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, false, "route_id"));
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, false, "train_id"));
        decl.getParamDecls().add(createParam(DataType.STRING_TYPE, true, "segment_ids"));
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
