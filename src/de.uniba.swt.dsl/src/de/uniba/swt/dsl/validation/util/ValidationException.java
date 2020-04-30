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
        return new ValidationException(String.format(ValidationErrors.TypeErrorFormat, expectedType, actualType), feature);
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
