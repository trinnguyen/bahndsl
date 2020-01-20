package de.uniba.swt.dsl.tests


import com.google.inject.Inject
import de.uniba.swt.dsl.bahn.RootModule
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

@ExtendWith(InjectionExtension)
@InjectWith(BahnInjectorProvider)

class ConfigurationParsingTest {
	@Inject extension ParseHelper<RootModule>
	@Inject extension ParserTestHelper
	
	@Test
	def void testPeripheralsProperty() {
		'''
			module lite
				 peripherals lightcontrol
				         lanterns 0x13
				             aspects
				                 on 0x00
				                 off 0x01
				             end
				             initial on
				     end
			end
		'''.parse.assertNoParsingErrors
	}
}