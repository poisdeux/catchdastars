#!/bin/bash
# Run this to setup required directory structure and 
# load required libraries
set -x
mkdir libgdx
pushd libgdx
wget -r http://libgdx.googlecode.com/files/libgdx-0.9.8.zip -O libgdx-0.9.8.zip
unzip libgdx-0.9.8.zip
popd
mkdir -p tools/physics-body-editor
pushd tools/physics-body-editor
wget -r http://box2d-editor.googlecode.com/files/physics-body-editor-2.9.2.zip -O physics-body-editor-2.9.2.zip
unzip physics-body-editor-2.9.2.zip
popd
