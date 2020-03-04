package de.uniba.swt.dsl.normalization;

import de.uniba.swt.dsl.bahn.BahnFactory;
import de.uniba.swt.dsl.bahn.VarDecl;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

public class TemporaryVarGenerator {

    private String prefix = "t";

    private int counter = 0;

    public void reset() {
        counter = 0;
    }

    public VarDecl createTempVar(ExprDataType dataType) {
        String name = nextTempVarName();

        var decl = BahnFactory.eINSTANCE.createVarDecl();
        decl.setName(name);
        decl.setType(dataType.getDataType());
        decl.setArray(dataType.isArray());

        return decl;
    }

    private String nextTempVarName() {
        return String.format("%s_%d", prefix, counter++);
    }
}
