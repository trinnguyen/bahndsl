package de.uniba.swt.dsl.common.generator.sccharts.models;

public class Transition extends Action {
    private boolean isDeferred;
    private boolean immediate;
    private String targetStateId;

    public Transition(String targetStateId) {
        this(targetStateId, true);
    }

    public Transition(String targetStateId, boolean immediate) {
        this.targetStateId = targetStateId;
        this.immediate = immediate;
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

    public String getTargetStateId() {
        return targetStateId;
    }

    public void setTargetStateId(String targetStateId) {
        this.targetStateId = targetStateId;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "targetState=" + targetStateId +
                "\n" + super.toString() +
                '}';
    }
}
