package de.uniba.swt.dsl.validation;

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

    public EStructuralFeature getFeature() {
        return feature;
    }

    public void setFeature(EStructuralFeature feature) {
        this.feature = feature;
    }
}
