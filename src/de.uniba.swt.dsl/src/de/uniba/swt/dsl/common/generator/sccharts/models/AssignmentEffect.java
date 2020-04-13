package de.uniba.swt.dsl.common.generator.sccharts.models;

import de.uniba.swt.dsl.bahn.Expression;
import de.uniba.swt.dsl.bahn.LiteralExpr;

import java.util.List;

public class AssignmentEffect extends Effect {
    private SVarDeclaration varDeclaration;
    private Expression indexExpr;

    public SVarDeclaration getVarDeclaration() {
        return varDeclaration;
    }

    public void setVarDeclaration(SVarDeclaration varDeclaration) {
        this.varDeclaration = varDeclaration;
    }

    public void setIndexExpr(Expression indexExpr) {
        this.indexExpr = indexExpr;
    }

    private List<LiteralExpr> arrayExprs;

    public List<LiteralExpr> getArrayExprs() {
        return arrayExprs;
    }

    public void setArrayExprs(List<LiteralExpr> arrayExprs) {
        this.arrayExprs = arrayExprs;
    }

    public Expression getIndexExpr() {
        return indexExpr;
    }
}
