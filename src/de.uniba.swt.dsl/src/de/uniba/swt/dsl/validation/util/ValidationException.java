package de.uniba.swt.dsl.validation.util;

import de.uniba.swt.dsl.validation.typing.ExprDataType;
import org.eclipse.emf.ecore.EStructuralFeature;

public class ValidationException extends Exception
{
    private EStructuralFeature feature;

    public ValidationException() {
    }

    public ValidationException(String message, EStructuralFeature feature) {
        super(message);
        this.feature = feature;
    }

    public static ValidationException createTypeException(ExprDataType expectedType, ExprDataType actualType, EStructuralFeature feature) {
        return createTypeException(expectedType.displayTypeName(), actualType.displayTypeName(), feature);
    }

    public static ValidationException createTypeException(String expectedType, String actualType, EStructuralFeature feature) {
        return new ValidationException(String.format("Type Error: Expected %s but actual %s", expectedType, actualType), feature);
    }

    public EStructuralFeature getFeature() {
        return feature;
    }

    public void setFeature(EStructuralFeature feature) {
        this.feature = feature;
    }
}
