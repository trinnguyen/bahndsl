package de.uniba.swt.dsl.validation.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.*;
import de.uniba.swt.dsl.common.layout.models.CompositeLayoutException;
import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.validators.NetworkValidator;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.validation.util.ValidationException;

import java.util.List;

public class LayoutValidator {

    private NetworkLayoutBuilder layoutBuilder;
    private NetworkValidator validator;

    public LayoutValidator() {
        layoutBuilder = new NetworkLayoutBuilder();
        validator = new NetworkValidator();
    }

    public void validateLayout(LayoutProperty layoutProp) throws CompositeLayoutException {
        // 1. Build vertices
        NetworkLayout networkLayout = null;
        try {
            networkLayout = layoutBuilder.build(layoutProp);
        } catch (LayoutException e) {
            throw new CompositeLayoutException(List.of(e));
        }

        // 2. Validation
        validator.checkWelformness(networkLayout);
    }

    public static void validateReference(LayoutReference reference) throws ValidationException {
        if (reference.getElem() instanceof SignalElement) {
            if (reference.getProp() == null)
                return;

            throw new ValidationException("Signal should have no connector property", BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
        }

        if (reference.getElem() instanceof BlockElement) {
            // only a subset of property is allowed
            var prop = reference.getProp();
            if (prop == null || prop.isEmpty()) {
                throw new ValidationException("Missing connector property", BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
            }

            prop = prop.toLowerCase();
            if (!BahnConstants.BLOCK_PROPS.contains(prop)
                    && !BahnConstants.SWITCH_PROPS.contains(prop)
                    && !BahnConstants.CROSSING_PROPS.contains(prop)) {
                var hint = String.join(",", BahnConstants.BLOCK_PROPS)
                        + " or "
                        + String.join(",", BahnConstants.SWITCH_PROPS)
                        + " or "
                        + String.join(",", BahnConstants.CROSSING_PROPS);
                throw new ValidationException("Invalid connector property, use: " + hint, BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
            }

            return;
        }

        throw new ValidationException("Connector is allowed for signal and block only, unexpected " + reference.getElem().getClass().getSimpleName(), BahnPackage.Literals.LAYOUT_REFERENCE__ELEM);
    }
}
