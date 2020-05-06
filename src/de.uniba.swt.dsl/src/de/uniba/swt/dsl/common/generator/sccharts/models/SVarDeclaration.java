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

package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.bahn.RefVarDecl;

public class SVarDeclaration {
    private final RefVarDecl varDecl;
    private final boolean isInput;
    private final boolean isOutput;
    private int cardinality;

    public SVarDeclaration(RefVarDecl varDecl, boolean isInput, boolean isOutput) {

        this.varDecl = varDecl;
        this.isInput = isInput;
        this.isOutput = isOutput;
    }

    public boolean isInput() {
        return isInput;
    }

    public boolean isOutput() {
        return isOutput;
    }

    public String getName() {
        return varDecl.getName();
    }

    public String formatDataType() {
        switch (varDecl.getType()) {
            case INT_TYPE:
                return "int";
            case FLOAT_TYPE:
                return "float";
            case BOOLEAN_TYPE:
                return "bool";
            case STRING_TYPE:
                return "string";
        }

        throw new RuntimeException("Data type is not supported: " + varDecl.getType());
    }

    public boolean isArray() {
        return varDecl.isArray();
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public int getCardinality() {
        return cardinality;
    }
}
