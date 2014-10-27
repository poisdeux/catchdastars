package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class LevelWriter {

	/**
	 * Saves the content of stage to a local file.
	 * These files can be loaded using {@link #loadLocalSync(int)}
	 * @param stage
	 * @param level
	 */
	static public boolean save(Level level) {
		int[] position = level.getPosition();
		FileHandle file = Gdx.files.local(LevelLoader.getLocalPath(position[0]+","+position[1]));
		Gdx.app.log("LevelWriter", "save: saving level="+level+" to "+file.path());
		try {
			Json json = new Json();
			file.writeString(json.prettyPrint(level.getJson()), false);
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
	static public Array<Level> save(Array<Level> levels) {
		Array<Level> levelsFailed = new Array<Level>();
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
		if( file.isDirectory() ) {
			if( file.deleteDirectory())  {
				return true;
			}
		} else if( file.exists() ) {
			if( file.delete() ) {
				return true;
			}
		} else {
			return true; // directory does not exist
		}
		Gdx.app.log("LevelWriter", "deleteLocalLevelsDir: failed to delete directory "+LevelLoader.getLocalPath());
		return false;
	}
	
	/**
	 * Deletes the local file for the given level
	 * @param level
	 */
	static public boolean deleteLocal(Level level) {
		try {
			FileHandle file = Gdx.files.local(LevelLoader.getLocalPath(level.getPositionAsString()));
			return file.delete();
		} catch (Exception e) {
			return false;
		}
	}
}
