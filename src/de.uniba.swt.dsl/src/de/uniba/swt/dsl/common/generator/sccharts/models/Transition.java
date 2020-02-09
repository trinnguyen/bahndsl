package de.uniba.swt.dsl.common.generator.sccharts.models;

public class Transition extends Action {
    private boolean isDeferred;
    private boolean immediate;
    private State targetState;

    public Transition() {
    }

    public Transition(State targetState) {
        this.targetState = targetState;
    }

    public boolean isDeferred() {
        return isDeferred;
    }

    public void setDeferred(boolean deferred) {
        isDeferred = deferred;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public State getTargetState() {
        return targetState;
    }

    public void setTargetState(State targetState) {
        this.targetState = targetState;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "targetState=" + targetState.getId() +
                "\n" + super.toString() +
                '}';
    }
}
