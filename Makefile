verify:
	mvn -f de.uniba.swt.dsl.parent clean verify
build:
	mvn -f de.uniba.swt.dsl.parent clean install
build-rcp:
	mvn -f de.uniba.swt.dsl.parent -pl de.uniba.swt.dsl.product.rcp -am clean install