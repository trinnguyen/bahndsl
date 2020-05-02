# thesis-masters-bahndsl

Respository for Masters thesis on BahnDSL: A Domain-Specific Language for Configuring and Modelling Model Railways

## Download latest artifacts

### Bahn IDE
- [macOS: BahnIDE-macosx.cocoa.x86_64.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/src/build/BahnIDE-macosx.cocoa.x86_64.tar.gz?job=build-rcp-compiler)
- [Linux: BahnIDE-linux.gtk.x86_64.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/src/build/BahnIDE-linux.gtk.x86_64.tar.gz?job=build-rcp-compiler)
- [Windows: BahnIDE-win32.win32.x86_64.zip](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/src/build/BahnIDE-win32.win32.x86_64.zip?job=build-rcp-compiler)

### bahnc (Bahn Compiler CLI)
- [macOS/Linux/Windows: bahnc-1.0.0.zip](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/src/build/bahnc-1.0.0.zip?job=build-rcp-compiler)

### Test Reports
- [test-reports.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/src/build/test-reports.tar.gz?job=test)

### Bahn Language Support for VS Code (manually release to Marketplace)
- [Bahn Language VS Code Extension](https://marketplace.visualstudio.com/items?itemName=trinnguyen.bahn-language)

### Fix damaged macOS application
- Run command line: `xattr -c "Bahn IDE.app"`

## Usage

### Requirements
- Java SE 11 *([Download OpenJDK 11](https://adoptopenjdk.net/index.html?variant=openjdk11&jvmVariant=hotspot))*

### New project
- File -> New Project...
- Choose: General -> Project
- Input project name, example: SWTbahn

### New model inside the project
- File -> New File
- Select a project
- Input file name with `.bahn` as extension, example: `SWTbahnLite.bahn`
- In the dialog: *Do you want to convert '[PROJECT_NAME]' to an Xtext project?* -> **Yes**

### Compile model with Bahn IDE
- Generated YAML files are in `src-gen` folder

### Compile model with bahnc (Bahn Compiler)
- Compile to default output folder: `bahnc model.bahn`
- Compile to custom output folder: `bahnc model.bahn custom/src-gen`

## Development

### Source code location
- Folder **src**

### Build systems
- **maven**: Build Eclipse plugins and Bahn IDE standalone application (Eclipse RCP product)
- **gradle**: Build command line compiler, language server application and run tests
- **npm (Node JS)**:  Build Visual Studio Code extension

### Build scripts (Bash)

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

- Run integration tests for command line compiler
```
build-test-cli.sh
```