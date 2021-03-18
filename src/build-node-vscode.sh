#!/bin/bash
cd "$(dirname "$0")" || exit 1

# copy file
cd vscode-bahn || exit 1
rm -rf bahn-ide-server
unzip ../build/bahn-ide-server*.zip -d ./
mv bahn-ide-server* bahn-ide-server

# build
npm install
npm run package
cd ..

# build folder
mkdir build
cd build || exit 1

# copy the file
cp ../vscode-bahn/*.vsix ./