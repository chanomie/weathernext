#!/bin/bash

for SVG in `find src/main/webapp/imgs/icon -name "*.svg"`
do
	PNG=`echo ${SVG} | sed -e "s/svg/png/g"`
	convert ${SVG} -fuzz 40% -transparent "white" -crop 60x60+20+20 +repage ${PNG}
done
