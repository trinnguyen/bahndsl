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

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.models.BlockDirection;
import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.*;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.validation.ValidationErrors;

import java.util.List;

public class NetworkLayoutBuilder {

    public NetworkLayout build(LayoutProperty layoutProp) throws LayoutException {
        NetworkLayout networkLayout = new NetworkLayout();

        // generate
        for (var layoutElement : layoutProp.getItems()) {
            if (layoutElement.getBlocks().size() > 1) {
                int i = 0;
                while (i < layoutElement.getBlocks().size() - 1) {
                    boolean isDirected = BahnConstants.CONNECTOR_DIRECTED.equalsIgnoreCase(layoutElement.getConnectors().get(i));

                    // convert to members
                    var members = convertToVertexMembers(layoutElement.getBlocks().get(i),
                            layoutElement.getBlocks().get(++i));

                    // check if all are same block, update direction and skip
                    if (updateBlockDirection(networkLayout, members, isDirected))
                        continue;

                    // find or add to vertex
                    LayoutVertex vertex = null;
                    for (AbstractVertexMember member : members) {
                        vertex = networkLayout.findVertex(member);
                        if (vertex != null)
                            break;
                    }
                    if (vertex == null) {
                        vertex = networkLayout.addNewVertex();
                    }
                    networkLayout.addMembersToVertex(members, vertex);
                }
            }
        }

        // add missing vertex for block
        networkLayout.addMissingBlockVertices();
        return networkLayout;
    }

    private List<AbstractVertexMember> convertToVertexMembers(LayoutReference firstRef, LayoutReference secondRef) throws LayoutException {
        if (isSignal(firstRef)) {
            if (!isBlock(secondRef)) {
                throw new LayoutException(ValidationErrors.SingleToBlockOnly);
            }
            var signalMember = new SignalVertexMember((SignalElement) firstRef.getElem(), (BlockElement)secondRef.getElem());
            var blockMember = new BlockVertexMember((BlockElement)secondRef.getElem(), secondRef.getProp().getLiteral());
            return List.of(signalMember, blockMember);
        }

        if (isSignal(secondRef)) {
            if (!isBlock(firstRef)) {
                throw new LayoutException(ValidationErrors.SingleToBlockOnly);
            }
            var signalMember = new SignalVertexMember((SignalElement) secondRef.getElem(), (BlockElement)firstRef.getElem());
            var blockMember = new BlockVertexMember((BlockElement)firstRef.getElem(), firstRef.getProp().getLiteral());
            return List.of(blockMember, signalMember);
        }

        return List.of(convertToVertexMember(firstRef), convertToVertexMember(secondRef));
    }

    private boolean updateBlockDirection(NetworkLayout networkLayout, List<AbstractVertexMember> members, boolean isDirected) throws LayoutException {
        if (members.stream().anyMatch(m -> !(m instanceof BlockVertexMember)))
            return false;

        if (members.size() < 2)
            return false;

        BlockVertexMember firstBlockMem = (BlockVertexMember)members.get(0);
        BlockVertexMember secondBlockMem = (BlockVertexMember)members.get(1);

        // do not process if different block -> normal connection
        if (!firstBlockMem.getBlock().equals(secondBlockMem.getBlock())) {
            return false;
        }

        // update direction if directed, otherwise skip
        if (isDirected) {
            var direction = BlockDirection.DownUp;
            if (firstBlockMem.getEndpoint() == BlockVertexMember.BlockEndpoint.Up
                    && secondBlockMem.getEndpoint() == BlockVertexMember.BlockEndpoint.Down) {
                direction = BlockDirection.UpDown;
            }

            // update
            String name = firstBlockMem.getBlock().getName();
            if (networkLayout.getBlockDirection(name) != BlockDirection.Bidirectional)
                throw new LayoutException(ValidationErrors.DefinedBlockDirectionFormat);

            networkLayout.setBlockDirection(name, direction);
        }

        return true;
    }

    private AbstractVertexMember convertToVertexMember(LayoutReference ref) {
        if (isSwitch(ref))
            return new StandardSwitchVertexMember((PointElement) ref.getElem(), ref.getProp().getLiteral());

        if (isDoubleSlipSwitch(ref))
            return new DoubleSlipSwitchVertexMember((PointElement) ref.getElem(), ref.getProp().getLiteral());

        if (isCrossing(ref))
            return new CrossingVertexMember((CrossingElement) ref.getElem(), ref.getProp().getLiteral());

        return new BlockVertexMember((BlockElement) ref.getElem(), ref.getProp().getLiteral());
    }

    private boolean isSignal(LayoutReference ref) {
        return ref.getElem() instanceof SignalElement;
    }

    private boolean isBlock(LayoutReference ref) {
        return ref.getElem() instanceof BlockElement;
    }

    private boolean isCrossing(LayoutReference ref) {
        return ref.getElem() instanceof CrossingElement;
    }

    private boolean isSwitch(LayoutReference ref) {
        return ref.getElem() instanceof PointElement
                && ref.getProp() != null
                && BahnConstants.STANDARD_SWITCH_PROPS.contains(ref.getProp().getLiteral().toLowerCase());
    }

    private boolean isDoubleSlipSwitch(LayoutReference ref) {
        return ref.getElem() instanceof PointElement
                && ref.getProp() != null
                && BahnConstants.DOUBLE_SLIP_SWITCH_PROPS.contains(ref.getProp().getLiteral().toLowerCase());
    }
}
