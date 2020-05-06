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

package de.uniba.swt.dsl.common.layout.validators;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.validation.ValidationErrors;
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
                    throw new ValidationException(ValidationErrors.DirectionBlockOnly, BahnPackage.Literals.LAYOUT_ELEMENT__CONNECTORS);
                }

                if (!leftRef.getElem().equals(rightRef.getElem())) {
                    throw new ValidationException(ValidationErrors.DirectionSameBlock, BahnPackage.Literals.LAYOUT_ELEMENT__CONNECTORS);
                }

                if (!isBlockProp(leftRef.getProp().getLiteral())
                        || !isBlockProp(rightRef.getProp().getLiteral())
                        || leftRef.getProp().getLiteral().equalsIgnoreCase(rightRef.getProp().getLiteral())) {
                    var hint = String.join(",", BahnConstants.BLOCK_PROPS);
                    throw new ValidationException(ValidationErrors.InvalidConnectorsFormat, BahnPackage.Literals.LAYOUT_ELEMENT__CONNECTORS);
                }
            }
        }
    }

    private static boolean isBlockProp(String prop) {
        return BahnConstants.BLOCK_PROPS.contains(prop.toLowerCase());
    }

    public static void validateReference(LayoutReference reference) throws ValidationException {
        if (reference.getElem() instanceof SignalElement) {
            if (!reference.isNotSignal())
                return;

            throw new ValidationException(ValidationErrors.SignalConnectorNoAllowed, BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
        }

        String prop = reference.getProp().getLiteral();
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
            throw new ValidationException(ValidationErrors.MissingConnector, BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
        }

        if (allowedProps == null || allowedProps.isEmpty())
            throw new ValidationException(String.format(ValidationErrors.NotSupportedConnectorElementFormat, reference.getElem().getClass().getSimpleName()), BahnPackage.Literals.LAYOUT_REFERENCE__ELEM);

        if (!allowedProps.contains(prop)) {
            throw new ValidationException(String.format(ValidationErrors.InvalidConnectorsFormat, String.join(",", allowedProps)), BahnPackage.Literals.LAYOUT_REFERENCE__PROP);
        }
    }
}
