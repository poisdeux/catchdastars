package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.storage.GameMetaData;
import com.strategames.engine.storage.Writer;

import java.util.HashMap;
import java.util.UUID;

public class Game {
	private HashMap<String, Level> levels;
	private int[] currentLevelPosition = {0,0};
    private GameMetaData gameMetaData;

    private Game() {}

	public Game(GameMetaData gameMetaData) {
		this.gameMetaData = gameMetaData;
        this.levels = new HashMap<String, Level>();
	}

//	public int getScore() {
//		return score;
//	}
//
//	public void setScore(int score) {
//		this.score = score;
//	}
//
	public int[] getCurrentLevelPosition() {
		return currentLevelPosition;
	}

	public void setCurrentLevelPosition(int[] currentLevelPosition) {
		this.currentLevelPosition = currentLevelPosition;
	}

    public Level getCurrentLevel() {
        return getLevel(this.currentLevelPosition[0], this.currentLevelPosition[1]);
    }

    public GameMetaData getGameMetaData() {
        return gameMetaData;
    }

    /**
     * Resets game
     */
    public void reset() {
        this.currentLevelPosition[0] = 0;
        this.currentLevelPosition[1] = 0;
    }

    /**
	 * Removes existing levels from game
	 */
	public void clearLevels() {
		this.levels = new HashMap<String, Level>();
	}

	public HashMap<String, Level> getLevels() {
		return levels;
	}

	/**
	 * Overrides any existing level. This means that any level
	 * already at the same position as level will be overwritten.
	 * @param level
	 */
	public void setLevel(Level level) {
		this.levels.put(createKey(level), level);
	}

	public Level deleteLevel(int column, int row) {
		return this.levels.remove(createKey(column, row));
	}

	/**
	 * Adds level only if a level at the same position does not already exist.
	 * @param level
	 */
	public void addLevel(Level level) {
		String key = createKey(level);
		Level curLevel = this.levels.get(key); 
		if( curLevel == null ) {
			this.levels.put(key, level);
		}
	}

	public Level getLevel(int column, int row) {
		return this.levels.get(createKey(column, row));
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
		for(Level l : this.levels.values() ) {
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

//	@Override
//	public String toString() {
//		return super.toString() + ", uuid="+this.uuid+", name="+this.name+
//				", designer="+designer+", currentLevelPosition="+this.currentLevelPosition+
//				", score="+this.score;
//	}

	private String createKey(int column, int row) {
		return column+""+row;
	}

	private String createKey(Level level) {
		int[] pos = level.getPosition();
		return pos[0]+""+pos[1];
	}
}
