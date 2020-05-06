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

package de.uniba.swt.dsl.common.layout.validators.connectors;

import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.BlockVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.CrossingVertexMember;
import de.uniba.swt.dsl.validation.ValidationErrors;

import java.util.Objects;
import java.util.Set;

public class BlockConnectorValidator extends AbstractConnectorValidator {
    private final Set<BlockVertexMember.BlockEndpoint> endpoints = Set.of(
            BlockVertexMember.BlockEndpoint.Down,
            BlockVertexMember.BlockEndpoint.Up
    );

    @Override
    public void validate(NetworkLayout networkLayout, AbstractVertexMember member) throws LayoutException {
        if (!(member instanceof BlockVertexMember))
            return;
        BlockVertexMember blockMember = (BlockVertexMember) member;

        // find vertices
        var countItems = endpoints.stream()
                .map(e -> networkLayout.findVertex(blockMember.generateKey(e)))
                .filter(Objects::nonNull)
                .distinct().count();

        ensureEndpoints(member.getName(), 2, (int) countItems);
    }
}
