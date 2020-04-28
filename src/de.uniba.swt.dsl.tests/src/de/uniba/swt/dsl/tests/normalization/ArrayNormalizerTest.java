package de.uniba.swt.dsl.tests.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.normalization.ArrayNormalizer;
import de.uniba.swt.dsl.normalization.BahnNormalizationProvider;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ResourceHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
public class ArrayNormalizerTest {

    @Inject
    ResourceHelper resourceHelper;

    @Inject
    BahnNormalizationProvider provider;

    @Inject
    ArrayNormalizer normalizer;

    @Test
    public void testStringArr() throws Exception {
        var res = resourceHelper.resource("def test() string arr[] end");
        FuncDecl funcDecl = BahnUtil.getDecls(res).get(0);

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

}
