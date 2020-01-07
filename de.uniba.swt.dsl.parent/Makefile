
# Expr projects
expr: expr-install expr-test
expr-install: 
	mvn -pl de.uniba.swt.expr -o clean install
expr-test:
	mvn -pl de.uniba.swt.expr.tests -o integration-test
expr-generate:
	mvn -pl de.uniba.swt.expr -o generate-sources
expr-exec-test1:
	mvn -pl de.uniba.swt.expr -o exec:java@generatormain -Dexec.args='samples/expr/interlocking1.bahnexpr' -Dtycho.mode=maven

# Bahn projects
bahn: bahn-install bahn-test
bahn-install: 
	mvn -pl de.uniba.swt.dsl -o clean install
bahn-test:
	mvn -pl de.uniba.swt.dsl.tests -o integration-test
bahn-generate:
	mvn -pl de.uniba.swt.dsl -o generate-sources