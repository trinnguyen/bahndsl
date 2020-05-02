/*
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BahnDSL.  If not, see <https://www.gnu.org/licenses/>.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 */

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
