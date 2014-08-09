package com.strategames.engine.sounds;

import java.util.HashMap;

import com.badlogic.gdx.audio.Sound;
import com.strategames.engine.utils.Settings;

abstract public class SoundEffect {

	private Sound soundEffect;
	private static HashMap<String, Sound> soundsMap = new HashMap<String, Sound>();

	private static float preferenceVolumeSfx = Settings.getInstance().getSfxVolume();

	public SoundEffect() {
		this.soundEffect = soundsMap.get(getClass().getSimpleName());
		if( this.soundEffect == null ) {
			this.soundEffect = getSound();
			soundsMap.put(getClass().getSimpleName(), this.soundEffect);
		}
	}

	static public void releaseAll() {
		for( String name : soundsMap.keySet() ) {
			soundsMap.get(name).dispose();
		}
		soundsMap.clear();
	}

	public void release() {
		this.soundEffect.dispose();
		soundsMap.remove(getClass().getSimpleName());
	}

	public void load() {
		this.soundEffect = soundsMap.get(getClass().getSimpleName());
		if( this.soundEffect == null ) {
			this.soundEffect = getSound();
			soundsMap.put(getClass().getSimpleName(), this.soundEffect);
		}
	}

	public void play(float volume) {
		soundEffect.play(volume * preferenceVolumeSfx);
	}

	public void play() {
		soundEffect.play(preferenceVolumeSfx);
	}

	static public SoundEffect getSoundForIncrement(int increment) {
		SoundEffect sound = null;

		if( increment > 49 ) {
			sound = new CoinsDropManySound();
		} else if ( increment > 9 ) {
			sound = new CoinsDropSound();
		} else {
			sound = new CoinsDropSingleSound();
		}

		return sound;
	}

	static public void setVolume(float volume) {
		if( volume < 0 ) {
			preferenceVolumeSfx = 0;
		} else if (volume > 1) {
			preferenceVolumeSfx = 1;
		} else {
			preferenceVolumeSfx = volume;
		}
	}

	abstract protected Sound getSound();
}
