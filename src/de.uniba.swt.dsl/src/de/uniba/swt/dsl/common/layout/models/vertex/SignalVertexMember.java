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

package de.uniba.swt.dsl.common.layout.models.vertex;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.SignalElement;

public class SignalVertexMember extends AbstractVertexMember {

    public SignalVertexMember(SignalElement signal, BlockElement connectedBlock) {
        this.signal = signal;
        this.connectedBlock = connectedBlock;
    }

    private final SignalElement signal;
    private final BlockElement connectedBlock;

    public BlockElement getConnectedBlock() {
        return connectedBlock;
    }

    @Override
    public String getName() {
        return signal.getName();
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.Signal;
    }

    @Override
    public String getKey() {
        return getName();
    }
}
