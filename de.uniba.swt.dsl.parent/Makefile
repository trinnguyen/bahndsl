
# Expr projects
expr: expr-build expr-test
expr-build: 
	gradle de.uniba.swt.expr:build
expr-test:
	gradle de.uniba.swt.expr.tests:test
expr-generate:
	gradle de.uniba.swt.expr:generateXtextLanguage
expr-exec-interlocking1:
	gradle de.uniba.swt.expr:runGenerator --args='../samples/expr/interlocking1.bahnexpr'

# Bahn projects
bahn: bahn-build bahn-test
bahn-build: 
	gradle de.uniba.swt.dsl:build
bahn-test:
	gradle de.uniba.swt.dsl.tests:test
bahn-generate:
	gradle de.uniba.swt.dsl:generateXtextLanguage

bahn-exec-expr:
	gradle de.uniba.swt.dsl:runGenerator --args='../samples/bahn/expr.bahn'
bahn-exec-config:
	gradle de.uniba.swt.dsl:runGenerator --args='../samples/bahn/config.bahn'
bahn-exec-interlocking:
	gradle de.uniba.swt.dsl:runGenerator --args='../samples/bahn/interlocking.bahn'

# clean
clean:
	gradle clean