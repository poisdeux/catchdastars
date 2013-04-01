package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class GameFile {

	final private String PATH = "levels";
	
	public void load(int level) {
		
	}
	
	public void save(Stage stage, int level) {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);
		String text = json.toJson(stage, Stage.class);
		
		FileHandle file = Gdx.files.local(PATH + "/" + level);
		file.writeString(text, false);
	}
}
