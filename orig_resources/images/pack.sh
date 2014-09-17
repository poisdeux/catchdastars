#!/bin/bash

function usage 
{
	echo $0 density
	exit 1
}

[ $# -lt 1 ] && usage

DENSITY="$1"

export CLASSPATH=libgdx/gdx.jar:libgdx/gdx-tools.jar

java com.badlogic.gdx.tools.texturepacker.TexturePacker gameobjects/"${DENSITY}"/pack gameobjects/"${DENSITY}"/packed "${DENSITY}"
