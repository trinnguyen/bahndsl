package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.TestHelper;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
class BahnNormalizationProviderTest {

    @Inject
    BahnNormalizationProvider provider;

    @Inject
    Serializer serializer;

    @Inject
    TestHelper testHelper;

    @ParameterizedTest
    @ValueSource (strings = {
            "def test() string arr[] end",
            "def test() int arr[] end",
            "def test() bool arr[] end",
            "def test() float arr[] end",
    })
    void testArrayStmt(String src) throws Exception {
        ensureNormalize(src, List.of("int _test_arr_cnt_1 = 1024"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string arr[]) end",
            "def test(int arr[]) end",
            "def test(bool arr[]) end",
            "def test(float arr[]) end",
    })
    void testArrayParams(String src) throws Exception {
        ensureNormalize(src, List.of("int _test_arr_cnt_1"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test() string items[] for string id in items end end",
            "def test() int items[] for int id in items end end",
            "def test() bool items[] for bool id in items end end",
            "def test() float items[] for float id in items end end"
    })
    void testForeachStmt(String src) throws Exception {
        ensureNormalize(src, List.of("while", "int _test_items_cnt_1 = 1024", "int _test_t1 = 0"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test() for int id in {2,3} end end",
            "def test() for float id in {2.2,3} end end",
            "def test() for bool id in {true, false} end end",
            "def test() for string id in {\"a\", \"b\"} end end",
    })
    void testForeachStmtLiteralArray(String src) throws Exception {
        ensureNormalize(src, List.of("while", "_test_t1 [] = {", "int _test__test_t1_cnt_1 = "));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string items[]) for string id in items end end",
            "def test(int items[]) for int id in items end end",
            "def test(bool items[]) for bool id in items end end",
            "def test(float items[]) for float id in items end end",
            //"def test() for int id in run() end end def run(): int[] int i[] return i end",
    })
    void testForeachParam(String src) throws Exception {
        ensureNormalize(src, List.of("while", "int _test_items_cnt_1", "int _test_t1 = 0"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(): bool return \"a\" != \"a\" end",
            "def test(): bool return \"a\" == \"a\" end",
            "def test(string id): bool return \"a\" == id end",
            "def test(string id): bool return \"a\" != id end",
            "def test(string id): bool return id == \"a\" end",
            "def test(string id): bool return id != \"a\" end",
            "def test(string id1, string id2): bool return id1 == id2 end",
            "def test(string id1, string id2): bool return id1 != id2 end"
    })
    void testStringEquals(String src) throws Exception {
        if (src.contains("!=")) {
            ensureNormalize(src, List.of("return ! extern string_equals ( "));
        } else {
            ensureNormalize(src, List.of("return extern string_equals ( "));
        }
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(): string return \"a\" + \"a\" end",
            "def test(string id): string return \"a\" + id end",
            "def test(string id1, string id2): string return id1 + id2 end"
    })
    void testStringConcat(String src) throws Exception {
        ensureNormalize(src, List.of("return extern string_concat ( "));
    }

    @ParameterizedTest
    @ValueSource (strings = {
             "def test(string src, string dst) string ids[] = get routes from src to dst end",
            "def test() string ids[] = get routes from \"a\" to \"b\" end",
    })
    void testDomainGetRoutes(String src) throws Exception {
        ensureNormalize(src, List.of(SyntacticTransformer.EXTERN_TABLE_GET_ROUTES));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) get state id end",
            "def test() get state \"sig1\" end",
    })
    void testDomainGetState(String src) throws Exception {
        ensureNormalize(src, List.of(SyntacticTransformer.EXTERN_STATE_GETTER_NAME));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) set state id to clear end",
            "def test() set state \"sig1\" to normal end",
    })
    void testDomainSetState(String src) throws Exception {
        ensureNormalize(src, List.of(SyntacticTransformer.EXTERN_STATE_SETTER_NAME));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) get config block.limit id end",
            "def test() get config block.limit \"block1\" end",
    })
    void testDomainGetConfigInt(String src) throws Exception {
        ensureNormalize(src, List.of("extern config_get_scalar_int_value"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) string arr[] = get config block.block_signals id end",
            "def test() string arr[] = get config route.path \"r0\" end",
    })
    void testDomainGetConfigArray(String src) throws Exception {
        ensureNormalize(src, List.of("extern config_get_array_string_value"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) set config route.train \"r0\" to id end",
            "def test() set config route.train \"r0\" to \"cargo\" end"
    })
    void testDomainSetConfigArray(String src) throws Exception {
        ensureNormalize(src, List.of("extern config_set_scalar_string_value"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) grant \"r0\" to id end",
            "def test() grant \"r0\" to \"cargo\" end"
    })
    void testDomainGrant(String src) throws Exception {
        ensureNormalize(src, List.of("extern config_set_scalar_string_value"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) int i = get speed id end",
            "def test() int i = get speed \"r0\" end"
    })
    void testDomainGetSpeed(String src) throws Exception {
        ensureNormalize(src, List.of("extern " + SyntacticTransformer.EXTERN_TRAIN_SPEED_GETTER_NAME));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) set speed id to 100 end",
            "def test() set speed \"r0\" to 0 end"
    })
    void testDomainSetSpeed(String src) throws Exception {
        ensureNormalize(src, List.of("extern " + SyntacticTransformer.EXTERN_TRAIN_SPEED_SETTER_NAME));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) bool b = is id occupied end",
            "def test(string id) bool b = is id not occupied end",
    })
    void testDomainSegmetOccupied(String src) throws Exception {
        ensureNormalize(src, List.of("extern is_segment_occupied"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) bool b = is id available end",
            "def test(string id) bool b = is id not available end",
    })
    void testDomainRouteAvailable(String src) throws Exception {
        ensureNormalize(src, List.of("extern config_get_scalar_string_value", "route", "train"));
    }

    @Test
    void testBasicStatmentIf() throws Exception {
        var src = "def test() if run() && true end end def run() : bool return true end";
        ensureNormalize(src, List.of("bool _test_t1 = run()", "if _test_t1 && true"));
    }

    @Test
    void testBasicStatmentWhile() throws Exception {
        var src = "def test() while run() && true end end def run() : bool return false end";
        ensureNormalize(src, List.of("bool _test_t1 = run()", "while _test_t1 && true"));
    }

    private void ensureNormalize(String src, List<String> expectedItems) throws Exception {
        var input = testHelper.parseValid(src);

        // perform
        var decls = BahnUtil.getDecls(input);
        provider.normalize(decls);
        var out = serializer.serialize(input.getContents().get(0), SaveOptions.newBuilder().format().getOptions());

        // verify
        testHelper.ensureTextContent(out, expectedItems);
    }
}