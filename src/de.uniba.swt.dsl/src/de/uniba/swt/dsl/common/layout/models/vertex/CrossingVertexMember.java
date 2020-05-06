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

import de.uniba.swt.dsl.bahn.CrossingElement;
import de.uniba.swt.dsl.common.util.BahnConstants;

public class CrossingVertexMember extends AbstractVertexMember {
    private final CrossingElement crossing;
    private final Endpoint endpoint;

    public CrossingVertexMember(CrossingElement crossing, String prop) {
        this.crossing = crossing;
        this.endpoint = convertToEndpoint(prop);
    }

    private static Endpoint convertToEndpoint(String prop) {
        switch (prop.toLowerCase()) {
            case BahnConstants.CROSSING_DOWN_1:
                return Endpoint.Down1;
            case BahnConstants.CROSSING_DOWN_2:
                return Endpoint.Down2;
            case BahnConstants.CROSSING_UP_1:
                return Endpoint.Up1;
            default:
                return Endpoint.Up2;
        }
    }

    public CrossingElement getCrossing() {
        return crossing;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public String getName() {
        return crossing.getName();
    }

    @Override
    public VertexMemberType getType() {
        return VertexMemberType.Crossing;
    }

    @Override
    public String getKey() {
        return generateKey(getEndpoint());
    }

    public Endpoint getConnectedEndpoint() {
        switch (getEndpoint()) {
            case Down1:
                return Endpoint.Up2;
            case Down2:
                return Endpoint.Up1;
            case Up1:
                return Endpoint.Down2;
            default:
                return Endpoint.Down1;
        }
    }

    public String generateKey(Endpoint endpoint) {
        return getName() + "." + endpoint.toString().toLowerCase();
    }

    public enum Endpoint {
        Down1,
        Down2,
        Up1,
        Up2
    }
}
