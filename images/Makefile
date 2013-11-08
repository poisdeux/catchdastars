SHELL = /bin/sh

all:
	./resizeall.sh
	make movepacked

movepacked:
	for density in mdpi hdpi xhdpi xxhdpi ; do \
		mv gameobjects/$$density/*.png gameobjects/$$density/packed; \
		mv gameobjects/$$density/packed/bricks-*.png gameobjects/$$density/; \
		mv gameobjects/$$density/packed/splashscreen.png gameobjects/$$density/; \
	done

pack:
	./pack.sh mdpi		
	./pack.sh hdpi		
	./pack.sh xhdpi		
	./pack.sh xxhdpi		
