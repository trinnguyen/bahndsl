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

package de.uniba.swt.dsl.tests.parser;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.inject.Inject;

import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.ParserTestHelper;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
public class ExprParsingTest {

	@Inject
	private ParserTestHelper parserTestHelper;

	@ParameterizedTest
	@ValueSource(strings = {
			"def inter() end",

			"def shortest_route(string ids[]): string end",

			"def request_route(string id1, string id2, string id3): string end",

			"def request_route(string id1, string id2, string id3): string\n" +
			"end\n" +
			"def drive_route(string id1, string id2, string id3): bool\n" +
			"end",

			"def filter(string ids[], bool test): string[] end"
	})
	public void validFuncDeclTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"def test() if true out = 1 end end",

			"def test() if true out = 1 else out = 2 end end",

			"def test() if true if false o = 2 else o = 3 end end end",

			"def test()\n" +
			"    int sum = 0\n" +
			"    int i = 0\n" +
			"    while i < 10\n" +
			"        sum = sum + i\n" +
			"        i = i + 1\n" +
			"    end\n" +
			"end",

			"def test() for string id in ids out = id end end",

			"def test() string a = 3 end",

			"def test() a = 4 end",

			"def test() min = get_shortest_route(ids) end",

			"def test() min = extern log(msg) end",

			"def test(): int return 3 end"
	})
	public void validStatementTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"def test() int i end",
			"def test() float i = 1 end",
			"def test() float f end",
			"def test() float f = 1 end",
			"def test() float f = 1.2 end",
			"def test() bool b = true end",
			"def test() bool b = false end",
			"def test() string s end",
			"def test() string s = \"\" end",
			"def test() int ai[] end",
			"def test() float af[] end",
			"def test() bool ab[] end",
			"def test() string as[] end"
	})
	public void validVarDeclTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"def test() i = 0 end",
			"def test() f = 1 end",
			"def test() f = 1.2 end",
			"def test() b = true end",
			"def test() b = false end",
			"def test() s = \"\" end",
			"def test() s = \"value\" end"
	})
	public void validAssignmentTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"def test() a[0] = 0 end",
			"def test() a[1 + 2] = 0.2 end",
			"def test() int a[] = {1,2,3} end",
			"def test() float a[] = {1,2.3,3} end",
			"def test() bool a[] = {false, true, false} end",
			"def test() string a[] = {\"\", \"test1\"} end",
	})
	public void validArrayAssignmentTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"def test() bool a = true || false end",
			"def test() bool b = true && false end",
			"def test() bool b = true == false end",
			"def test() bool b = true != false end",
			"def test() bool b = 1 > 2 end",
			"def test() bool b = 1 >= 2 end",
			"def test() bool b = 1 < 2 end",
			"def test() int c = 1 + 2 end",
			"def test() int c = 1 - 2 end",
			"def test() float c = 1.2 - 2 end",
			"def test() float c = 1.2 + 2 end",
			"def test() float c = 1.2 + 2.2 end",
			"def test() int c = 1 * 2 end",
			"def test() int c = 1 / 2 end",
			"def test() float c = 1.2 * 2 end",
			"def test() float c = 1.2 / 2 end",
			"def test() float c = 1.2 / 2.2 end",
			"def test() bool a = !false end",
			"def test() int a = -1 end",
			"def test() int a = 3 * (4.5 - 3) end",
			"def test() int a = arr[0] end",
			"def test() int a = arr.len end",
	})
	public void validOpExpressionTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"def test() int a = 1 end",
			"def test() float a = 1.2 end",
			"def test() bool a = true end",
			"def test() bool a = false end",
			"def test() string a = \"id\" end",
	})
	public void validLiteralTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"def test()\n" +
			"    int a[] = {3,4}\n" +
			"    float b[] = {4.5, 2, 4}\n" +
			"    bool c[] = {true}\n" +
			"end",

			"def test()\n" +
			"    a = 3\n" +
			"    b = 4.5\n" +
			"    c = true\n" +
			"end",

			"def test()\n" +
			"    a[2] = {3,4}\n" +
			"    b[1+ 2] = {4.5, 2, 4}\n" +
			"    c[1] = {true}\n" +
			"end",

			"def test()\n" +
			"	bool result = true && false\n" +
			"	bool result = true && false || true\n" +
			"end ",

			"def test()\n" +
			"    bool result1 = 3 == 4\n" +
			"    result1 = 3 != 4\n" +
			"    result2 = true == true\n" +
			"    result3 = true != false\n" +
			"    result4 = 1 == 2\n" +
			"end ",

			"def test()\n" +
			"    bool result1 = 3 == 4\n" +
			"    result1 = 3 != 4\n" +
			"    result2 = true == true\n" +
			"    result3 = true != false\n" +
			"    result4 = 1 == 2\n" +
			"end",

			"def test()\n" +
			"    int a = 3 + 4\n" +
			"    int b = 4 - 3\n" +
			"    int c = 3 - 4 +5\n" +
			"end",

			"def test()\n" +
			"    int a = 5 * (3 + 4)\n" +
			"    bool result = true || (false && true)\n" +
			"end",

			"def test()\n" +
			"    float b = a[0] * 5\n" +
			"    b = a[1] + 5\n" +
			"    b = c[2] || true\n" +
			"    b = d[3] == true\n" +
			"    b = d[4] > 3\n" +
			"end "
	})
	public void validExprsTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"def test() get_all_routes(ids) end",
			"def test() clear_route(id) end",
			"def test() is_route_available(id, train) end",
			"def test() is_clear() end"
	})
	public void validFunctionCallTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"def inter() string ids[] = get routes from sig1 to sig2 end",
			"def inter() string id = get config route.source route1 end",
			"def inter() string asp = get state point1 end",
			"def inter() string asp = get state sig1 end",
			"def inter() set state sig1 to clear end",
			"def inter() set state sig1 to caution end",
			"def inter() set state sig1 to stop end",
			"def inter() set state point1 to normal end",
			"def inter() set state point1 to reverse end",
			"def inter() set config route.train route1 to cargo_green end",
			"def inter() grant route1 to cargo_green end",
			"def inter() bool b1 = is route1 available end",
			"def inter() bool b1 = is route1 not available end",
			"def inter() bool b1 = is seg1 occupied end",
			"def inter() bool b1 = is seg1 not occupied end",
			"def inter() int lm = get speed train1 end",
	})
	public void validSyntacticSugarTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}
}
