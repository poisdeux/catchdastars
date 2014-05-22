package com.strategames.catchdastars.utils;

import java.util.ArrayList;
import java.util.Collections;

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
	
	public void deleteLevel(Level level) {
		this.levels.remove(level);
	}
	
	/**
	 * Renumbers all levels so the numbering is sequential again
	 * after deleting one or more levels
	 */
	public void renumberLevels() {
		Collections.sort(this.levels);
		
		int levelNumber = 1;
		
		for(Level level : levels) {
			level.setLevelNumber(levelNumber++);
			LevelWriter.save(level);
		}
	}
}
