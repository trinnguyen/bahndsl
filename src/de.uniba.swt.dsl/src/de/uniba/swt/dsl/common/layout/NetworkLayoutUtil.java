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

package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.common.layout.models.BlockDirection;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.BlockVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.SignalVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;
import de.uniba.swt.dsl.common.layout.models.vertex.StandardSwitchVertexMember;

public class NetworkLayoutUtil {
    /**
     * check the direction of attached block, to see if the trains can leave at the endpoint
     * @param networkLayout
     * @param vertex
     * @param member
     * @return
     */
    public static boolean validateSignalDirection(NetworkLayout networkLayout, LayoutVertex vertex, SignalVertexMember member) {
        BlockDirection direction = networkLayout.getBlockDirection(member.getConnectedBlock().getName());
        if (direction != BlockDirection.Bidirectional) {
            var blockEndPoint = vertex.getMembers()
                    .stream()
                    .filter(m -> (m instanceof BlockVertexMember)
                            && ((BlockVertexMember) m).getBlock().equals(member.getConnectedBlock()))
                    .map(m -> ((BlockVertexMember) m).getEndpoint())
                    .findFirst().orElseThrow();
            return (direction == BlockDirection.DownUp && blockEndPoint == BlockVertexMember.BlockEndpoint.Up)
                    || (direction == BlockDirection.UpDown && blockEndPoint == BlockVertexMember.BlockEndpoint.Down);
        }

        return true;
    }
}
