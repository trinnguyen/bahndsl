#!/bin/bash
cd "$(dirname "$0")"
./gradlew de.uniba.swt.dsl:build
./gradlew de.uniba.swt.dsl.ide:build

# build folder
mkdir build
cd build

# copy the file
cp ../de.uniba.swt.dsl/build/distributions/*.zip ./