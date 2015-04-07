SHELL = /bin/sh

all:
	make setup
	./resizeall.sh
	for density in mdpi hdpi xhdpi xxhdpi xxxhdpi; do \
		mv gameobjects/$$density/*.png gameobjects/$$density/pack; \
		mv gameobjects/$$density/pack/bricks-*.png gameobjects/$$density/; \
		mv gameobjects/$$density/pack/splashscreen.png gameobjects/$$density/; \
		mv gameobjects/$$density/pack/Loading.png gameobjects/$$density/; \
		mv gameobjects/$$density/pack/dot.png gameobjects/$$density/; \
	done
	make pack

setup:
	@mkdir -p gameobjects/mdpi/pack
	@mkdir -p gameobjects/hdpi/pack
	@mkdir -p gameobjects/xhdpi/pack
	@mkdir -p gameobjects/xxhdpi/pack
	@mkdir -p gameobjects/xxxhdpi/pack

pack:
	./pack.sh mdpi		
	./pack.sh hdpi		
	./pack.sh xhdpi		
	./pack.sh xxhdpi		
	./pack.sh xxxhdpi		

install:
	for density in mdpi hdpi xhdpi xxhdpi xxxhdpi; do \
		cp -r gameobjects/$$density/packed/* ../../android/assets/packed/; \
		cp gameobjects/$$density/*.png ../../android/assets/images/$$density/; \
	done

clean:
	rm -rf gameobjects
