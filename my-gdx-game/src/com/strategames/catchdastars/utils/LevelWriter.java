package com.strategames.catchdastars.utils;

import java.util.ArrayList;
import java.util.Collections;

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
	
	/**
	 * Deletes the local file for the given level
	 * @param level
	 */
	static public boolean deleteLocal(int level) {
		try {
			FileHandle file = Gdx.files.local(LevelLoader.getLocalPath(level));
			if( file.delete() ) {
				reorderLevelFiles();
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	static private void reorderLevelFiles() {
		ArrayList<Level> levels = LevelLoader.loadAllLocalLevels();
		Collections.sort(levels);
		
		if(! LevelWriter.deleteLocalLevelsDir()) {
			return;
		}
		
		int levelNumber = 1;
		
		for(Level level : levels) {
			level.setLevelNumber(levelNumber++);
			LevelWriter.save(level);
		}
	}
}
