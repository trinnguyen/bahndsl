package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.common.layout.models.*;


import java.util.*;

public class NetworkValidator {

    private NetworkLayout networkLayout;
    private Set<String> validElements = new HashSet<>();

    public void checkWelformness(NetworkLayout networkLayout) {
        this.networkLayout = networkLayout;
        validate();
    }

    private void validate() {
        validElements.clear();
        List<String> errors = new ArrayList<>();
        for (var vertex : networkLayout.getVertices()) {
            for (var member : vertex.getMembers()) {
                try {
                    switch (member.getType()) {
                        case Signal:
                            checkSignal((SignalVertexMember) member);
                            break;
                        case Point:
                            checkPoint((PointVertexMember) member);
                            break;
                        default:
                            checkBlock((BlockVertexMember) member);
                            break;
                    }
                } catch (LayoutException exception) {
                    errors.add(exception.getMessage());
                }
            }
        }

        // throw errors
        if (errors.size() > 0) {
            throw new RuntimeException(String.join("\n", errors));
        }
    }

    private void checkBlock(BlockVertexMember member) throws LayoutException {
        if (validElements.contains(member.getName()))
            return;

        var vertex = networkLayout.findVertex(member);
        LayoutVertex tmpVertex = null;

        switch (member.getEndpoint()) {
            case Up:
                tmpVertex = networkLayout.findVertex(member.createBlockMember(BlockVertexMember.BlockEndpoint.Down));
                break;
            case Down:
                tmpVertex = networkLayout.findVertex(member.createBlockMember(BlockVertexMember.BlockEndpoint.Up));
                break;
        }

        if (vertex == null && tmpVertex == null) {
            throw new LayoutException("Block must connect to at least one block: " + member.getName());
        }

        if (vertex != null && vertex.equals(tmpVertex)) {
            throw new LayoutException("Block must connect to 2 different blocks: " + member.getName());
        }

        // mark as validated
        validElements.add(member.getName());
    }

    private void checkPoint(PointVertexMember member) throws LayoutException {

        if (validElements.contains(member.getName()))
            return;

        var endpoints = new HashSet<PointVertexMember.PointEndpoint>();
        endpoints.add(member.getEndpoint());

        switch (member.getEndpoint()) {
            case Normal:
                endpoints.add(PointVertexMember.PointEndpoint.Stem);
                endpoints.add(PointVertexMember.PointEndpoint.Reverse);
                break;
            case Reverse:
                endpoints.add(PointVertexMember.PointEndpoint.Stem);
                endpoints.add(PointVertexMember.PointEndpoint.Normal);
                break;
            default:
                endpoints.add(PointVertexMember.PointEndpoint.Normal);
                endpoints.add(PointVertexMember.PointEndpoint.Reverse);
                break;
        }

        // find vertices
        var countItems = endpoints.stream().map(e -> {
            if (e == member.getEndpoint()) {
                return networkLayout.findVertex(member);
            }

            var tmpMem = member.createMember(e);
            return networkLayout.findVertex(tmpMem);
        }).filter(Objects::nonNull).distinct().count();
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
