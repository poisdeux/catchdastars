package com.strategames.engine.utils;

import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.utils.FileWriter.Writer;

public class Game implements Json.Serializable, Writer {
	private Array<Level> levels;
	private String uuid;
	private String name;
	private String designer;
	private int[] currentLevelPosition = {0,0};
	private int score;

	public Game() {
		this.levels = new Array<Level>();
		this.uuid = UUID.randomUUID().toString();
	}

	@Override
	public void write(Json json) {
		json.writeValue("uuid", uuid);
		json.writeValue("name", name);
		json.writeValue("designer", designer);
		json.writeValue("currentLevelPosition", currentLevelPosition);
		json.writeValue("score", score);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		JsonValue child = jsonData.child();
		while( child != null ) {
			if(child.name.contentEquals("uuid")) {
				uuid = child.asString();
			} else if(child.name.contentEquals("name")) {
				name = child.asString();
			} else if(child.name.contentEquals("designer")) {
				designer = child.asString();
			} else if(child.name.contentEquals("currentLevelPosition")) {
				currentLevelPosition = child.asIntArray();
			} else if(child.name.contentEquals("score")) {
				score = child.asInt();
			}
			child = child.next();
		}
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int[] getCurrentLevelPosition() {
		return currentLevelPosition;
	}

	public void setCurrentLevelPosition(int[] currentLevelPosition) {
		this.currentLevelPosition = currentLevelPosition;
	}

	public String getUuid() {
		return uuid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	public String getDesigner() {
		return designer;
	}

	public void clear() {
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

	public void markLevelsReachable() {
		Level startLevel = null;
		for(Level l : levels) {
			int[] pos = l.getPosition();
			if( ( pos[0] == 0 ) && ( pos[1] == 0 ) ) {
				startLevel = l;
				l.setReachable(true);
			} else {
				l.setReachable(false);
			}
		}

		Array<Level> reachableLevels = new Array<Level>();
		getLevelsReachable(startLevel, reachableLevels);
		for(Level l : reachableLevels) {
			l.setReachable(true);
		}
	}

	@Override
	public String getFilename() {
		return "meta";
	}

	@Override
	public String toString() {
		return super.toString() + ", uuid="+this.uuid+", name="+this.name+
				", designer="+designer+", currentLevelPosition="+this.currentLevelPosition+
				", score="+this.score;
	}
}
