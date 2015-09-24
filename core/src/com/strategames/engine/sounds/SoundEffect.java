/**
 * 
 * Copyright 2014 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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

	/**
	 * This method is the reason we do not use a singleton design
	 * We want to be able to release all sounds easily without needing
	 * to keep a separate list of all used sounds
	 */
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

	public void stop() {
		soundEffect.stop();
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
