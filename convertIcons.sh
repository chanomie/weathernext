#!/bin/bash

#for SVG in `find src/main/webapp/imgs/icon -name "*.svg"`
#do
#	PNG=`echo ${SVG} | sed -e "s/svg/png/g"`
#	convert ${SVG} -density 1200 -resize 200x200 -crop 120x120+40+40 -fuzz 40% -transparent "white" ${PNG}
	# -crop 60x60+20+20 +repage
#done

for SVG in `find src/main/webapp/imgs/moon -name "*.svg"`
do
	PNG=`echo ${SVG} | sed -e "s/svg/png/g"`
	convert ${SVG} -density 1200 -rotate 180 -resize 110x110 -crop 100x100+5+5 -background "blue" -gravity center -extent 120x120 -fuzz 40% -transparent "blue" ${PNG}
	# -crop 60x60+20+20 +repage
done
