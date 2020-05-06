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
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
public class ConfigurationValidationTest extends AbstractValidationTest {

    @Inject
    Provider<ResourceSet> resourceSetProvider;

    @Inject
    ParseHelper<BahnModel> parseHelper;

    @Inject
    ValidationTestHelper validationTestHelper;

    @ParameterizedTest
    @ValueSource(strings = {
            "module test\n" +
            "    boards\n" +
            "        master 0x00\n" +
            "    end\n" +
            "    segments master\n" +
            "        seg1 0x00 length 11cm\n" +
            "    end\n" +
            "    signals master\n" +
            "        entry sig1 0x01\n" +
            "    end\n" +
            "    blocks\n" +
            "        b1 main seg1\n" +
            "    end\n" +
            "    layout\n" +
            "        sig1 -- b1.down\n" +
            "    end\n" +
            "end",

            "module test\n" +
            "    boards\n" +
            "        master 0x00\n" +
            "    end\n" +
            "    segments master\n" +
            "        seg1 0x00 length 11cm\n" +
            "        seg2 0x01 length 11cm\n" +
            "        seg3 0x02 length 11cm\n" +
            "        seg4 0x03 length 11cm\n" +
            "    end\n" +
            "    points master\n" +
            "        p1 0x04 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
            "    end\n" +
            "    blocks\n" +
            "        b1 main seg2\n" +
            "        b2 main seg3\n" +
            "        b3 main seg4\n" +
            "    end\n" +
            "    layout\n" +
            "        b1.up -- p1.stem\n" +
            "        b2.up -- p1.straight\n" +
            "        b3.down -- p1.side\n" +
            "    end\n" +
            "end",

            "module test\n" +
            "    boards\n" +
            "        master 0x00\n" +
            "    end\n" +
            "    segments master\n" +
            "        seg1 0x00 length 11cm\n" +
            "        seg2 0x01 length 11cm\n" +
            "        seg3 0x02 length 11cm\n" +
            "        seg4 0x03 length 11cm\n" +
            "        seg5 0x04 length 11cm\n" +
            "    end\n" +
            "    points master\n" +
            "        p1 0x05 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
            "    end\n" +
            "    blocks\n" +
            "        b1 main seg2\n" +
            "        b2 main seg3\n" +
            "        b3 main seg4\n" +
            "        b4 main seg5\n" +
            "    end\n" +
            "    layout\n" +
            "        b1.up -- p1.down1\n" +
            "        b2.up -- p1.down2\n" +
            "        b3.down -- p1.up1\n" +
            "        b4.down -- p1.up2\n" +
            "    end\n" +
            "end",

            "module test\n" +
            "    boards\n" +
            "        master 0x00\n" +
            "    end\n" +
            "    segments master\n" +
            "        seg1 0x00 length 11cm\n" +
            "        seg2 0x01 length 11cm\n" +
            "        seg3 0x02 length 11cm\n" +
            "        seg4 0x03 length 11cm\n" +
            "        seg5 0x04 length 11cm\n" +
            "    end\n" +
            "    crossings\n" +
            "        c1 segment seg1\n" +
            "    end\n" +
            "    blocks\n" +
            "        b1 main seg2\n" +
            "        b2 main seg3\n" +
            "        b3 main seg4\n" +
            "        b4 main seg5\n" +
            "    end\n" +
            "    layout\n" +
            "        b1.up -- c1.down1\n" +
            "        b2.up -- c1.down2\n" +
            "        b3.down -- c1.up1\n" +
            "        b4.down -- c1.up2\n" +
            "        b1.down -- b2.down\n" +
            "        b3.up -- b4.up\n" +
            "    end\n" +
            "end"
    })
    public void validLayoutTest(String src) {
        validationTestHelper.assertNoErrors(internalParse(src));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            TestConstants.SampleEmptyConfig,
            TestConstants.SampleStandardConfig,
            TestConstants.SampleLiteConfig,
            TestConstants.SampleConfigSignals,
            TestConstants.SampleLayoutConfig,
            TestConstants.SampleTrainConfig1,
            TestConstants.SampleTrainConfig2,
    })
    public void validSampleCode(String src) {
        validationTestHelper.assertNoErrors(internalParse(src));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "module test boards master 0x00 end segments master end segments master end end",
            "module test boards master 0x00 end signals master end signals master end end",
            "module test boards master 0x00 end points master end points master end end",
            "module test boards master 0x00 end peripherals master end peripherals master end end"
    })
    public void errorMultipleSectionsByBoard(String src) {
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.ROOT_MODULE, null, "section is already defined for board");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "module test boards master 0x00 end blocks end blocks end end",
            "module test boards master 0x00 end platforms end platforms end end",
            "module test boards master 0x00 end crossings end crossings end end",
            "module test boards master 0x00 end trains end trains end end",
            "module test boards master 0x00 end layout end layout end end",
    })
    public void errorMultipleSection(String src) {
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.ROOT_MODULE, null, "section is already defined");
    }

    @Test
    public void errorPointConnectorTest() {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master " +
                "       seg1 0x01 length 11cm " +
                "       seg2 0x02 length 11cm " +
                "       seg3 0x03 length 11cm " +
                "   end\n" +
                "    points master\n" +
                "        p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
                "    end\n" +
                "    blocks\n" +
                "        b1 main seg2\n" +
                "        b2 main seg3\n" +
                "    end\n" +
                "    layout\n" +
                "        p1.stem -- b1.down\n" +
                "        p1.straight -- b2.down\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "p1 must connect to 3 different");
    }

    @Test
    public void testErrorBlockConnector() throws Exception {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master " +
                "       seg1 0x01 length 11cm\n" +
                "       seg2 0x02 length 11cm\n" +
                "       seg3 0x03 length 11cm\n" +
                "   end\n" +
                "    blocks\n" +
                "        b1 main seg2\n" +
                "        b2 main seg3\n" +
                "    end\n" +
                "    layout\n" +
                "        b1.down -- b2.down\n" +
                "        b1.up -- b2.down\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "b1 must connect to 2 different");
    }

    @Test
    public void testErrorNotConnected() {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master\n" +
                "       seg1 0x01 length 11cm\n" +
                "       seg2 0x02 length 11cm\n" +
                "       seg3 0x03 length 11cm\n" +
                "    end" +
                "    signals master\n" +
                "       entry sig1 0x00\n" +
                "    end" +
                "    blocks\n" +
                "        b1 main seg1\n" +
                "        b2 main seg2\n" +
                "        b3 main seg3\n" +
                "    end\n" +
                "    layout\n" +
                "        b1.down -- sig1\n" +
                "        b2.up -- b3.down\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "Network layout is not strongly connected");
    }

    @Test
    public void testErrorNetworkBuilderDuplicatedBlockDirection() throws Exception {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master seg1 0x01 length 11cm end\n" +
                "    blocks\n" +
                "        b1 main seg1\n" +
                "    end\n" +
                "    layout\n" +
                "        b1.down -> b1.up\n" +
                "        b1.up -> b1.down\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "Direction of", "is already defined");
    }

    @Test
    public void testErrorNetworkBuilderSignal2() throws Exception {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master seg1 0x01 length 11cm end\n" +
                "    signals master\n" +
                "        exit sig1 0x01\n" +
                "    end\n" +
                "    points master\n" +
                "        p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
                "    end\n" +
                "    layout\n" +
                "        p1.stem -- sig1\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "Signal can only connect to a block");
    }

    @Test
    public void testErrorNetworkBuilderSignal1() throws Exception {
        var src = "module test\n" +
                "    boards master 0x01 end\n" +
                "    segments master seg1 0x01 length 11cm end\n" +
                "    signals master\n" +
                "        entry sig1 0x01\n" +
                "    end\n" +
                "    points master\n" +
                "        p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal\n" +
                "    end\n" +
                "    layout\n" +
                "        sig1 -- p1.stem\n" +
                "    end\n" +
                "end";
        validationTestHelper.assertError(internalParse(src), BahnPackage.Literals.LAYOUT_PROPERTY, null, "Signal can only connect to a block");
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
