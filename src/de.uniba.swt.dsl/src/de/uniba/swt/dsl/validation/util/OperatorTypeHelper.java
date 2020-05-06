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

package de.uniba.swt.dsl.validation.util;

import de.uniba.swt.dsl.bahn.OperatorType;

public class OperatorTypeHelper {
    public static boolean isArithmeticOp(OperatorType op) {
        return op == OperatorType.PLUS
                || op == OperatorType.MINUS
                || op == OperatorType.MULTIPLY
                || op == OperatorType.DIVISION
                || op == OperatorType.MOD;
    }

    public static boolean isLogicalOp(OperatorType op) {
        return op == OperatorType.OR
                || op == OperatorType.AND;
    }

    public static boolean isEqualityOp(OperatorType op) {
        return op == OperatorType.EQUAL
                || op == OperatorType.NOT_EQUAL;
    }

    public static boolean isRelationalOp(OperatorType op) {
        return op == OperatorType.GREATER
                || op == OperatorType.LESS
                || op == OperatorType.GREATER_OR_EQUAL
                || op == OperatorType.LES_OR_EQUAL;
    }
}
