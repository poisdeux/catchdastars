package com.strategames.catchdastars.utils;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class Levels {
	private ArrayList<Level> levels;
	
	public Levels() {
		// TODO Auto-generated constructor stub
	}
	
	public void setLevels(ArrayList<Level> levels) {
		this.levels = levels;
	}
	
	public ArrayList<Level> getLevels() {
		return levels;
	}
	
	public String getJson() {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);
		return json.toJson(this);
	}
}
