#!/bin/bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

cd "$(dirname "$0")" || exit 1
./mvnw clean install || exit 1

# build folder
mkdir build
cd build || exit 1

# move
echo "Move all artifacts to build folder"
cp ../de.uniba.swt.dsl.product.rcp/target/products/BahnIDE-macosx.cocoa.aarch64.tar.gz ./BahnIDE-macosx.cocoa.aarch64.tar.gz
cp ../de.uniba.swt.dsl.product.rcp/target/products/BahnIDE-macosx.cocoa.x86_64.tar.gz ./BahnIDE-macosx.cocoa.x86_64.tar.gz
cp ../de.uniba.swt.dsl.product.rcp/target/products/BahnIDE-linux.gtk.x86_64.tar.gz ./BahnIDE-linux.gtk.x86_64.tar.gz
cp ../de.uniba.swt.dsl.product.rcp/target/products/BahnIDE-win32.win32.x86_64.zip ./BahnIDE-win32.win32.x86_64.zip