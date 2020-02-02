# Bahn projects
default: build test
build: 
	gradle de.uniba.swt.dsl:build
test:
	gradle de.uniba.swt.dsl.tests:test
generate:
	gradle de.uniba.swt.dsl:generateXtextLanguage
web:
	gradle de.uniba.swt.dsl.web:jettyRun

# clean
clean:
	gradle clean