/*
 * generated by Xtext 2.20.0
 */
package de.uniba.swt.dsl.tests.validation

import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import de.uniba.swt.dsl.bahn.RootModule
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import de.uniba.swt.dsl.bahn.BahnPackage
import de.uniba.swt.dsl.tests.BahnInjectorProvider

@ExtendWith(InjectionExtension)
@InjectWith(BahnInjectorProvider)
class LayoutValidationTest {
	@Inject extension ParseHelper<RootModule>
	@Inject extension ValidationTestHelper

	@Test
    def void testErrorNetworkBuilderSignal1() {
        '''
			module test
				boards master 0x01 end
				segments master seg1 0x01 length 11cm end
				aspects red 0x01 end
				signals master
					sig1 0x01 aspects red end initial red
				end
				points master
				    p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal
				end
				layout
					sig1 -- p1.stem
				end
			end
		'''.parse.assertError(BahnPackage.Literals.LAYOUT_PROPERTY, null, "Signal can only connect to a block")
    }
    
    @Test
    def void testErrorNetworkBuilderSignal2() {
        '''
			module test
				boards master 0x01 end
				segments master seg1 0x01 length 11cm end
				aspects red 0x01 end
				signals master
					sig1 0x01 aspects red end initial red
				end
				points master
				    p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal
				end
				layout
					p1.stem -- sig1
				end
			end
		'''.parse.assertError(BahnPackage.Literals.LAYOUT_PROPERTY, null, "Signal can only connect to a block")
    }
    
    @Test
    def void testErrorNetworkBuilderDuplicatedBlockDirection() {
        '''
			module test
				boards master 0x01 end
				segments master seg1 0x01 length 11cm end
				blocks
					b1 main seg1
				end
				layout
					b1.down -> b1.up
					b1.up -> b1.down
				end
			end
		'''.parse.assertError(BahnPackage.Literals.LAYOUT_PROPERTY, null, "Block direction is already defined")
    }
    
    @Test
    def void testErrorNotConnected() {
        '''
			module test
				boards master 0x01 end
				segments master seg1 0x01 length 11cm end
				aspects red 0x01 end
					signals master
						sig1 0x01 aspects red end initial red
					end
				blocks
					b1 main seg1
					b2 main seg1
					b3 main seg1
				end
				layout
					b1.down -- sig1
					b2.up -- b3.down
				end
			end
		'''.parse.assertError(BahnPackage.Literals.LAYOUT_PROPERTY, null, "Network layout is not strongly connected")
    }
    @Test
    def void testErrorBlockConnector() {
        '''
			module test
				boards master 0x01 end
				segments master seg1 0x01 length 11cm end
				points master
				    p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal
				end
				blocks
					b1 main seg1
					b2 main seg1
				end
				layout
					b1.down -- p1.stem
					b1.up -- p1.side
				end
			end
		'''.parse.assertError(BahnPackage.Literals.LAYOUT_PROPERTY, null, "Block must connect to 2 different blocks: b1")
    }
    
    @Test
    def void testErrorPointConnector() {
        '''
			module test
				boards master 0x01 end
				segments master seg1 0x01 length 11cm end
				points master
				    p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal
				end
				blocks
					b1 main seg1
					b2 main seg1
				end
				layout
					p1.stem -- b1.down
					p1.straight -- b2.down
					p1.side -- b1.up
				end
			end
		'''.parse.assertError(BahnPackage.Literals.LAYOUT_PROPERTY, null, "Point must connect to 3 different blocks: p1")
    }
	@Test
    def void testLayoutSwitch() {
		'''
			module test
				boards
					master 0x00
				end
				segments master
					seg1 0x00 length 11cm
				end
				points master
				    p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal
				end
				blocks
					b1 main seg1
					b2 main seg1
					b3 main seg1
				end
				layout
					b1.up -- p1.stem
					b2.up -- p1.straight
					b3.down -- p1.side
				end
			end
		'''.parse.assertNoErrors
	}
	
	@Test
	def void testLayoutCrossing() {
		'''
			module test
				boards
					master 0x00
				end
				segments master
					seg1 0x00 length 11cm
				end
				points master
				    p1 0x00 segment seg1 normal 0x00 reverse 0x00 initial normal
				end
				blocks
					b1 main seg1
					b2 main seg1
					b3 main seg1
					b4 main seg1
				end
				layout
					b1.up -- p1.down1
					b2.up -- p1.down2
					b3.down -- p1.up1
					b4.down -- p1.up2
				end
			end
		'''.parse.assertNoErrors
	}
	
	@Test
	def void testLayoutSignal() {
		'''
			module test
				aspects
					red 0x00
				end
				boards
					master 0x00
				end
				segments master
					seg1 0x00 length 11cm
				end
				signals master
					sig1 0x00 aspects red end initial red
				end
				blocks
					b1 main seg1
				end
				layout
				    sig1 -- b1.down
				end
			end
		'''.parse.assertNoErrors
	}
}
