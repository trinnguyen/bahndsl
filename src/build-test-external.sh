#!/bin/bash
set -e
cd "$(dirname "$0")"

# ensure gradle is build
sh build-gradle.sh

# unzip
BUILD=de.uniba.swt.dsl.tests.external/build
rm -rf $BUILD/bahnc*
unzip build/bahnc-*.zip -d $BUILD
mv $BUILD/bahnc-* $BUILD/bahnc