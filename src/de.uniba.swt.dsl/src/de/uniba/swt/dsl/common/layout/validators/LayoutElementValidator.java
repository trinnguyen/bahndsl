package de.uniba.swt.dsl.common.layout.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.validation.util.ValidationException;

class LayoutElementValidator {
    public static void validateElement(LayoutElement element) throws ValidationException {
        for (int i = 0; i < element.getConnectors().size(); i++) {
            var isDirected = BahnConstants.CONNECTOR_DIRECTED.equalsIgnoreCase(element.getConnectors().get(i));
            if (isDirected) {
                // prev element
                LayoutReference leftRef = element.getBlocks().get(i);
                LayoutReference rightRef = element.getBlocks().get(i+1);

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
        if (reference.getElem() instanceof SignalElement) {
            if (reference.getProp() == null)
                return;

            throw new ValidationException("Signal should have no connector property", BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
        }

        if (reference.getElem() instanceof PointElement) {
            var prop = reference.getProp();
            if (prop == null || prop.isEmpty()) {
                throw new ValidationException("Missing connector property", BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
            }

            prop = prop.toLowerCase();
            if (!BahnConstants.STANDARD_SWITCH_PROPS.contains(prop)
                    && !BahnConstants.DOUBLE_SLIP_SWITCH_PROPS.contains(prop)) {
                var hint = String.join(",", BahnConstants.STANDARD_SWITCH_PROPS)
                        + " or "
                        + String.join(",", BahnConstants.DOUBLE_SLIP_SWITCH_PROPS);
                throw new ValidationException("Invalid connector property, use: " + hint, BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
            }

            return;
        }

        if (reference.getElem() instanceof BlockElement) {
            // only a subset of property is allowed
            var prop = reference.getProp();
            if (prop == null || prop.isEmpty()) {
                throw new ValidationException("Missing connector property", BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
            }

            prop = prop.toLowerCase();
            if (!BahnConstants.BLOCK_PROPS.contains(prop)) {
                var hint = String.join(",", BahnConstants.BLOCK_PROPS);
                throw new ValidationException("Invalid connector property, use: " + hint, BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
            }

            return;
        }

        throw new ValidationException("Connector is allowed for signal, point, block and platform only, unexpected " + reference.getElem().getClass().getSimpleName(), BahnPackage.Literals.LAYOUT_REFERENCE__ELEM);
    }
}
