package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class LevelEditorPreferences {
	private final String keySnapToGrid = "snapToGrid";
	private final String keyDisplayGrid = "displayGrid";
	
	private Preferences preferences;
	
	public LevelEditorPreferences() {
		this.preferences = Gdx.app.getPreferences("LevelEditorPreferences");
	}
	
	public boolean snapToGridEnabled() {
		return this.preferences.getBoolean(keySnapToGrid, false);
	}
	
	public void snapToGrid(boolean bool) {
		this.preferences.putBoolean(keySnapToGrid, bool);
		this.preferences.flush();
	}
	
	public boolean displayGridEnabled() {
		return this.preferences.getBoolean(keyDisplayGrid, false);
	}
	
	public void displayGrid(boolean bool) {
		this.preferences.putBoolean(keyDisplayGrid, bool);
		this.preferences.flush();
	}
}
