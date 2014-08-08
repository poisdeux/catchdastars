package com.strategames.engine.sounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Glass {

	//We use static class as class loading is thread safe
	static class SingletonHolder {
		private static final Sound INSTANCE = Gdx.audio.newSound(Gdx.files.internal("sounds/glass.ogg"));
	}
	
	private Glass() {}
	
	static public Sound getInstance() {
		return SingletonHolder.INSTANCE;
	}
}
