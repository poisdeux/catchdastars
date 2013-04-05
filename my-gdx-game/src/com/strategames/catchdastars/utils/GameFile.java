package com.strategames.catchdastars.utils;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class GameFile {

	static private String PATH = "levels";

	public ArrayList<Actor> load(int level) {
		Json json = new Json();
		FileHandle file = Gdx.files.local(PATH + "/" + level);
		String text = file.readString();
		Object root =  json.fromJson(ArrayList.class, text);
		@SuppressWarnings("unchecked")
		ArrayList<Actor> actors = (ArrayList<Actor>) root;
		return actors;
	}

	public void save(Stage stage, int level) {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);

		FileHandle file = Gdx.files.local(PATH + "/" + level);
		file.delete();

		Array<Actor> stageActors = stage.getActors();
		
		ArrayList<Actor> arrayOfActors = new ArrayList<Actor>();
		for(int i = 0; i < stageActors.size; i++) {
			arrayOfActors.add(stageActors.get(i));
		}
		
		String text = json.toJson(arrayOfActors);
		file.writeString(text, true);
	}
}
