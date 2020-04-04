package de.uniba.swt.dsl.common.generator.sccharts.builder;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.sccharts.models.*;
import de.uniba.swt.dsl.common.util.BahnUtil;

public class SCChartsUtil {
    public final static String VAR_OUTPUT_NAME = "_out";

    public final static String VAR_HAS_RETURN_NAME = "_has_return";

    public final static String VAR_HAS_BREAK = "_has_break";

    public static OpExpression createTrueBooleanTrigger(SVarDeclaration varDecl) {
        // generate decl
        var decl = BahnFactory.eINSTANCE.createVarDecl();
        decl.setType(DataType.BOOLEAN_TYPE);
        decl.setArray(false);
        decl.setName(varDecl.getName());

        // generate condition
        var expression = BahnFactory.eINSTANCE.createOpExpression();
        expression.setOp(OperatorType.EQUAL);
        expression.setLeftExpr(createValuedReferenceExpr(decl));
        expression.setRightExpr(BahnUtil.createBooleanLiteral(true));
        return expression;
    }

    public static ValuedReferenceExpr createValuedReferenceExpr(VarDecl decl) {
        var expr = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        expr.setDecl(decl);
        return expr;
    }

    public static Effect generateBoolAssignEffect(SVarDeclaration varDecl, boolean val) {
        AssignmentEffect effect = new AssignmentEffect();
        effect.setExpression(BahnUtil.createBooleanLiteral(val));
        effect.setVarDeclaration(varDecl);
        return effect;
    }
}
