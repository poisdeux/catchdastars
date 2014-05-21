#!/bin/bash
# Run this to setup required directory structure and 
# load required libraries
set -x
mkdir libgdx
pushd libgdx
wget -r http://libgdx.badlogicgames.com/releases/libgdx-1.0.1.zip
unzip libgdx-1.0.1.zip
popd
mkdir -p tools/physics-body-editor
pushd tools/physics-body-editor
wget -r http://box2d-editor.googlecode.com/files/physics-body-editor-2.9.2.zip -O physics-body-editor-2.9.2.zip
unzip physics-body-editor-2.9.2.zip
popd
