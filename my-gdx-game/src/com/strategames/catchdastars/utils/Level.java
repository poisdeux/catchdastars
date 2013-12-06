package com.strategames.catchdastars.utils;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.catchdastars.actors.GameObject;

public class Level implements Comparable<Level> {
	private int number;
	private String name;
	private ArrayList<GameObject> gameObjects;

	static private String INTERNAL_PATH = "levels";
	static private String LOCAL_PATH = "levels";
	
	static private Level level;
	static private LevelLoaded levelLoadedListener;
	
	public interface LevelLoaded {
		public void onLevelLoaded(Level level);
	}
	
	public void setGameObjects(ArrayList<GameObject> gameObjects) {
		this.gameObjects = new ArrayList<GameObject>();
		for( GameObject object : gameObjects ) {
			if( object.getSaveToFile() ){
				this.gameObjects.add(object);
			}
		}
	}

	public ArrayList<GameObject> getGameObjects() {
		return this.gameObjects;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setLevelNumber(int number) {
		this.number = number;
	}

	public int getLevelNumber() {
		return this.number;
	}

	public static Level getLevel() {
		return level;
	}
	
	/**
	 * Loads packaged level files (synchronous)
	 * @param level
	 * @return Level object containing the game objects
	 */
	static public Level loadInternalSync(int level) {
		try {
			FileHandle file = Gdx.files.internal(INTERNAL_PATH + "/" + level);
			return loadSync(file);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Loads local level files (synchronous) saved using {@link #save(Stage, int)}
	 * @param level levelnumber to load
	 * @return Level object containing the game objects 
	 */
	static public Level loadLocalSync(int level) {
		try {
			FileHandle file = Gdx.files.local(LOCAL_PATH + "/" + level);
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
	static public void loadLocalAsync(int level, LevelLoaded listener) {
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
	static public Level loadSync(FileHandle file) {
		Json json = new Json();
		String text = file.readString();
		Object root =  json.fromJson(Level.class, text);
		return (Level) root;
	}
	
	/**
	 * Loads level file (asynchronous) from FileHandle.
	 * You should never need to use this. Use {@link #loadInternalAsync(int)} or {@link #loadLocalAsync(int)} instead.
	* @param file
	 */
	static public void loadAsync(final FileHandle file) {
		Thread thread = new Thread( new Runnable() {
			
			@Override
			public void run() {
				Json json = new Json();
				String text = file.readString();
				Object root =  json.fromJson(Level.class, text);
				if( levelLoadedListener != null ) {
					levelLoadedListener.onLevelLoaded((Level) root);
				}
			}
		});
		
		thread.run();
	}
	
	static public ArrayList<Level> loadAllLocalLevels() {
		FileHandle dir = getLocalLevelsDir();
		FileHandle[] files = dir.list();
		
		ArrayList<Level> levels = new ArrayList<Level>();
		
		for( FileHandle file : files ) {
			levels.add(loadSync(file));
		}
		
		return levels;
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

	/**
	 * Deletes the local file for the given level
	 * @param level
	 */
	static public boolean deleteLocal(int level) {
		try {
			FileHandle file = Gdx.files.local(LOCAL_PATH + "/" + level);
			if( file.delete() ) {
				reorderLevelFiles();
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	static public String getLocalPath(int level) {
		return LOCAL_PATH + "/" + level;
	}
	
	static public String getInternalPath(int level) {
		return INTERNAL_PATH + "/" + level;
	}
	
	static private void reorderLevelFiles() {
		ArrayList<Level> levels = loadAllLocalLevels();
		Collections.sort(levels);
		
		if(! deleteLocalLevelsDir()) {
			return;
		}
		
		int levelNumber = 1;
		
		for(Level level : levels) {
			level.setLevelNumber(levelNumber++);
			level.save();
		}
	}

	static private boolean deleteLocalLevelsDir() {
		FileHandle file = Gdx.files.local(LOCAL_PATH);
		return file.deleteDirectory();
	}
	
	/**
	 * Saves the content of stage to a local file.
	 * These files can be loaded using {@link #loadLocalSync(int)}
	 * @param stage
	 * @param level
	 */
	public void save() {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);

		FileHandle file = Gdx.files.local(LOCAL_PATH + "/" + this.number);
		file.delete();

		String text = json.toJson(this);
		file.writeString(text, true);
	}

	@Override
	public int compareTo(Level o) {
		if( this.number > o.getLevelNumber() ) {
			return 1;
		} else if( this.number == o.getLevelNumber() ) {
			return 0;
		} else {
			return -1;
		}
	}
}
