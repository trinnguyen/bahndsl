# thesis-masters-bahndsl

Respository for Masters thesis on BahnDSL: A Domain-Specific Language for Configuring and Modelling Model Railways

## Download latest artifacts

- [macOS: BahnIDE-macosx.cocoa.x86_64.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/build/BahnIDE-macosx.cocoa.x86_64.tar.gz?job=install:jdk11)
- [Linux: BahnIDE-linux.gtk.x86_64.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/build/BahnIDE-linux.gtk.x86_64.tar.gz?job=install:jdk11)
- [Windows: BahnIDE-win32.win32.x86_64.zip](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/build/BahnIDE-win32.win32.x86_64.zip?job=install:jdk11)

### Fix damaged macOS application
- Run command line: `xattr -c "Bahn IDE.app"`


## Requirements
- Java SE 11 ([Download OpenJDK 11](https://adoptopenjdk.net/index.html?variant=openjdk11&jvmVariant=hotspot))

## Create new Bahn model

### New project
- File -> New Project...
- Choose: General -> Project
- Input project name, example: SWTbahn

### New model inside the project
- File -> New File
- Select a project
- Input file name with `.bahn` as extension, example: `SWTbahnLite.bahn`
- In the dialog: *Do you want to convert '[PROJECT_NAME]' to an Xtext project?* -> **Yes**

### Build model with Bahn IDE
- Generated YAML files are in `src-gen` folder

## Development
- Build and run all tests: `make verify`
- Development with `gradle`: `gradle build`