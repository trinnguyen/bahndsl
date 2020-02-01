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