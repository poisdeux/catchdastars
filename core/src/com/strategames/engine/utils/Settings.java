package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class Settings {
	//We use static class as class loading is thread safe
	static class SingletonHolder {
		private static final Settings INSTANCE = new Settings();
	}
	
	private String MUSIC_VOLUME_PREF = "musicvolume";
	private String SFX_VOLUME_PREF = "sfxvolume";
	private Preferences prefs;
	
	private Settings() {
		this.prefs = Gdx.app.getPreferences("strategames-preferences");
	}
	
	public static Settings getInstance() {
        return SingletonHolder.INSTANCE;
    }
	
	public float getMusicVolume() {
		return this.prefs.getFloat(MUSIC_VOLUME_PREF, 0.7f);
	}
	
	public void setMusicVolume(float musicVolume) {
		this.prefs.putFloat(MUSIC_VOLUME_PREF, musicVolume);
	}
	
	public float getSfxVolume() {
		return this.prefs.getFloat(SFX_VOLUME_PREF, 1f);
	}
	
	public void setSfxVolume(float sfxVolume) {
		this.prefs.putFloat(SFX_VOLUME_PREF, sfxVolume);
	}
	
	/**
	 * Saves the settings to file
	 */
	public void save() {
		this.prefs.flush();
	}
}
