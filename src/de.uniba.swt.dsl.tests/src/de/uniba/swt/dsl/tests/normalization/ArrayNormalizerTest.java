package de.uniba.swt.dsl.tests.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.normalization.ArrayNormalizer;
import de.uniba.swt.dsl.normalization.BahnNormalizationProvider;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
public class ArrayNormalizerTest {

    @Inject
    BahnNormalizationProvider provider;

    @Inject
    ArrayNormalizer normalizer;

    @Test
    public void TestArray() {
        var varDecl = BahnFactory.eINSTANCE.createVarDecl();
        varDecl.setArray(true);
        varDecl.setName("arr");
        varDecl.setType(DataType.STRING_TYPE);

        var declStmt = BahnFactory.eINSTANCE.createVarDeclStmt();
        declStmt.setDecl(varDecl);

        FuncDecl funcDecl = createFuncDecl("test", declStmt);

        // perform
        provider.beforeNormalize(funcDecl);
        normalizer.normalizeFunc(funcDecl);

        // count var
        var items = funcDecl.getStmtList().getStmts().stream()
                .filter(s -> s instanceof VarDeclStmt)
                .map(s -> {
                    var decl = ((VarDeclStmt) s).getDecl();
                    return Tuple.of(decl.getName(), new ExprDataType(decl.getType(), decl.isArray()));
                })
                .collect(Collectors.toList());

        // ensure having 2 variables
        var expectedVars = List.of(Tuple.of("_test_arr_cnt_1", ExprDataType.ScalarInt),
                Tuple.of("arr", ExprDataType.ArrayString));
        Assert.assertEquals(expectedVars, items);
    }

    private FuncDecl createFuncDecl(String name, Statement stmt) {
        var decl = BahnFactory.eINSTANCE.createFuncDecl();

        decl.setName(name);
        decl.setStmtList(BahnFactory.eINSTANCE.createStatementList());
        decl.getStmtList().getStmts().add(stmt);

        return decl;
    }
}
