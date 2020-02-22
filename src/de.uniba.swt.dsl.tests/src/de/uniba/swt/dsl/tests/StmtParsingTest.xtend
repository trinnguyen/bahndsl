/*
 * generated by Xtext 2.20.0
 */
package de.uniba.swt.dsl.tests

import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import de.uniba.swt.dsl.bahn.RootModule

@ExtendWith(InjectionExtension)
@InjectWith(BahnInjectorProvider)
class StmtParsingTest {
	
	@Inject extension ParseHelper<RootModule>
	@Inject extension ParserTestHelper

	@Test
	def void testIterationStmt() {
		'''
			module test 
				def test()
					int sum = 0
					int i = 0
					while i < 10
					    sum = sum + i
					    i = i + 1
					end
				end 
			end
		'''.parse.assertNoParsingErrors
	}
	
	@Test
	def void testIfOnly() {
		'''
			module test 
				def test()
					if true
						o = 1
					end
				end 
			end
		'''.parse.assertNoParsingErrors
	}

	@Test
	def void testIfWithElse() {
		'''
			module test def test()
				if true
					o = 1
				else
					o = 2
				end
			end end
		'''.parse.assertNoParsingErrors
	}

	@Test
	def void testDanglingElse() {
		'''
			module test def test()
				if true
					if false
						o = 2
					else 
						o = 3
					end
				end
			end end
		'''.parse.assertNoParsingErrors
	}
	
	@Test
	def void testObjectReturnStmt() {
		'''
			module test def test()
				return nil
			end end
		'''.parse.assertNoParsingErrors
	}

	@Test
	def void testIntReturnStmt() {
		'''
			module test def test()
				return 1
			end end
		'''.parse.assertNoParsingErrors
	}
}
