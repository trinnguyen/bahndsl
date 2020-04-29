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
