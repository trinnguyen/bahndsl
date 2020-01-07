test-expr:
	mvn -pl de.uniba.swt.expr.tests -o integration-test
install-expr: 
	mvn -pl de.uniba.swt.expr -o clean install
install-test-expr: install-expr test-expr

test-bahn:
	mvn -pl de.uniba.swt.dsl.tests -o integration-test
install-bahn: 
	mvn -pl de.uniba.swt.dsl -o clean install
install-test-bahn: install-bahn test-bahn

expr-generate-sources:
	mvn -pl de.uniba.swt.expr -o generate-sources
exec-expr-test1:
	mvn -pl de.uniba.swt.expr -o exec:java@generatormain -Dexec.args='output/expr/test1.bahnexpr'