#!/bin/bash

FLOATSCALE=6

function usage
{
	echo "usage: ${0} width height filename..."
	echo "width: width of image in hdpi"
  echo "height: height of image in hdpi"
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

cat > ${TMPFILE} << 'EOF'
(define (scale-image infile
                      outfile
                      width
                      height)
  (let* ((image (car (gimp-file-load RUN-NONINTERACTIVE infile infile))))
    (gimp-context-set-interpolation 2)
    (gimp-image-scale image width height)
    (let* ((drawable (car (gimp-image-get-active-layer image))))
      (gimp-file-save RUN-NONINTERACTIVE image drawable outfile outfile))))
EOF


for FILENAME in "${@}"
do
	SWIDTH=$(scale $WIDTH "2/3")
	SHEIGHT=$(scale $HEIGHT "2/3")
	echo "(scale-image \"${FILENAME}\" \"gameobjects/mdpi/$(basename ${FILENAME})\" ${SWIDTH} ${SHEIGHT})"

	SWIDTH=$WIDTH
	SHEIGHT=$HEIGHT
	echo "(scale-image \"${FILENAME}\" \"gameobjects/hdpi/$(basename ${FILENAME})\" ${SWIDTH} ${SHEIGHT})"

	SWIDTH=$(scale $WIDTH "4/3")
	SHEIGHT=$(scale $HEIGHT "4/3")
	echo "(scale-image \"${FILENAME}\" \"gameobjects/xhdpi/$(basename ${FILENAME})\"  ${SWIDTH} ${SHEIGHT})"

	SWIDTH=$(scale $WIDTH "2")
	SHEIGHT=$(scale $HEIGHT "2")
	echo "(scale-image \"${FILENAME}\" \"gameobjects/xxhdpi/$(basename ${FILENAME})\"  ${SWIDTH} ${SHEIGHT})"
done  >> ${TMPFILE}

echo "(gimp-quit 0)" >> ${TMPFILE}

cat ${TMPFILE} | gimp -i -b -

