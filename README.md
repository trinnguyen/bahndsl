# thesis-masters-bahndsl

Repository for Masters thesis on BahnDSL: A Domain-Specific Language for Configuring and Modelling Model Railways

## Download latest artefacts

### Bahn IDE
- [macOS: BahnIDE-macosx.cocoa.x86_64.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/master/raw/src/build/BahnIDE-macosx.cocoa.x86_64.tar.gz?job=build-maven)
- [Linux: BahnIDE-linux.gtk.x86_64.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/master/raw/src/build/BahnIDE-linux.gtk.x86_64.tar.gz?job=build-maven)
- [Windows: BahnIDE-win32.win32.x86_64.zip](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/master/raw/src/build/BahnIDE-win32.win32.x86_64.zip?job=build-maven)

### bahnc (Bahn Compiler CLI)
- [macOS/Linux/Windows: bahnc-1.0.0.zip](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/master/raw/src/build/bahnc-1.0.0.zip?job=build-gradle)

### Test Reports
- [test-reports.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/master/raw/src/build/test-reports.tar.gz?job=test)

### Bahn Language Support for VS Code (manually release to Marketplace)
- [Bahn Language VS Code Extension](https://marketplace.visualstudio.com/items?itemName=trinnguyen.bahn-language)

### Fix damaged macOS application
- Run command line: `xattr -c "Bahn IDE.app"`

## Usage

### Requirements
- Java SE 11 *([Download OpenJDK 11](https://adoptopenjdk.net/index.html?variant=openjdk11&jvmVariant=hotspot))*

### Bahn IDE
- Create new project
  - File -> New -> Bahn Project
  - Input project name, for example: SWTbahn
  - Finish
  - A new project with default BahnDSL source file named: `untitled.bahn` is created
- Create new BahnDSl file
  - File -> New -> Bahn File
  - Input name
  - New BahnDSL model is created with given name, contains empty railway configuration model and interlocking functions

- Output
  - Generated YAML files, SCCharts models and layout diagram are in `src-gen` folder

### bahnc (command-line compiler)
- Execute the command without argument to show help menu, for example: `bahnc`
```
OVERVIEW: Bahn compiler

USAGE: bahnc file [-o <path>] [-m <mode>] [-v] [-d]
  -o <path> output folder
  -m <mode> code generation mode (default, c-code, library)
  -v    verbose output
  -d    debug output

EXAMPLE: 
  bahnc example.bahn
  bahnc -m library -v example.bahn
  bahnc -o output/src-gen example.bahn
```

- Add `-v` to enable verbose output
- Add `-o <path>` to change output folder. Default is `src-gen`
- Add `-d` for detailed log level (recommended for development only) 
- Compile BahnDSL source code for getting YAML files and SCCharts models
  ```
  bahnc example.bahn -v
  ```
- Compile BahnDSL source code with YAML files, SCCharts models and low-level C code
  ```
  bahnc example.bahn -m c-code -v
  ```
- Compile BahnDSL source code with YAML files, SCCharts models and interlocking shared library
  ```
  bahnc example.bahn -m library -v
  ```

### Visual Studio Code extension

- Install from Marketplace via the link: https://marketplace.visualstudio.com/items?itemName=trinnguyen.bahn-language
- Install from local package, for example: `code --install-extension  bahn-language.vsix`
- Open the BahnDSL source code in Visual Studio Code (with `.bahn` extension)
- The compiler is triggered automatically and generates the output files on file changed.
- The default output folder is `src-gen`

## Syntax
Details in [docs/BahnDSLSyntax.md](docs/BahnDSLSyntax.md).

## Maintenance

Details in [docs/Maintenance.md](docs/Maintenance.md)

## Examples
Two examples for configuring the SWTbahn Standard, SWTbahn Lite are attached in addition to a default interlocking procedures:

- SWTbahn Standard: [examples/standard/SWTbahnStandard.bahn](examples/standard/SWTbahnStandard.bahn)
- SWTbahn Lite: [examples/lite/SWTbahnLite.bahn](examples/lite/SWTbahnLite.bahn)
- Default interlocking procedure: [examples/interlocking/default.bahn](examples/interlocking/default.bahn)