/*
 *  
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *  
 * This file is part of the BahnDSL project, a domain-specific language 
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *  
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *  
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

package de.uniba.swt.dsl.normalization;

import com.google.inject.Inject;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.TestHelper;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
class BahnNormalizationProviderTest {

    @Inject
    SyntacticExprNormalizer syntacticExprNormalizer;

    @Inject
    BasicStatementNormalizer basicStatementNormalizer;

    @Inject
    StringEqualNormalizer stringEqualNormalizer;

    @Inject
    ArrayNormalizer arrayNormalizer;

    @Inject
    ForeachNormalizer foreachNormalizer;

    @Inject
    VariableNameNormalizer variableNameNormalizer;

    @Inject
    BahnNormalizationProvider provider;

    @Inject
    Serializer serializer;

    @Inject
    TestHelper testHelper;

    @BeforeEach
    void prepare() {
        provider.setNormalizers(List.of(basicStatementNormalizer, arrayNormalizer, foreachNormalizer, stringEqualNormalizer, syntacticExprNormalizer));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test() string arr[] end",
            "def test() int arr[] end",
            "def test() bool arr[] end",
            "def test() float arr[] end",
    })
    void testArrayStmt(String src) throws Exception {
        ensureNormalize(src, List.of("int _arr_cnt = 0"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test() string arr[] = {\"a\", \"b\"} end",
            "def test() int arr[] = {2,3} end",
            "def test() bool arr[] = {true, false} end",
            "def test() float arr[] = {4.5,9.5} end",
    })
    void testArrayLiteralStmt(String src) throws Exception {
        ensureNormalize(src, List.of("int _arr_cnt = 2"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test() string arr[] arr = {\"a\", \"b\"} end",
            "def test() int arr[] arr = {2,3} end",
            "def test() bool arr[] arr = {true, false} end",
            "def test() float arr[] arr = {4.5,9.5} end",
    })
    void testArrayAssignment(String src) throws Exception {
        ensureNormalize(src, List.of("int _arr_cnt = 0", "_arr_cnt = 2"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string arr[]) end",
            "def test(int arr[]) end",
            "def test(bool arr[]) end",
            "def test(float arr[]) end",
    })
    void testArrayParams(String src) throws Exception {
        ensureNormalize(src, List.of("int _arr_cnt"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test() for int i in {1,2,3} end end"
    })
    void testArrayForeachInitial(String src) throws Exception {
        ensureNormalize(src, List.of("int _test_t1 [] = { 1,2,3}"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test() foo({1,2,3}, {true, false}) end def foo(int arr[], bool arr2[]) end"
    })
    void testArrayFunctonInitial(String src) throws Exception {
        ensureNormalize(src, List.of(
                "int _test_t1 [] = { 1,2,3}",
                "bool _test_t2 [] = { true, false}",
                "int __test_t1_cnt",
                "int __test_t2_cnt",
                "foo( _test_t1 , __test_t1_cnt , _test_t2 , __test_t2_cnt )",
                "def foo(int arr[], int _arr_cnt , bool arr2[] , int _arr2_cnt )"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(int arr[]) int x = arr.len end",
            "def test() int arr[] int x = arr.len end"
    })
    void testArrayLen(String src) throws Exception {
        ensureNormalize(src, List.of("int x = _arr_cnt"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test() string items[] for string id in items end end",
            "def test() int items[] for int id in items end end",
            "def test() bool items[] for bool id in items end end",
            "def test() float items[] for float id in items end end"
    })
    void testForeachStmt(String src) throws Exception {
        ensureNormalize(src, List.of("while", "int _items_cnt = 0", "int _test_t1 = 0"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test() for int id in {2,3} end end",
            "def test() for float id in {2.2,4} end end",
            "def test() for bool id in {true, false} end end",
            "def test() for string id in {\"a\", \"b\"} end end",
    })
    void testForeachStmtLiteralArray(String src) throws Exception {
        ensureNormalize(src, List.of("while", "_test_t1 [] = {", "int __test_t1_cnt = 2"));
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
        ensureNormalize(src, List.of("items[] , int _items_cnt )",
                "int _test_t1 = 0",
                "while _test_t1 < _items_cnt",
                "id = items [ _test_t1 ]",
                "_test_t1 = _test_t1 + 1"));
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
             "def test(string src, string dst) string ids[] = get routes from src to dst end"
    })
    void testDomainGetRoutes(String src) throws Exception {
        ensureNormalize(src, List.of("int _ids_cnt = 0", "string ids[]", "_ids_cnt = extern interlocking_table_get_routes ( src , dst , ids )"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) string ids[] = get config route.path id end",
    })
    void testGetConfigArray(String src) throws Exception {
        ensureNormalize(src, List.of("int _ids_cnt = 0", "string ids[]", "_ids_cnt = extern config_get_array_string_value ( \"route\" , id , \"path\" , ids )"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string rid, string pid) get position pid in rid end",
    })
    void testGetPointPosition(String src) throws Exception {
        ensureNormalize(src, List.of("extern config_get_point_position ( rid , pid )"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) string src = get config route.source id end",
    })
    void testGetConfigScalar(String src) throws Exception {
        ensureNormalize(src, List.of("string src = extern config_get_scalar_string_value ( \"route\" , id , \"source\" )"));
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string id) get state id end",
            "def test() string id = \"a\" string st = get state id end",
    })
    void testDomainGetState(String src) throws Exception {
        ensureNormalize(src, List.of("extern track_state_get_value ( id )"));
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

    @ParameterizedTest
    @CsvSource( value = {
            "def test(string sig) set state sig to stop end, stop",
            "def test(string sig) set state sig to caution end, caution",
            "def test(string sig) set state sig to clear end, clear",
    })
    void testDomainSetSignal(String src, String param) throws Exception {
        ensureNormalize(src, List.of("extern track_state_set_value ( sig , \"" + param + "\""));
    }

    @ParameterizedTest
    @CsvSource( value = {
            "def test(string pt) set state pt to normal end, normal",
            "def test(string pt) set state pt to reverse end, reverse"
    })
    void testDomainSetPoint(String src, String param) throws Exception {
        ensureNormalize(src, List.of("extern track_state_set_value ( pt , \"" + param + "\""));
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

    @ParameterizedTest
    @ValueSource (strings = {
            "def test(string a1[]) end",
            "def test() string a1[] end",
            "def test(int i1, string a1) end",
            "def test() int a1 end",
            "def test() float a1 end",
            "def test() bool a1 end",
    })
    void testRenameNormalizer(String src) throws Exception {
        provider.setNormalizers(List.of(variableNameNormalizer));
        ensureNormalize(src, List.of("_a1"));
    }

    private void ensureNormalize(String src, List<String> expectedItems) throws Exception {
        // verify
        TestHelper.ensureTextContent(normalize(src), expectedItems);
    }

    private String normalize(String src) throws Exception {
        var input = testHelper.parseValid(src);

        // perform
        var decls = BahnUtil.getDecls(input);
        provider.normalize(decls);
        return serializer.serialize(input.getContents().get(0), SaveOptions.newBuilder().getOptions());
    }
}