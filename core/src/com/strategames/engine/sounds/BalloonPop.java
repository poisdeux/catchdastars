package com.strategames.engine.sounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class BalloonPop {

	//We use static class as class loading is thread safe
	static class SingletonHolder {
		private static final Sound INSTANCE = Gdx.audio.newSound(Gdx.files.internal("sounds/balloon_pop.mp3"));
	}
	
	private BalloonPop() {}
	
	static public Sound getInstance() {
		return SingletonHolder.INSTANCE;
	}
}
