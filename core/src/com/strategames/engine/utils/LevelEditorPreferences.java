package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class LevelEditorPreferences {
	private static final String keySnapToGrid = "snapToGrid";
	private static final String keyDisplayGrid = "displayGrid";
	
	private static Preferences preferences;
	
	private LevelEditorPreferences() {
		
	}
	
	public static boolean snapToGridEnabled() {
		return getPreferences().getBoolean(keySnapToGrid, false);
	}
	
	public static void snapToGrid(boolean bool) {
		getPreferences();
		preferences.putBoolean(keySnapToGrid, bool);
		preferences.flush();
	}
	
	public static boolean displayGridEnabled() {
		getPreferences();
		return preferences.getBoolean(keyDisplayGrid, false);
	}
	
	public static void displayGrid(boolean bool) {
		getPreferences();
		preferences.putBoolean(keyDisplayGrid, bool);
		preferences.flush();
	}
	
	public static Preferences getPreferences() {
		if( preferences == null ) {
			preferences = Gdx.app.getPreferences("LevelEditorPreferences");
		}
		return preferences;
	}
}
