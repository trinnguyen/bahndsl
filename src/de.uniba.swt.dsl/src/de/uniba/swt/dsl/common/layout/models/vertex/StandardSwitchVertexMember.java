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

public class StandardSwitchVertexMember extends AbstractSwitchVertexMember {

    public enum Endpoint {
        Stem,
        Normal,
        Reverse
    }

    private Endpoint endpoint;

    public StandardSwitchVertexMember(PointElement point, String prop) {
        this(point, convertToPointEndpoint(prop));
    }

    public StandardSwitchVertexMember(PointElement point, Endpoint endpoint) {
        super(point);
        this.endpoint = endpoint;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.StandardSwitch;
    }

    @Override
    public String getKey() {
        return generateKey(getEndpoint());
    }

    public String generateKey(Endpoint endpoint) {
        return getName() + "." + endpoint.toString().toLowerCase();
    }

    private static Endpoint convertToPointEndpoint(String prop) {
        switch (prop.toLowerCase()) {
            case BahnConstants.STANDARD_SWITCH_STRAIGHT:
                return Endpoint.Normal;
            case BahnConstants.STANDARD_SWITCH_SIDE:
                return Endpoint.Reverse;
            default:
                return Endpoint.Stem;
        }
    }

    public Set<Endpoint> getConnectedEndpoints() {
        return this.getEndpoint() == Endpoint.Stem ?
                Set.of(Endpoint.Normal, Endpoint.Reverse) :
                Set.of(Endpoint.Stem);
    }
}
