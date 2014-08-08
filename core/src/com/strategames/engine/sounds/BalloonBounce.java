package com.strategames.engine.sounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class BalloonBounce {

	//We use static class as class loading is thread safe
	static class SingletonHolder {
		private static final Sound INSTANCE = Gdx.audio.newSound(Gdx.files.internal("sounds/single_balloon_bounce.ogg"));
	}
	
	private BalloonBounce() {}
	
	static public Sound getInstance() {
		return SingletonHolder.INSTANCE;
	}
}
