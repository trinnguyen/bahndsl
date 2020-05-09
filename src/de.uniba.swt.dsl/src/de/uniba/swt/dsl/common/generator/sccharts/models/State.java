/*
 *
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.bahn.Expression;

import java.util.ArrayList;
import java.util.List;

public class State {
    private String id;
    private boolean isInitial;
    private boolean isFinal;
    private List<Transition> outgoingTransitions = new ArrayList<>();
    private String label;

    public State() {
    }

    public State(State other) {
        this.id = other.id;
        this.isInitial = other.isInitial;
        this.isFinal = other.isFinal;
        this.outgoingTransitions = other.outgoingTransitions;
        this.label = other.label;
    }

    public State(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public void setInitial(boolean initial) {
        isInitial = initial;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public List<Transition> getOutgoingTransitions() {
        return outgoingTransitions;
    }

    public void setOutgoingTransitions(List<Transition> outgoingTransitions) {
        this.outgoingTransitions = outgoingTransitions;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "State{" +
                "id='" + id + '\'' +
                ", isInitial=" + isInitial +
                ", isFinal=" + isFinal +
                ", outgoingTransitions=" + outgoingTransitions +
                ", label='" + label + '\'' +
                '}';
    }

    public void addImmediateTransition(String id) {
        addImmediateTransition(id, null);
    }

    public void addImmediateTransition(String id, Expression expr) {
        addTransition(id, TransitionType.Immediate, expr);
    }

    public void addTransition(String id, TransitionType transitionType, Expression expr) {
        var transition = new Transition(id, transitionType);
        transition.setTrigger(expr);
        getOutgoingTransitions().add(transition);
    }
}
