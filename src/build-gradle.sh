#!/bin/bash
gradlew build

# build folder
mkdir build
cd build

# copy the file
cp ../de.uniba.swt.dsl/build/distributions/*.zip ./