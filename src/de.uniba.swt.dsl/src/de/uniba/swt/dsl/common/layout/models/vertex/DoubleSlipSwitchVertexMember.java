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

import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.util.BahnConstants;

import java.util.Set;

public class DoubleSlipSwitchVertexMember extends AbstractSwitchVertexMember {

    public DoubleSlipSwitchVertexMember(PointElement point, String prop) {
        super(point);
        this.endpoint = convertToEndpoint(prop);
    }

    public enum Endpoint {
        Down1,
        Down2,
        Up1,
        Up2
    }

    private final Endpoint endpoint;

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.DoubleSlipSwitch;
    }

    @Override
    public String getKey() {
        return generateKey(getEndpoint());
    }

    public Set<Endpoint> getConnectedEndpoints() {
        return (getEndpoint() == Endpoint.Up1 || getEndpoint() == Endpoint.Up2)
                ? Set.of(Endpoint.Down1, Endpoint.Down2)
                : Set.of(Endpoint.Up1, Endpoint.Up2);
    }

    public String generateKey(Endpoint endpoint) {
        return getName() + "." + endpoint.toString().toLowerCase();
    }

    private static Endpoint convertToEndpoint(String prop) {
        switch (prop.toLowerCase()) {
            case BahnConstants.DOUBLE_SLIP_SWITCH_DOWN_1:
                return Endpoint.Down1;
            case BahnConstants.DOUBLE_SLIP_SWITCH_DOWN_2:
                return Endpoint.Down2;
            case BahnConstants.DOUBLE_SLIP_SWITCH_UP_1:
                return Endpoint.Up1;
            default:
                return Endpoint.Up2;
        }
    }
}
