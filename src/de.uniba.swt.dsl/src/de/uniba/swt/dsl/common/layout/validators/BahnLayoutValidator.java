package de.uniba.swt.dsl.common.layout.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.*;
import de.uniba.swt.dsl.common.layout.models.CompositeLayoutException;
import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.validators.NetworkValidator;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.validation.util.ValidationException;

import java.util.List;

public class BahnLayoutValidator {

    private NetworkLayoutBuilder layoutBuilder;
    private NetworkValidator validator;

    public BahnLayoutValidator() {
        layoutBuilder = new NetworkLayoutBuilder();
        validator = new NetworkValidator();
    }

    public void validateLayout(LayoutProperty layoutProp) throws CompositeLayoutException {
    	if (layoutProp.getItems().isEmpty())
    		return;
    	
        // 1. Build vertices
        NetworkLayout networkLayout = null;
        try {
            networkLayout = layoutBuilder.build(layoutProp);
        } catch (LayoutException e) {
            throw new CompositeLayoutException(List.of(e));
        }

        // 2. Validation
        if (networkLayout != null && networkLayout.hasEdge())
            validator.checkWelformness(networkLayout);
    }

    public static void validateElement(LayoutElement element) throws ValidationException {
        LayoutElementValidator.validateElement(element);
    }

    public static void validateReference(LayoutReference reference) throws ValidationException {
        LayoutElementValidator.validateReference(reference);
    }
}
