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

	static private String PATH = "levels";

	public void setGameObjects(ArrayList<GameObject> gameObjects) {
		this.gameObjects = gameObjects;
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

	/**
	 * Loads packaged level files
	 * @param level
	 * @return ArrayList<GameObject>
	 */
	static public Level loadInternal(int level) {
		try {
			FileHandle file = Gdx.files.internal(PATH + "/" + level);
			return load(file);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Loads local level files saved using {@link #save(Stage, int)}
	 * @param level
	 * @return ArrayList<GameObject>
	 */
	static public Level loadLocal(int level) {
		try {
			FileHandle file = Gdx.files.local(PATH + "/" + level);
			return load(file);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Loads level file from FileHandle.
	 * You should never need to use this. Use {@link #loadInternal(int)} or {@link #loadLocal(int)} instead.
	 * @param level
	 * @param file
	 * @return ArrayList<GameObject>
	 */
	static public Level load(FileHandle file) {
		Json json = new Json();
		String text = file.readString();
		Object root =  json.fromJson(Level.class, text);
		return (Level) root;
	}

	static public ArrayList<Level> loadAllLocalLevels() {
		FileHandle dir = getLocalLevelsDir();
		FileHandle[] files = dir.list();
		
		ArrayList<Level> levels = new ArrayList<Level>();
		
		for( FileHandle file : files ) {
			levels.add(load(file));
		}
		
		return levels;
	}
	
	static public FileHandle getLocalLevelsDir() {
		try {
			FileHandle dir = Gdx.files.local(PATH);
			return dir;
		} catch (Exception e) {
//			Gdx.app.log("Level", "error");
		}
		return null;
	}

	static public FileHandle getInternalLevelsDir() {
		try {
			FileHandle dir = Gdx.files.internal(PATH);
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
			FileHandle file = Gdx.files.local(PATH + "/" + level);
			if( file.delete() ) {
				reorderLevels(level);
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	static private void reorderLevels(int number) {
		ArrayList<Level> levels = loadAllLocalLevels();
		Collections.sort(levels);
		
		int levelNumber = 1;
		
		for(Level level : levels) {
			level.setLevelNumber(levelNumber++);
			level.save();
		}
	}

	/**
	 * Saves the content of stage to a local file.
	 * These files can be loaded using {@link #loadLocal(int)}
	 * @param stage
	 * @param level
	 */
	public void save() {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);

		FileHandle file = Gdx.files.local(PATH + "/" + this.number);
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
