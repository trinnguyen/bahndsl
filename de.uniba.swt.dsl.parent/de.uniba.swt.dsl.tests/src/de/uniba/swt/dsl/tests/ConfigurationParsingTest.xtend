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
	def void testSegmentWithLength() {
		'''
			module test
				segments master
					seg1 0x00 length 34cm
				end
			end
		'''.parse.assertNoParsingErrors
	}
	
	@Test
	def void testBlock() {
		'''
			module test
				blocks
					block2 buffer:seg7,signal4 <-> main:seg6 <-> buffer:seg5,signal3 
						trains
							cargo passenger 
						end
				end
			end
		'''.parse.assertNoParsingErrors
	}
	
	@Test
	def void testRoute() {
		'''
			module test
				routes
			    	route1
			    		source signal1
			    		destination signal2
			    		path seg1 seg2 end
			    		points 
			    			point1 normal 
			    			point2 reverse 
						end
			    		signals signal3 signal4 end
			    		conflicts route2 route3 end
			    end
			end
		'''.parse.assertNoParsingErrors
	}
	
	@Test
	def void testPeripheralsProperty() {
		'''
			module test
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