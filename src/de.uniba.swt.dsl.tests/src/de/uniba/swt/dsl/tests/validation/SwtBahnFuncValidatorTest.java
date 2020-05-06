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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
public class SwtBahnFuncValidatorTest extends AbstractValidationTest {

    @Inject
    Provider<ResourceSet> resourceSetProvider;

    @Inject
    ParseHelper<BahnModel> parseHelper;

    @Inject
    ValidationTestHelper validationTestHelper;

    // @Test
    public void warningMissing2Functions() {
        var src = "def test() end";
        validationTestHelper.assertWarning(internalParse(src), BahnPackage.Literals.BAHN_MODEL, null, "Neither request_route nor drive_route is implemented");
    }

    // @Test
    public void warningMissingDriveRoute() {
        var src = "def request_route(string src_signal_id, string dst_signal_id, string train_id): string return \"\" end";
        validationTestHelper.assertWarning(internalParse(src), BahnPackage.Literals.BAHN_MODEL, null, "drive_route is not implemented");
    }

    // @Test
    public void warningMissingRequestRoute() {
        var src = "def drive_route(string route_id, string train_id) end";
        validationTestHelper.assertWarning(internalParse(src), BahnPackage.Literals.BAHN_MODEL, null, "request_route is not implemented");
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
