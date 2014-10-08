package com.strategames.engine.utils;

import java.util.Iterator;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;
import com.strategames.engine.gameobject.types.Door;

public class LevelsTest {
	private Levels levels;
	
	/** 
	 *              _____ _____ _____ 
	 *             | 2,3 | 3,3 | 4,3 |
	 *             |  >  |  >  |  \/ |
	 *  _____ _____|_____|_____|_____|_____ _____
	 * | 0,2 | 1,2 | 2,2 |     | 4,2 | 5,2 | 6,2 | 
	 * |  >  |  >  |  ^  |     |  >  |  >  | \/  |
	 * |_____|_____|_____|     |_____|_____|_____|
	 * | 0,1 |                 | 4,1 |     | 6,1 |
	 * |  ^  |                 |  ^  |     | \/  |
	 * |_____|      _____ _____|_____|_____|_____|_____
	 * | 0,0 |     | 2,0 | 3,0 | 4,0 | 5,0 | 6,0 | 7,0 |
	 * |  ^  |     |  >  |  <  | <^  |  <  | < > |     |
	 * |_____|     |_____|_____|_____|_____|_____|_____|
	 *  Start
	 */
	
	private int[][] positions = {
			{0,0},{0,1},{0,2},{1,2},{2,2},
			{2,3},{3,3},{4,3},{4,2},{5,2},
			{6,2},{6,1},{6,0},{7,0},{5,0},
			{4,0},{4,1},{3,0},{2,0}
	};
	
	/**
	 * 
	 */
	private int[][] doors = {
			{0,1},{0,2},{1,2},{2,2},{2,3},
			{3,3},{4,3},{4,2},{5,2},{6,2},
			{6,1},{6,0},{5,0,7,0},{},{4,0},
			{3,0,4,1},{4,2},{2,0},{3,0}
	};
	
	@Before
	public void setUp() throws Exception {
		this.levels = new Levels();
		for(int i = 0; i < positions.length; i++) {
			Level level = new Level();
			level.setPosition(positions[i][0], positions[i][1]);
			int[] levelDoors = doors[i];
			for(int j = 0; j < levelDoors.length; j += 2) {
				Door door = new Door();
				door.setNextLevelPosition(levelDoors[j], levelDoors[j+1]);
				level.addDoor(door);
			}
			this.levels.addLevel(level);
		}
	}

	@Test
	public void getLevelsTest() {
		Array<Level> levels = this.levels.getLevels();
		Iterator<Level> itr = levels.iterator();
		while(itr.hasNext()) {
			boolean levelNotFound = true;
			Level level = itr.next();
			for(int i = 0; i < positions.length; i++) {
				int[] pos = level.getPosition();
				if((pos[0] == positions[i][0]) && (pos[1] == positions[i][0])) {
					itr.remove();
					levelNotFound = false;
				}
			}
			assertTrue("Level="+level+" not found in levels", levelNotFound);
		}
		assertTrue("getLevels() does not return all levels", levels.size == 0);
	}

}
