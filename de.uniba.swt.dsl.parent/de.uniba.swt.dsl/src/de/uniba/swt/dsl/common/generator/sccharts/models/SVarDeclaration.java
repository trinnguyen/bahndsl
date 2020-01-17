package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.bahn.Expression;

import java.util.List;

public class SVarDeclaration {
    private boolean isInput;
    private boolean isOutput;
    private String name;
    private SDataType dataType;
    private int cardinality;
    private Expression initialExpr;
    private List<Expression> cardinalityExprs;

    public boolean isInput() {
        return isInput;
    }

    public void setInput(boolean input) {
        isInput = input;
    }

    public boolean isOutput() {
        return isOutput;
    }

    public void setOutput(boolean output) {
        isOutput = output;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SDataType getDataType() {
        return dataType;
    }

    public void setDataType(SDataType dataType) {
        this.dataType = dataType;
    }

    public int getCardinality() {
        return cardinality;
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public Expression getInitialExpr() {
        return initialExpr;
    }

    public void setInitialExpr(Expression initialExpr) {
        this.initialExpr = initialExpr;
    }

    public List<Expression> getCardinalityExprs() {
        return cardinalityExprs;
    }

    public void setCardinalityExprs(List<Expression> cardinalityExprs) {
        this.cardinalityExprs = cardinalityExprs;
    }
}
