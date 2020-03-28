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
