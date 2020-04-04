package de.uniba.swt.dsl.common.generator.sccharts.models;

public class Transition extends Action {
    private String targetStateId;
    private TransitionType transitionType = TransitionType.Immediate;

    public Transition(String targetStateId) {
        this(targetStateId, TransitionType.Immediate);
    }

    public Transition(String targetStateId, TransitionType transitionType) {
        this.targetStateId = targetStateId;
        this.transitionType = transitionType;
    }

    public String getTargetStateId() {
        return targetStateId;
    }

    public void setTargetStateId(String targetStateId) {
        this.targetStateId = targetStateId;
    }

    public TransitionType getTransitionType() {
        return transitionType;
    }

    public void setTransitionType(TransitionType transitionType) {
        this.transitionType = transitionType;
    }
}

