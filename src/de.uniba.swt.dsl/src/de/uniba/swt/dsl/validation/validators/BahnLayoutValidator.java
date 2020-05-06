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

    private final NetworkLayoutBuilder layoutBuilder;
    private final NetworkValidator validator;

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
