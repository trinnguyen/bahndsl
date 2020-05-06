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

import com.google.inject.Inject;
import de.uniba.swt.dsl.tests.BahnInjectorProvider;
import de.uniba.swt.dsl.tests.helpers.ParserTestHelper;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
public class ConfigurationParsingTest {

	@Inject
	private ParserTestHelper parserTestHelper;

	@ParameterizedTest
	@ValueSource(strings = { 
			"module full end",

			"module standard end",

			"module test\n" +
			"    segments master\n" +
			"        seg1 0x00 length 34cm\n" +
			"    end\n" +
			"end",

			"module test\n" +
			"    signals master\n" +
			"        entry sig1 0x00\n" +
			"        distant sig2 0x02\n" +
			"        composite sig3 signals sig1 sig2 end\n" +
			"    end\n" +
			"end",

			"module test\n" +
			"    layout\n" +
			"        b1.up -- p1.stem\n" +
			"        b2.up -- p1.straight\n" +
			"        b3.down -- p1.side\n" +
			"    end\n" +
			"end",

			"module test\n" +
			"    layout\n" +
			"        b1.up -- p1.down1\n" +
			"        b2.up -- p1.down2\n" +
			"        b3.down -- p1.up1\n" +
			"        b4.down -- p1.up2\n" +
			"    end\n" +
			"end",

			"module test\n" +
			"    layout\n" +
			"        sig1 -- b1.down\n" +
			"        sig2 -- b2.up\n" +
			"    end\n" +
			"end",
			
			"module test\n" +
			"    points onecontrol\n" +
			"        point1 0x00 \n" +
			"        segment seg1\n" +
			"        normal 0x01\n" +
			"        reverse 0x00\n" +
			"        initial normal\n" +
			"    end\n" +
			"end",

			"module test\n" +
			"    platforms\n" +
			"        platform1 overlap seg1 main seg1 overlap seg2\n" +
			"    end\n" +
			"end",

			"module test\n" +
			"     peripherals lightcontrol\n" +
			"             peripheral lanterns 0x13\n" +
			"         end\n" +
			"end"
			})
	public void validConfigurationTest(String src) {
		parserTestHelper.assertNoParsingErrors(src);
	}

}
