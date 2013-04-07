package com.strategames.catchdastars.utils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.catchdastars.actors.GameObject;

public class Level {
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
		FileHandle file = Gdx.files.internal(PATH + "/" + level);
		return load(level, file);
	}
	
	/**
	 * Loads local level files saved using {@link #save(Stage, int)}
	 * @param level
	 * @return ArrayList<GameObject>
	 */
	static public Level loadLocal(int level) {
		FileHandle file = Gdx.files.local(PATH + "/" + level);
		return load(level, file);
	}

	/**
	 * Loads level file from FileHandle.
	 * You should never need to use this. Use {@link #loadInternal(int)} or {@link #loadLocal(int)} instead.
	 * @param level
	 * @param file
	 * @return ArrayList<GameObject>
	 */
	static public Level load(int level, FileHandle file) {
		Json json = new Json();
		String text = file.readString();
		Object root =  json.fromJson(ArrayList.class, text);
		return (Level) root;
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
}
