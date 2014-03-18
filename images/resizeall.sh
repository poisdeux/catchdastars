#!/bin/bash

./resizeimages.sh 315 99 originals/Loading.png
./resizeimages.sh 27 99 originals/dot.png
./resizeimages.sh 32 32 originals/bricks-texture-*.png
./resizeimages.sh 16 16 originals/Chalk-0*.png originals/gridpoint.png
./resizeimages.sh 32 33 originals/ice_cube/icecube-part*.png originals/ice_cube/icecube.png
./resizeimages.sh 320 31 originals/splashscreen.png

./resizesvg.sh 32 45 originals/aj_balloon_*.svg
./resizesvg.sh 16 16 originals/gridpoint.svg
./resizesvg.sh 32 32 originals/star_*.svg
./resizesvg.sh 127 134 originals/cash_register.svg

