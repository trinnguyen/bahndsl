package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.models.*;
import de.uniba.swt.dsl.common.layout.models.graph.LayoutVertex;
import de.uniba.swt.dsl.common.util.BahnConstants;
import org.apache.log4j.Logger;

import java.util.List;

public class NetworkLayoutBuilder {
    private final static Logger logger = Logger.getLogger(NetworkLayoutBuilder.class);

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
                    for (VertexMember member : members) {
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

    private List<VertexMember> convertToVertexMembers(LayoutReference firstRef, LayoutReference secondRef) throws LayoutException {
        if (isSignal(firstRef)) {
            if (!isBlock(secondRef)) {
                throw new LayoutException("Signal can only connect to a block");
            }
            var signalMember = new SignalVertexMember((SignalElement) firstRef.getElem(), (BlockElement)secondRef.getElem());
            var blockMember = new BlockVertexMember((BlockElement)secondRef.getElem(), secondRef.getProp());
            return List.of(signalMember, blockMember);
        }

        if (isSignal(secondRef)) {
            if (!isBlock(firstRef)) {
                throw new LayoutException("Signal can only connect to a block");
            }
            var signalMember = new SignalVertexMember((SignalElement) secondRef.getElem(), (BlockElement)firstRef.getElem());
            var blockMember = new BlockVertexMember((BlockElement)firstRef.getElem(), firstRef.getProp());
            return List.of(blockMember, signalMember);
        }

        return List.of(convertToVertexMember(firstRef), convertToVertexMember(secondRef));
    }

    private boolean updateBlockDirection(NetworkLayout networkLayout, List<VertexMember> members, boolean isDirected) throws LayoutException {
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
                throw new LayoutException("Block direction is already defined: " + name);

            networkLayout.setBlockDirection(name, direction);
        }

        return true;
    }

    private VertexMember convertToVertexMember(LayoutReference ref) {
        if (isSwitch(ref))
            return new SwitchVertexMember((PointElement) ref.getElem(), ref.getProp());

        if (isCrossing(ref))
            return new CrossingVertexMember((PointElement) ref.getElem(), ref.getProp());

        return new BlockVertexMember((BlockElement) ref.getElem(), ref.getProp());
    }

    private boolean isSignal(LayoutReference ref) {
        return ref.getElem() instanceof SignalElement;
    }

    private boolean isBlock(LayoutReference ref) {
        return ref.getElem() instanceof BlockElement;
    }

    private boolean isSwitch(LayoutReference ref) {
        return ref.getElem() instanceof PointElement
                && ref.getProp() != null
                && BahnConstants.SWITCH_PROPS.contains(ref.getProp().toLowerCase());
    }

    private boolean isCrossing(LayoutReference ref) {
        return ref.getElem() instanceof PointElement
                && ref.getProp() != null
                && BahnConstants.CROSSING_PROPS.contains(ref.getProp().toLowerCase());
    }
}
