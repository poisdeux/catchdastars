/**
 * 
 * Copyright 2013 Martijn Brekhof
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
