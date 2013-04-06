package com.strategames.catchdastars.utils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.catchdastars.actors.GameObject;

public class GameFile {

	static private String PATH = "levels";

	/**
	 * Loads packaged level files
	 * @param level
	 * @return ArrayList<GameObject>
	 */
	public ArrayList<GameObject> loadInternal(int level) {
		FileHandle file = Gdx.files.internal(PATH + "/" + level);
		return load(level, file);
	}
	
	/**
	 * Loads local level files saved using {@link #save(Stage, int)}
	 * @param level
	 * @return ArrayList<GameObject>
	 */
	public ArrayList<GameObject> loadLocal(int level) {
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
	public ArrayList<GameObject> load(int level, FileHandle file) {
		Json json = new Json();
		String text = file.readString();
		Object root =  json.fromJson(ArrayList.class, text);
		@SuppressWarnings("unchecked")
		ArrayList<GameObject> actors = (ArrayList<GameObject>) root;
		return actors;
	}
	
	/**
	 * Saves the content of stage to a local file.
	 * These files can be loaded using {@link #loadLocal(int)}
	 * @param stage
	 * @param level
	 */
	public void save(Stage stage, int level) {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);

		FileHandle file = Gdx.files.local(PATH + "/" + level);
		file.delete();

		Array<Actor> stageActors = stage.getActors();
		
		ArrayList<GameObject> arrayOfActors = new ArrayList<GameObject>();
		for(int i = 0; i < stageActors.size; i++) {
			arrayOfActors.add((GameObject) stageActors.get(i));
		}
		
		String text = json.toJson(arrayOfActors);
		file.writeString(text, true);
	}
}
