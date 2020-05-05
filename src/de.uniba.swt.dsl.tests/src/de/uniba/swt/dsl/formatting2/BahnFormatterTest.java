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

package de.uniba.swt.dsl.formatting2;

import com.google.inject.Inject;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.generator.StandardLibHelper;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.TestConstants;
import de.uniba.swt.dsl.tests.helpers.TestHelper;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.SaveOptions;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
class BahnFormatterTest {

    @Inject
    Serializer serializer;

    @Inject
    TestHelper testHelper;

    @Inject
    IResourceValidator validator;

    @Test
    public void testFormatterExpr() throws Exception {
        var fmt = formatCode("def test() end");
        var expected = "def test()\nend\n";
        assertEquals(expected, fmt);
    }

    @ParameterizedTest
    @ValueSource (strings = {
            TestConstants.SampleLayoutConfig,
            TestConstants.SampleLayoutDoubleSlipConfig,
            TestConstants.SampleTrainConfig1,
            TestConstants.SampleTrainConfig2,
    })
    public void testFormatConfigNoError(String src) throws Exception {
        var result = formatCode(src);

        // check again
        var resource = testHelper.parseValid(result);
        var model = BahnUtil.getRootModule(resource);
        assertTrue(model.getName() != null && !model.getName().isEmpty(), "Model name must be exist");
    }

    @ParameterizedTest
    @ValueSource (strings = {
            TestConstants.SampleOperators,
            TestConstants.SampleIfElse,
            TestConstants.SampleWhile,
            TestConstants.SampleRequestRouteForeach,
            TestConstants.SampleDriveRoute
    })
    public void testFormatExpressions(String src) throws Exception {
        var result = formatCode(src);
        // verify
        var resource = testHelper.parseValid(result);
        var decl = BahnUtil.getDecls(testHelper.parseValid(result)).get(0);
        assertTrue(decl.getName() != null && !decl.getName().isEmpty(), "Model name must be exist");
    }

    @Test
    public void testFormatRequestRoute() throws Exception {
        var result = formatCode(TestConstants.SampleRequestRouteForeach);

        // verify
        var resource = testHelper.parseValid(result);
        assertEquals("request_route", BahnUtil.getDecls(resource).get(0).getName());
    }

    @Test
    public void testFormatDriveRoute() throws Exception {
        var result = formatCode(TestConstants.SampleDriveRoute);

        // verify
        var resource = testHelper.parseValid(result);
        assertEquals("drive_route", BahnUtil.getDecls(resource).get(0).getName());
    }

    @Test
    public void testFormatStandardLib() throws Exception {
        var input = testHelper.parseValid("");
        StandardLibHelper.loadStandardLibResource(validator, input.getResourceSet());

        // find the standard lib resource
        Resource standardRes = null;
        for (Resource resource : input.getResourceSet().getResources()) {
            if (resource != input) {
                standardRes = resource;
                break;
            }
        }

        // format
        if (standardRes != null) {
            var out = formatResource(standardRes);
            assertTrue(out != null && !out.isEmpty(), "Formatted standard lib must not be empty");
        } else {
            fail("Failed to load the standard library");
        }
    }

    /**
     * BahnFormatter is called using Serializer
     * @param src
     * @return
     * @throws Exception
     */
    private String formatCode(String src) throws Exception {
        var input = testHelper.parseValid(src);
        return formatResource(input);
    }

    private String formatResource(Resource input) {
        return serializer.serialize(input.getContents().get(0), SaveOptions.newBuilder().format().getOptions());
    }
}