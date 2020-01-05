test-expr:
	mvn -pl de.uniba.swt.expr.tests -o integration-test
install-expr: 
	mvn -pl de.uniba.swt.expr -o clean install
install-test-expr: install-expr test-expr

expr-generate-sources:
	mvn -pl de.uniba.swt.expr -o generate-sources
exec-expr-test1:
	mvn -pl de.uniba.swt.expr -o exec:java@generatormain -Dexec.args='output/expr/test1.bahnexpr'