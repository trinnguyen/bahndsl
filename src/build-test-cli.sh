#!/bin/bash
export JAVA_HOME=$(/usr/libexec/java_home -v 11)

set -e
cd "$(dirname "$0")"

# ensure gradle is built
sh build-gradle.sh || exit 1

# unzip
MODULE=de.uniba.swt.dsl.cli.tests
BUILD=$MODULE/build
rm -rf $BUILD
unzip build/bahnc-*.zip -d $BUILD
mv $BUILD/bahnc-* $BUILD/bahnc

# run test
./gradlew ${MODULE}:test