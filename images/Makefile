SHELL = /bin/sh

setup:
	@mkdir -p gameobjects/mdpi/pack
	@mkdir -p gameobjects/hdpi/pack
	@mkdir -p gameobjects/xhdpi/pack
	@mkdir -p gameobjects/xxhdpi/pack

all:
	make setup
	./resizeall.sh
	for density in mdpi hdpi xhdpi xxhdpi ; do \
		mv gameobjects/$$density/*.png gameobjects/$$density/pack; \
		mv gameobjects/$$density/pack/bricks-*.png gameobjects/$$density/; \
		mv gameobjects/$$density/pack/splashscreen.png gameobjects/$$density/; \
		mv gameobjects/$$density/pack/Loading.png gameobjects/$$density/; \
		mv gameobjects/$$density/pack/dot.png gameobjects/$$density/; \
	done
	make pack

pack:
	./pack.sh mdpi		
	./pack.sh hdpi		
	./pack.sh xhdpi		
	./pack.sh xxhdpi		

install:
	for density in mdpi hdpi xhdpi xxhdpi ; do \
		cp -r /home/mbrekhof/projects/gdx/PopDaBalloons/images/gameobjects/$$density/packed/* /home/mbrekhof/projects/gdx/PopDaBalloons/my-gdx-game-android/assets/packed/; \
		cp /home/mbrekhof/projects/gdx/PopDaBalloons/images/gameobjects/$$density/*.png /home/mbrekhof/projects/gdx/PopDaBalloons/my-gdx-game-android/assets/images/$$density/; \
	done
