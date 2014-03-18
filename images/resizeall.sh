#!/bin/bash

./resizeimages.sh 315 99 originals/Loading.xcf
./resizeimages.sh 27 99 originals/dot.xcf
./resizeimages.sh 32 32 originals/bricks-texture-*.png
./resizeimages.sh 16 16 originals/Chalk-0*.png
./resizeimages.sh 32 33 originals/ice_cube/icecube-part*.png originals/ice_cube/icecube.xcf
./resizeimages.sh 320 31 originals/splashscreen.xcf

./resizesvg.sh 32 45 originals/aj_balloon_*.svg
./resizesvg.sh 16 16 originals/gridpoint.svg
./resizesvg.sh 32 32 originals/star_*.svg
./resizesvg.sh 127 134 originals/cash_register.svg
./resizesvg.sh 16 16 originals/gridpoint.svg

