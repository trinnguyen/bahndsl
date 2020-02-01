# thesis-masters-bahndsl

Respository for Masters thesis on BahnDSL: A Domain-Specific Language for Configuring and Modelling Model Railways

## Download latest artifacts

### Bahn IDE
- [macOS: BahnIDE-macosx.cocoa.x86_64.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/src/build/BahnIDE-macosx.cocoa.x86_64.tar.gz?job=install:jdk11)
- [Linux: BahnIDE-linux.gtk.x86_64.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/src/build/BahnIDE-linux.gtk.x86_64.tar.gz?job=install:jdk11)
- [Windows: BahnIDE-win32.win32.x86_64.zip](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/src/build/BahnIDE-win32.win32.x86_64.zip?job=install:jdk11)

### bahnc (Bahn Compiler CLI)
- [macOS/Linux/Windows: bahnc-1.0.0-SNAPSHOT.zip](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/src/build/bahnc-1.0.0-SNAPSHOT.zip?job=install:jdk11)

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
- Build and run all tests with Tycho Maven: `make verify`
- Development with gradle: `gradle build`