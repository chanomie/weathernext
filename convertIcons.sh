#!/bin/bash

for SVG in `find src/main/webapp/imgs/icon -name "*.svg"`
do
	PNG=`echo ${SVG} | sed -e "s/svg/png/g"`
	convert ${SVG} -density 1200 -resize 200x200 -crop 120x120+40+40 -fuzz 40% -transparent "white" ${PNG}
	# -crop 60x60+20+20 +repage
done
