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
