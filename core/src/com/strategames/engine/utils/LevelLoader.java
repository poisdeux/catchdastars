package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

public class LevelLoader {

	static private final String LOCAL_PATH = "levels";
	static private final String INTERNAL_PATH = "levels";
	static private OnLevelLoadedListener levelLoadedListener;

	public interface OnLevelLoadedListener {
		public void onLevelLoaded(Level level);
	}

	/**
	 * Loads packaged level files (synchronous)
	 * @param name name of the level file
	 * @return Level object containing the game objects
	 */
	static private Level loadInternalSync(String name) {
		try {
			FileHandle file = Gdx.files.internal(INTERNAL_PATH + "/" + name);
			return loadSync(file);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Loads local level files (synchronous) saved using {@link LevelWriter#save(Stage, int)}
	 * @param pos position of the level
	 * @return Level object containing the game objects 
	 */
	static public Level loadLocalSync(int[] pos) {
		return loadLocalSync(pos[0]+","+pos[1]);
	}
	
	/**
	 * Loads local level files (synchronous) saved using {@link LevelWriter#save(Stage, int)}
	 * @param level levelnumber to load
	 * @return Level object containing the game objects 
	 */
	static public Level loadLocalSync(String name) {
		try {
			FileHandle file = Gdx.files.local(LOCAL_PATH + "/" + name);
			return loadSync(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Loads local level files (asynchronous) saved using {@link #save(Stage, int)}
	 * <br/>
	 * Use {@link #levelLoaded()} to see if loading has finished. After loading you
	 * can retrieve the Level object using {@link #getLevel()} 
	 * @param level levelnumber to load
	 */
	static private void loadLocalAsync(int level, OnLevelLoadedListener listener) {
		levelLoadedListener = listener;
		try {
			FileHandle file = Gdx.files.local(LOCAL_PATH + "/" + level);
			loadAsync(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads level file (synchronous) from FileHandle.
	 * You should never need to use this. Use {@link #loadInternalSync(int)} or {@link #loadLocalSync(int)} instead.
	 * @param file
	 * @return Level object containing the game objects 
	 */
	static private Level loadSync(FileHandle file) {
		Json json = new Json();
		try {
			String text = file.readString();
			Object root =  json.fromJson(Level.class, text);
			return (Level) root;
		} catch (GdxRuntimeException e) {
			Gdx.app.log("LevelLoader", "Runtime error while loading level: "+e.getMessage());
		} catch (SerializationException e) {
			Gdx.app.log("LevelLoader", "Serialization error while loading level: "+e.getMessage());
		}
		return null;
	}

	/**
	 * Loads level file (asynchronous) from FileHandle.
	 * You should never need to use this. Use {@link #loadInternalAsync(int)} or {@link #loadLocalAsync(int)} instead.
	 * @param file
	 */
	static private void loadAsync(final FileHandle file) {
		Thread thread = new Thread( new Runnable() {

			@Override
			public void run() {
				Json json = new Json();
				String text = file.readString();
				Gdx.app.log("LevelLoader", "loadAsync: text="+text);
				Object root =  json.fromJson(Level.class, text);
				if( levelLoadedListener != null ) {
					levelLoadedListener.onLevelLoaded((Level) root);
				}
			}
		});

		thread.start();
	}

	static public Array<Level> loadAllLocalLevels() {
		FileHandle dir = getLocalLevelsDir();
		FileHandle[] files = dir.list();

		Array<Level> levels = new Array<Level>();

		for( FileHandle file : files ) {
			Level level = loadSync(file);
			if( level != null ) {
				levels.add(level);
			}
		}

		return levels;
	}

	/**
	 * Creates an ArrayList of Level objects using jsonString as
	 * json input
	 * @param jsonString the json input containing a Levels block with one or more levels
	 * @return ArrayList of type Level
	 */
	static public Array<Level> getLevels(String jsonString) {
		Json json = new Json();
		try {
			Levels levels = json.fromJson(Levels.class, jsonString);
			return levels.getLevels();
		} catch (Exception e) {
			Gdx.app.log("LevelLoader", "getLevels: error parsing json: "+e.getMessage());
			return null;
		}
	}

	static public FileHandle getLocalLevelsDir() {
		try {
			FileHandle dir = Gdx.files.local(LOCAL_PATH);
			return dir;
		} catch (Exception e) {
			//			Gdx.app.log("Level", "error");
		}
		return null;
	}

	static public FileHandle getInternalLevelsDir() {
		try {
			FileHandle dir = Gdx.files.internal(INTERNAL_PATH);
			return dir;
		} catch (Exception e) {
			//			Gdx.app.log("Level", "error");
		}
		return null;
	}

	static public String getLocalPath() {
		return LOCAL_PATH;
	}

	static public String getLocalPath(String name) {
		return LOCAL_PATH + "/" + name;
	}

	static public String getInternalPath() {
		return INTERNAL_PATH;
	}

	static public String getInternalPath(String name) {
		return INTERNAL_PATH + "/" + name;
	}

	/**
	 * Reads the amount of level files on disk to determine
	 * the last level number
	 * @return
	 */
	public static int getLastLevelNumber() {

		FileHandle dir = getLocalLevelsDir();
		FileHandle[] files = dir.list();
		return files.length;
	}


}
