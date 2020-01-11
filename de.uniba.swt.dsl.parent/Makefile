
# Expr projects
expr: expr-build expr-test
expr-build: 
	gradle de.uniba.swt.expr:build
expr-test:
	gradle de.uniba.swt.expr.tests:test
expr-generate:
	gradle de.uniba.swt.expr:generateXtextLanguage
expr-web:
	gradle de.uniba.swt.expr.web:jettyRun

# Bahn projects
bahn: bahn-build bahn-test
bahn-build: 
	gradle de.uniba.swt.dsl:build
bahn-test:
	gradle de.uniba.swt.dsl.tests:test
bahn-generate:
	gradle de.uniba.swt.dsl:generateXtextLanguage
bahn-web:
	gradle de.uniba.swt.dsl.web:jettyRun

bahn-exec-expr:
	gradle de.uniba.swt.dsl:runGenerator --args='../samples/bahn/expr.bahn'
bahn-exec-config:
	gradle de.uniba.swt.dsl:runGenerator --args='../samples/bahn/config.bahn'
bahn-exec-interlocking:
	gradle de.uniba.swt.dsl:runGenerator --args='../samples/bahn/interlocking.bahn'

# clean
clean:
	gradle clean

# testing
expr-exec-interlocking1:
	gradle de.uniba.swt.expr:runGenerator --args='../samples/expr/interlocking1.bahnexpr'
expr-exec-linking1:
	gradle de.uniba.swt.expr:runGenerator --args='../samples/expr/linking1.bahnexpr'