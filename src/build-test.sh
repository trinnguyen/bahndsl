#!/bin/bash
export JAVA_HOME=$(/usr/libexec/java_home -v 11)

set -e
cd "$(dirname "$0")"
./gradlew clean de.uniba.swt.dsl.test:test de.uniba.swt.dsl.test:jacocoTestReport || exit 1

# build folder
mkdir -p build
cd build || exit 1

# copy the file
cp -r ../de.uniba.swt.dsl.tests/build/reports ./
tar zcf test-reports.tar.gz reports
rm -rf reports