package com.strategames.catchdastars.utils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class LevelWriter {

	/**
	 * Saves the content of stage to a local file.
	 * These files can be loaded using {@link #loadLocalSync(int)}
	 * @param stage
	 * @param level
	 */
	static public boolean save(Level level) {
		FileHandle file = Gdx.files.local(LevelLoader.getLocalPath(level.getLevelNumber()));
		try { 
			file.writeString(level.getJson(), false);
			return true;
		} catch (Exception e) {
			Gdx.app.log("LevelWriter", "save: could not write level: "+level+"\nError: "+e.getMessage());
			return false;
		}
	}
	
	/**
	 * Saves all levels in ArrayList levels
	 * @param levels to save
	 * @return ArrayList of levels that failed to save
	 */
	static public ArrayList<Level> save(ArrayList<Level> levels) {
		ArrayList<Level> levelsFailed = new ArrayList<Level>();
		for(Level level : levels) {
			if( ! LevelWriter.save(level) ) {
				Gdx.app.log("LevelWriter", "save: Failed to save level: "+level);
				levelsFailed.add(level);
			}
		}
		return levelsFailed;
	}
	
	static public boolean deleteLocalLevelsDir() {
		FileHandle file = Gdx.files.local(LevelLoader.getLocalPath());
		if( file.deleteDirectory() ) {
			return true;
		} else {
			Gdx.app.log("LevelWriter", "deleteLocalLevelsDir: failed to delete directory "+LevelLoader.getLocalPath());
			return false;
		}
	}
}
