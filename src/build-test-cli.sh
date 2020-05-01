#!/bin/bash
set -e
cd "$(dirname "$0")"

# ensure gradle is build
sh build-gradle.sh

# unzip
MODULE=de.uniba.swt.dsl.cli.tests
BUILD=$MODULE/build
rm -rf $BUILD/bahnc*
unzip build/bahnc-*.zip -d $BUILD
mv $BUILD/bahnc-* $BUILD/bahnc

# run test
./gradlew $MODULE:test