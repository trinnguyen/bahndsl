package de.uniba.swt.dsl.common.layout.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.validation.util.ValidationException;

import java.util.HashSet;
import java.util.Set;

public class LayoutElementValidator {
    public static void validateElement(LayoutElement element) throws ValidationException {
        for (int i = 0; i < element.getConnectors().size(); i++) {
            var isDirected = BahnConstants.CONNECTOR_DIRECTED.equalsIgnoreCase(element.getConnectors().get(i));
            if (isDirected) {
                // prev element
                LayoutReference leftRef = element.getBlocks().get(i);
                LayoutReference rightRef = element.getBlocks().get(i + 1);

                if (!(leftRef.getElem() instanceof BlockElement)
                        || !(rightRef.getElem() instanceof BlockElement)) {
                    throw new ValidationException("Direction is allowed for block only", BahnPackage.Literals.LAYOUT_ELEMENT__CONNECTORS);
                }

                if (!leftRef.getElem().equals(rightRef.getElem())) {
                    throw new ValidationException("Direction must be configured on the same block", BahnPackage.Literals.LAYOUT_ELEMENT__CONNECTORS);
                }

                if (!isBlockProp(leftRef.getProp())
                        || !isBlockProp(rightRef.getProp())
                        || leftRef.getProp().equalsIgnoreCase(rightRef.getProp())) {
                    var hint = String.join(",", BahnConstants.BLOCK_PROPS);
                    throw new ValidationException("Invalid connector property, use: " + hint, BahnPackage.Literals.LAYOUT_ELEMENT__CONNECTORS);
                }
            }
        }
    }

    private static boolean isBlockProp(String prop) {
        return BahnConstants.BLOCK_PROPS.contains(prop.toLowerCase());
    }

    public static void validateReference(LayoutReference reference) throws ValidationException {
        var prop = reference.getProp();
        if (reference.getElem() instanceof SignalElement) {
            if (prop == null)
                return;

            throw new ValidationException("Signal should have no connector property", BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
        }

        Set<String> allowedProps = null;
        if (reference.getElem() instanceof PointElement) {
            allowedProps = new HashSet<>();
            allowedProps.addAll(BahnConstants.STANDARD_SWITCH_PROPS);
            allowedProps.addAll(BahnConstants.DOUBLE_SLIP_SWITCH_PROPS);
        } else if (reference.getElem() instanceof BlockElement) {
            allowedProps = BahnConstants.BLOCK_PROPS;
        } else if (reference.getElem() instanceof CrossingElement) {
            allowedProps = BahnConstants.CROSSING_PROPS;
        }

        // validate
        if (prop == null || prop.isEmpty()) {
            throw new ValidationException("Missing connector property", BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
        }

        if (allowedProps == null || allowedProps.isEmpty())
            throw new ValidationException("Connector is allowed for signal, point, block, platform, crossing, unexpected " + reference.getElem().getClass().getSimpleName(), BahnPackage.Literals.LAYOUT_REFERENCE__ELEM);

        if (!allowedProps.contains(prop)) {
            throw new ValidationException("Invalid connector property, use: " + String.join(",", allowedProps), BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
        }
    }
}
