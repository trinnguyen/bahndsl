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

import de.uniba.swt.dsl.common.layout.models.CompositeLayoutException;
import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.VertexMemberType;
import de.uniba.swt.dsl.common.layout.validators.connectors.*;
import de.uniba.swt.dsl.validation.ValidationErrors;

import java.util.*;

public class NetworkValidator {

    private final Set<String> cacheElements = new HashSet<>();
    private final GraphStrongConnectedChecker graphValidator = new GraphStrongConnectedChecker();
    private NetworkLayout networkLayout;
    private final Map<VertexMemberType, AbstractConnectorValidator> validators;

    public NetworkValidator() {
        validators = Map.of(
                VertexMemberType.Block, new BlockConnectorValidator(),
                VertexMemberType.StandardSwitch, new StandardSwitchConnectorValidator(),
                VertexMemberType.DoubleSlipSwitch, new DoubleSlipSwitchConnectorValidator(),
                VertexMemberType.Crossing, new CrossingConnectorValidator());
    }

    public void checkWelformness(NetworkLayout networkLayout) throws CompositeLayoutException {
        this.networkLayout = networkLayout;

        // 1. validate connectors
        validateConnectors();

        // 2. ensure all vertices are reachable
        if (!graphValidator.isStrongConnected(networkLayout))
            throw new CompositeLayoutException(ValidationErrors.NetworkNotValid);
    }

    private void validateConnectors() throws CompositeLayoutException {
        cacheElements.clear();
        List<LayoutException> errors = new ArrayList<>();
        for (var vertex : networkLayout.getVertices()) {
            for (var member : vertex.getMembers()) {

                if (cacheElements.contains(member.getName()))
                    continue;

                if (validators.containsKey(member.getType())) {
                    try {
                        validators.get(member.getType()).validate(networkLayout, member);
                    } catch (LayoutException exception) {
                        errors.add(exception);
                    }
                }

                cacheElements.add(member.getName());
            }
        }

        // throw errors
        if (errors.size() > 0) {
            throw new CompositeLayoutException(errors);
        }
    }
}
