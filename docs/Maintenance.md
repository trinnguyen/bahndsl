# BahnDSL Development

## Development Environment
- OS: macOS, Windows, Linux
- Java JDK 11
- IDE (optional)
    - IntelliJ CE
    - Eclipse IDE for Java and DSL Developers
- Build systems (required)
    - **Maven**: build Eclipse-based IDE (RCP product)
    - **Gradle**: build compiler (cli), test, build language server application
    - **npm Node JS**: Visual Studio Code extension

## Project structure
- A mix of gradle and maven build system: a project can be both gradle or maven project
- Configuration files:
    - build.gradle (gradle)
    - pom.xml (maven)
- Projects
```
    de.uniba.swt.dsl
    de.uniba.swt.dsl.ide
    de.uniba.swt.dsl.tests 
    de.uniba.swt.dsl.product # maven
    de.uniba.swt.dsl.product.rcp # maven
    de.uniba.swt.dsl.target # maven
    de.uniba.swt.dsl.ui # maven
    vscode-bahn # visual studio code extension
```

### de.uniba.swt.dsl (Gradle/Maven)
- Core project implements the compiler
- Xtext Grammar
- Validators
- Normalisers
- Code generators
- Formatter
- Deployed as Eclipse plugin for RCP product or executable application (deployed as a command-line application)

### de.uniba.swt.dsl.ide (Gradle/Maven)
- Language server application
- Implement Language Server protocol
- Further customisation is optional (changing server configuration)
- Deployed as Eclipse plugin for RCP product or executable application
- The executable application is embedded into Visual Studio Code extension for generating code during development

### de.uniba.swt.dsl.tests (Gradle/Maven)
- JUnit 5 unit testing project
- Unit tests for all packages of the `de.uniba.swt.dsl` module
- Report code coverage with **jacoco**
- Expected coverage:
    - Instruction coverage: >= 80%
    - Branch coverage: >= 70%
- Important phases to be tested:
    - Parsing
    - Validation
    - Normalization
    - Code generation
    - Standalone application (for command line compiler)

### de.uniba.swt.dsl.cli.tests (Gradle/Maven)
- JUnit 5 integration tests
- End-to-end testing against built command-line compiler (**bahnc**)
- Require SCCharts compiler and C compiler installed in the host machine
- Important use case groups
    - Generate valid YAML files for the configuration module
    - Generate valid interlocking table and layout diagram
    - Generate valid SCCharts for the interlocking procedure
    - Generate valid low-level code with SCCharts compiler
    - Generate valid shared C library dynamically with C compiler

### Maven projects for building Eclipse-based IDE
- **de.uniba.swt.dsl.product**
    - Eclipse plugin project to define the dependencies to other projects/plugins
- **de.uniba.swt.dsl.product.rcp**
    - Eclipse product project for standalone application
- **de.uniba.swt.dsl.target**
    - Eclipse target project for third-party libraries and their remote sources
- **de.uniba.swt.dsl.ui**
    - Eclipse plugin project for customising the UI element of the Bahn IDE

## Build scripts

- Output folder: **build**

- Build Eclipse-based IDE (Bahn IDE)
```
sh build-maven-rcp.sh
```

- Build command line compiler **bahnc**
```
sh build-gradle.sh
```

- Build Visual Studio Code extension
```
sh build-gradle.sh
sh build-node-vscode.sh
```

- Publish Visual Studio Code extension (run in vscode-bahn)
```
vsce publish
```

- Run unit tests
```
sh build-test.sh
```

- Run integration tests for the command-line compiler
```
build-test-cli.sh
```