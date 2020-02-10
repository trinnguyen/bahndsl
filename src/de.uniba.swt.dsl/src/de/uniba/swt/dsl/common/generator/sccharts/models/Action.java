package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.bahn.Expression;

import java.util.ArrayList;
import java.util.List;

public class Action {
    private String label;
    private Expression trigger;
    private List<Effect> effects = new ArrayList<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Expression getTrigger() {
        return trigger;
    }

    public void setTrigger(Expression trigger) {
        this.trigger = trigger;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public void setEffects(List<Effect> effects) {
        this.effects = effects;
    }

    @Override
    public String toString() {
        return "Action{" +
                "label='" + label + '\'' +
                ", trigger=" + trigger +
                ", effects=" + effects +
                '}';
    }
}
