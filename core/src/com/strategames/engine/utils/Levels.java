package com.strategames.engine.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.engine.gameobject.types.Door;

public class Levels {
	private Array<Level> levels;
	
	public Levels() {
		this.levels = new Array<Level>();
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
	
	public Level getLevel(int row, int column) {
		for(Level level : this.levels) {
			int[] pos = level.getPosition();
			if((pos[0]==row)&&(pos[1]==column)) {
				return level;
			}
		}
		return null;
	}
	
	/**
	 * Returns an array of levels reachable from the given level
	 * @param level from which to start looking for reachable levels
	 * @param reachableLevels should be empty at start and will contain the reachableLevels at completion
	 */
	public void getLevelsReachable(Level level, Array<Level> reachableLevels) {
		if( level == null ) {
			return;
		}
		
		Array<Door> doors = level.getDoors();
		for(int i = 0; i < doors.size; i++) {
			int[] nextPos = doors.get(i).getNextLevelPosition();
			Level adjacentLevel = getLevel(nextPos[0], nextPos[1]);
			if( ( adjacentLevel != null ) && ( ! reachableLevels.contains(adjacentLevel, true) ) ) { // prevent looping when levels have doors to eachother
				reachableLevels.add(adjacentLevel);
				getLevelsReachable(adjacentLevel, reachableLevels);
			}
		}
	}
}
