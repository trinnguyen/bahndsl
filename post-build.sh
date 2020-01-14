#!/bin/bash

# new folder
mkdir build

# move
echo "Move all artifacts to build folder"
cp de.uniba.swt.dsl.parent/de.uniba.swt.dsl.product.rcp/target/products/BahnIDE-macosx.cocoa.x86_64.tar.gz ./build/BahnIDE-macosx.cocoa.x86_64.tar.gz
cp de.uniba.swt.dsl.parent/de.uniba.swt.dsl.product.rcp/target/products/BahnIDE-linux.gtk.x86_64.tar.gz ./build/BahnIDE-linux.gtk.x86_64.tar.gz
cp de.uniba.swt.dsl.parent/de.uniba.swt.dsl.product.rcp/target/products/BahnIDE-win32.win32.x86_64.zip ./build/BahnIDE-win32.win32.x86_64.zip

cd build
# unzip and remove extended attributes
echo "Fix damanged macOS package"
tar xzf BahnIDE-macosx.cocoa.x86_64.tar.gz
xattr -c *.app

# zip back
echo "Package macOS product back"
tar zcf BahnIDE-macosx.cocoa.x86_64.tar.gz *.app
rm -rf *.app