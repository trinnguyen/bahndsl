#!/bin/bash
cd "$(dirname "$0")" || exit
gradle de.uniba.swt.dsl:build
gradle de.uniba.swt.dsl.ide:build

# build folder
mkdir build
cd build || exit

# copy the file
cp ../de.uniba.swt.dsl/build/distributions/*.zip ./
cp ../de.uniba.swt.dsl.ide/build/distributions/*.zip ./