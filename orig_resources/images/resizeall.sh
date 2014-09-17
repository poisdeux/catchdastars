#!/bin/bash

./resizeimages.sh 315 99 originals/Loading.xcf
./resizeimages.sh 27 99 originals/dot.xcf
./resizeimages.sh 48 48 originals/bricks-texture-*.png
./resizeimages.sh 24 24 originals/Chalk-0*.png
./resizeimages.sh 48 49 originals/ice_cube/icecube-part*.png originals/ice_cube/icecube.xcf
./resizeimages.sh 320 31 originals/splashscreen.xcf

./resizesvg.sh 48 67 originals/aj_balloon_*.svg
./resizesvg.sh 24 24 originals/gridpoint.svg originals/icon-menu.svg
./resizesvg.sh 48 48 originals/star_*.svg
./resizesvg.sh 190 201 originals/cash_register.svg
./resizesvg.sh 48 48 originals/passagetonextlevel.svg

