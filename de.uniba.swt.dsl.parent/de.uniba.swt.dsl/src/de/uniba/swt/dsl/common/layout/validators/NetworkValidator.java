package de.uniba.swt.dsl.common.layout.validators;

import de.uniba.swt.dsl.common.layout.models.CompositeLayoutException;
import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.*;


import java.util.*;

public class NetworkValidator {

    private Set<String> validElements = new HashSet<>();
    private LayoutGraphValidator graphValidator = new LayoutGraphValidator();
    private NetworkLayout networkLayout;

    public void checkWelformness(NetworkLayout networkLayout) throws CompositeLayoutException {
        this.networkLayout = networkLayout;

        // 1. validate connectors
        validateConnectors();

        // 2. ensure all vertices are reachable
        if (!graphValidator.isStrongConnected(networkLayout))
            throw new CompositeLayoutException("Network layout is not strongly connected");
    }

    private void validateConnectors() throws CompositeLayoutException {
        validElements.clear();
        List<LayoutException> errors = new ArrayList<>();
        for (var vertex : networkLayout.getVertices()) {
            for (var member : vertex.getMembers()) {
                try {
                    switch (member.getType()) {
                        case Signal:
                            checkSignal((SignalVertexMember) member);
                            break;
                        case Switch:
                            checkPoint((SwitchVertexMember) member);
                            break;
                        default:
                            checkBlock((BlockVertexMember) member);
                            break;
                    }
                } catch (LayoutException exception) {
                    errors.add(exception);
                }
            }
        }

        // throw errors
        if (errors.size() > 0) {
            throw new CompositeLayoutException(errors);
        }
    }

    private void checkBlock(BlockVertexMember member) throws LayoutException {
        if (validElements.contains(member.getName()))
            return;

        var vertex = networkLayout.findVertex(member);
        var endpoint = member.getEndpoint() == BlockVertexMember.BlockEndpoint.Up ?
                BlockVertexMember.BlockEndpoint.Down :
                BlockVertexMember.BlockEndpoint.Up;
        var tmpVertex = networkLayout.findVertex(member.generateKey(endpoint));

        if (vertex == null || tmpVertex == null || vertex.equals(tmpVertex)) {
            throw new LayoutException("Block must connect to 2 different blocks: " + member.getName());
        }

        // mark as validated
        validElements.add(member.getName());
    }

    private void checkPoint(SwitchVertexMember member) throws LayoutException {

        if (validElements.contains(member.getName()))
            return;

        var endpoints = member.getConnectedEndpoints();
        endpoints.add(member.getEndpoint());

        // find vertices
        var countItems = endpoints.stream()
                .map(e -> networkLayout.findVertex(member.generateKey(e)))
                .filter(Objects::nonNull)
                .distinct().count();
        if (countItems != 3) {
            throw new LayoutException("Point must connect to 3 different blocks: " + member.getName());
        }

        // mark as validated
        validElements.add(member.getName());
    }

    /**
     * find block
     * @param member
     */
    private void checkSignal(SignalVertexMember member) throws LayoutException {
        var vertex = networkLayout.findVertex(member);
        if (vertex.getMembers().stream().noneMatch(member1 -> {
            if (member1.getType() == VertexMemberType.Block) {
                var blockVertexMember = (BlockVertexMember)member1;
                return blockVertexMember.getBlock().equals(member.getConnectedBlock());
            }

            return false;
        })) {
            throw new LayoutException("Connected block is not found for signal: " + member.getName());
        } else {
            validElements.add(member.getName());
        }
    }
}
