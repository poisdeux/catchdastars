#!/bin/bash

function usage 
{
	echo $0 density
	exit 1
}

[ $# -lt 1 ] && usage

DENSITY="$1"

export CLASSPATH=../libgdx/gdx.jar:../libgdx/extensions/gdx-tools/gdx-tools.jar

java com.badlogic.gdx.tools.imagepacker.TexturePacker2 ~/projects/gdx/PopDaBalloons/images/gameobjects/"${DENSITY}"/packed ~/projects/gdx/PopDaBalloons/my-gdx-game-android/assets/packed/ "${DENSITY}"
