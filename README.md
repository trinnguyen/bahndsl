# thesis-masters-bahndsl

Respository for Masters thesis on BahnDSL: A Domain-Specific Language for Configuring and Modelling Model Railways

## Download latest artifacts

- [macOS: BahnIDE-macosx.cocoa.x86_64.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/de.uniba.swt.dsl.parent/de.uniba.swt.dsl.product.rcp/target/products/BahnIDE-macosx.cocoa.x86_64.tar.gz?job=install:jdk11)
- [Linux: BahnIDE-linux.gtk.x86_64.tar.gz](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/de.uniba.swt.dsl.parent/de.uniba.swt.dsl.product.rcp/target/products/BahnIDE-linux.gtk.x86_64.tar.gz?job=install:jdk11)
- [Windows: BahnIDE-win32.win32.x86_64.zip](https://gitlab.rz.uni-bamberg.de/swt/teaching/2019-ws/thesis-masters-bahndsl/-/jobs/artifacts/develop/raw/de.uniba.swt.dsl.parent/de.uniba.swt.dsl.product.rcp/target/products/BahnIDE-win32.win32.x86_64.zip?job=install:jdk11)

### Workaround damaged macOS application
- Fix issue on opening on macOS: *“Eclipse” is damaged and can’t be opened. You should move it to the Bin.*
- Run cmd after decompressing: `xattr -c Eclipse.app`

## Development
- Build and run all tests: `make verify`
- Development with `gradle`: `gradle build`