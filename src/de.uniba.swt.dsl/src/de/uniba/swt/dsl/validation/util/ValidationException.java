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

import de.uniba.swt.dsl.validation.ValidationErrors;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class ValidationException extends Exception
{
    private EStructuralFeature feature;
    private int index = -1;

    public ValidationException(String message, EStructuralFeature feature, int index) {
        super(message);
        this.feature = feature;
        this.index = index;
    }

    public ValidationException(String message, EStructuralFeature feature) {
        this(message, feature, -1);
    }

    public static ValidationException createTypeException(ExprDataType expectedType, ExprDataType actualType, EStructuralFeature feature) {
        return createTypeException(expectedType.displayTypeName(), actualType.displayTypeName(), feature);
    }

    public static ValidationException createTypeException(String expectedType, String actualType, EStructuralFeature feature) {
        return new ValidationException(ValidationErrors.createTypeErrorMsg(expectedType, actualType), feature);
    }

    public EStructuralFeature getFeature() {
        return feature;
    }

    public void setFeature(EStructuralFeature feature) {
        this.feature = feature;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
