package de.uniba.swt.dsl.common.layout.models;

import de.uniba.swt.dsl.bahn.BlockElement;
import de.uniba.swt.dsl.bahn.SignalElement;

public class SignalVertexMember extends VertexMember {

    public SignalVertexMember(SignalElement signal, BlockElement connectedBlock) {
        this.signal = signal;
        this.connectedBlock = connectedBlock;
    }

    private SignalElement signal;
    private BlockElement connectedBlock;

    public SignalElement getSignal() {
        return signal;
    }

    public void setSignal(SignalElement signal) {
        this.signal = signal;
    }

    public BlockElement getConnectedBlock() {
        return connectedBlock;
    }

    public void setConnectedBlock(BlockElement connectedBlock) {
        this.connectedBlock = connectedBlock;
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
