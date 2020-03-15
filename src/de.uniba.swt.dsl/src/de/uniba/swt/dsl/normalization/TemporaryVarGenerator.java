package de.uniba.swt.dsl.normalization;

import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.BahnFactory;
import de.uniba.swt.dsl.bahn.VarDecl;
import de.uniba.swt.dsl.validation.typing.ExprDataType;

@Singleton
public class TemporaryVarGenerator {

    private String functionName = "";

    private int counter = 1;

    public void resetFunc(String name) {
        functionName = name.toLowerCase();
        counter = 1;
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
        String prefix = "t";
        return String.format("_%s_%s_%d", functionName, prefix, counter++);
    }
}
