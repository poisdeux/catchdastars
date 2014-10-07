package com.strategames.engine.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class Levels {
	private Array<Level> levels;
	
	public Levels() {
	}
	
	public void setLevels(Array<Level> levels) {
		this.levels = levels;
	}
	
	public Array<Level> getLevels() {
		return levels;
	}
	
	public String getJson() {
		Json json = new Json();
		json.setOutputType(OutputType.minimal);
		return json.toJson(this);
	}
	
	public void deleteLevel(Level level) {
		this.levels.removeValue(level, true);
	}
	
	public void addLevel(Level level) {
		this.levels.add(level);
	}
	
	public Level getLevel(int number) {
		return this.levels.get(number);
	}
}
