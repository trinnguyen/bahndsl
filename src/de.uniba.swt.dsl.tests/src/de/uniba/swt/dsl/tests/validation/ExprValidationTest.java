package de.uniba.swt.dsl.tests.validation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.uniba.swt.dsl.bahn.BahnModel;
import de.uniba.swt.dsl.bahn.BahnPackage;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.TestConstants;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
public class ExprValidationTest extends AbstractValidationTest {
    @Inject
    Provider<ResourceSet> resourceSetProvider;

    @Inject
    ParseHelper<BahnModel> parseHelper;

    @Inject
    ValidationTestHelper validationTestHelper;

    /**
     * Sample snippet generated in Visual Studio code and Eclipse IDE
     * @param src
     */
    @ParameterizedTest
    @ValueSource(strings = {
            TestConstants.SampleRequestRouteEmpty,
            TestConstants.SampleDriveRoute,
            TestConstants.SampleInterlockingEmpty,
    })
    void validSampleFunction(String src) {
        validationTestHelper.assertNoErrors(internalParse(src));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "def test() end",
            "def test() string ids[] get_shortest_route(ids) end",
            "def test()\n" +
            "    plus(1, -2)\n" +
            "end\n" +
            "def plus(int a, int b):int\n" +
            "    return a + b\n" +
            "end"
    })
    void validExprsTest(String src) {
        validationTestHelper.assertNoErrors(internalParse(src));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "def test() int i int i end",
            "def test() int i float i end",
            "def test(string ids[]) int ids[] end",
            "def test(string ids[], int ids) end",
    })
    void definedVariableName(String src) {
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.REF_VAR_DECL, null, "Variable", "is already defined");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "def test() end def test() end",
            "def test() end def test(int id) end",
    })
    void definedFunction(String src) {
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.FUNC_DECL, null, "Function", "is already defined");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "def test() int ids[] = {1,2,3} end",
            "def test() bool f[] = {false,true,false} end",
            "def test() test2({1.5,41}) end def test2(float arr[]) end",
            "def test() for int a in {2,3} end end",
    })
    void validLiteralArray(String src) {
        validationTestHelper.assertNoErrors(internalParse(src));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "def test() string a = \"x\" + \"y\" end",
            "def test() string a0 string a = \"x\" + a0 end",
            "def test() string a0 string a1 string a2 = a0 + a1 end",
            "def test() bool b = \"x\" == \"y\" end",
            "def test() string a0 bool b = \"x\" == a0 end",
            "def test() string a0 string a1 bool b = a0 == a1 end",
            "def test() bool b = \"x\" != \"y\" end",
            "def test() string a0 bool b = \"x\" != a0 end",
            "def test() string a0 string a1 bool b = a0 != a1 end",
    })
    void validStringOperators(String src) {
        validationTestHelper.assertNoErrors(internalParse(src));
    }

    @Test
    void testValuedRefInvalidVarDecl() {
        var src = "def plus(int a, int b): int\n" +
                "    return a + b + c\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.VALUED_REFERENCE_EXPR, "org.eclipse.xtext.diagnostics.Diagnostic.Linking", "Couldn't resolve reference to RefVarDecl 'c'");
    }

    @Test
    void testValuedRefInvalidCrossParamDecl() throws Exception {
        var src = "def plus(int a, int b)\n" +
                "    return a + b\n" +
                "end\n" +
                "def test()\n" +
                "    plus(a, -2)\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.VALUED_REFERENCE_EXPR, "org.eclipse.xtext.diagnostics.Diagnostic.Linking", "Couldn't resolve reference to RefVarDecl 'a'");
    }

    @Test
    void testValuedRefInvalidCrossVarDecl() throws Exception {
        var src = "def plus(int a, int b)\n" +
                "    int c = a + b\n" +
                "    return c\n" +
                "end\n" +
                "def test()\n" +
                "    plus(c, -2)\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.VALUED_REFERENCE_EXPR, "org.eclipse.xtext.diagnostics.Diagnostic.Linking", "Couldn't resolve reference to RefVarDecl 'c'");
    }

    @ParameterizedTest
    @CsvSource({
            "def test() int a1 = false + 1 end, Expected 'int or float' but found 'bool'",
            "def test() bool a1 = false || 3 end, Expected 'bool' but found 'int'",
            "def test() bool a1 = 3 == true end, Expected same type but found",
            "def test() bool a1 = false > true end, Expected 'int or float' but found 'bool'",

    })
    void errorOpExprTest(String src, String msg) {
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.OP_EXPRESSION, null, msg);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "def test() int n = true end",
            "def test() int n3 = true && false end",
    })
    void errorVarDeclTest(String src) {
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.VARIABLE_ASSIGNMENT, null, "Expected type int, actual type: bool");
    }

    @Test
    void errorVarDeclFuncCall() {
        var src = "def plus(int a, int b): int\n" +
                "    return a + b\n" +
                "end\n" +
                "\n" +
                "def test()\n" +
                "    bool c = plus(3, 4)\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.VARIABLE_ASSIGNMENT, null, "Expected type bool, actual type: int");
    }

    @Test
    void testTypeAssignmentStmt() {
        var src = "def test()\n" +
                "    bool b = false\n" +
                "   b = 4\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.VARIABLE_ASSIGNMENT, null, "Expected type bool, actual type: int");
    }

    @Test
    void errorVarDeclTestBoolInt() {
        var src = "def test()\n" +
                "    if 3\n" +
                "        int i1 = 1\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.SELECTION_STMT, null, "Expected 'bool' but found 'int'");
    }

    @Override
    protected ParseHelper<BahnModel> getParseHelper() {
        return parseHelper;
    }

    @Override
    protected Provider<ResourceSet> getResourceSetProvider() {
        return resourceSetProvider;
    }
}
