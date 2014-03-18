#!/bin/bash

FLOATSCALE=6

function usage
{
	echo "usage: ${0} width height filename..."
	echo "width: width of image in mdpi"
  echo "height: height of image in mdpi"
	exit 1
}

function scale
{
	VALUE=$1
	SCALE=$2
	echo $(echo "scale=$FLOATSCALE; ($SCALE)*($VALUE)" | bc -q)
}

[ $# -lt 3 ] && usage

TMPFILE=$(mktemp)
WIDTH=$1
shift
HEIGHT=$1
shift

for FILENAME in "${@}"
do
  PNGFILENAME="${FILENAME%%.svg}.png"
	SWIDTH=$WIDTH
	SHEIGHT=$HEIGHT
	inkscape -z -D -f "${FILENAME}" -e "gameobjects/mdpi/$(basename ${PNGFILENAME})" -w ${SWIDTH} -h ${SHEIGHT}

	SWIDTH=$(scale $WIDTH "3/2")
	SHEIGHT=$(scale $HEIGHT "3/2")
	inkscape -z -D -f "${FILENAME}" -e "gameobjects/hdpi/$(basename ${PNGFILENAME})" -w ${SWIDTH} -h ${SHEIGHT}

	SWIDTH=$(scale $WIDTH "2")
	SHEIGHT=$(scale $HEIGHT "2")
	inkscape -z -D -f "${FILENAME}" -e "gameobjects/xhdpi/$(basename ${PNGFILENAME})" -w ${SWIDTH} -h ${SHEIGHT}

	SWIDTH=$(scale $WIDTH "3")
	SHEIGHT=$(scale $HEIGHT "3")
	inkscape -z -D -f "${FILENAME}" -e "gameobjects/xxhdpi/$(basename ${PNGFILENAME})" -w ${SWIDTH} -h ${SHEIGHT}
done 
