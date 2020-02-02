package de.uniba.swt.dsl.validation.validators;

import de.uniba.swt.dsl.bahn.LayoutProperty;
import de.uniba.swt.dsl.common.layout.NetworkLayoutBuilder;
import de.uniba.swt.dsl.common.layout.models.CompositeLayoutException;
import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.validators.NetworkValidator;

import javax.inject.Singleton;
import java.util.List;

@Singleton
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
}
