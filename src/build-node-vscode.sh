#!/bin/bash
cd "$(dirname "$0")"

# copy file
cd vscode-bahn
rm -rf bahn-ide-server
unzip ../build/bahn-ide-server*.zip -d ./
mv bahn-ide-server* bahn-ide-server

# build
npm run package
cd ..

# build folder
mkdir build
cd build

# copy the file
cp ../vscode-bahn/*.vsix ./