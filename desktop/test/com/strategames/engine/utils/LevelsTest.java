package com.strategames.engine.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.libgdx.junit.LevelsTestHelper;

public class LevelsTest {
	private Levels levels;

	@Before
	public void setUp() throws Exception {
		this.levels = LevelsTestHelper.createLevels();
	}

	@Test
	public void getLevelsTest() {
		Array<Level> levels = this.levels.getLevels();
		int amountOfLevels = levels.size;
		Iterator<Level> itr = levels.iterator();
		while(itr.hasNext()) {
			boolean levelNotFound = true;
			Level level = itr.next();
			for(int i = 0; i < amountOfLevels; i++) {
				int[] pos = level.getPosition();
				int[] origPosition = LevelsTestHelper.getPositions(i);
				if((pos[0] == origPosition[0]) && (pos[1] == origPosition[1])) {
					itr.remove();
					levelNotFound = false;
					break;
				}
			}
			assertFalse("Level="+level+" not found in levels", levelNotFound);
		}
		assertTrue("getLevels() does not return all levels", levels.size == 0);
	}

	@Test
	public void markLevelsReachableTestAllReachable() {
		this.levels.markLevelsReachable();
		Array<Level> levels = this.levels.getLevels();
		for(Level level : levels) {
			assertTrue("Level not reachable: "+level, level.isReachable());
		}
	}

	@Test
	public void markLevelsReachableNotAllReachable() {
		Array<Level> levels = this.levels.getLevels();
		for(Level level : levels) {
			int[] pos = level.getPosition();
			//Remove door from 5,2 to 6,2
			if((pos[0] == 5) && (pos[1] == 2)) {
				Array<Door> doors = level.getDoors();
				for(Door door : doors) {
					level.removeDoor(door);
				}
			}
		}
		int[][] posNotReachable = {{6,2},{6,1},{6,0},{7,0}};
		this.levels.markLevelsReachable();
		for(Level level : levels) {
			int[] pos = level.getPosition();
			
			boolean levelShouldNotBeReachable = false;
			for(int i = 0; i < posNotReachable.length; i++) {
				if((pos[0] == posNotReachable[i][0]) && (pos[1] == posNotReachable[i][1])) {
					assertFalse("Level reachable: "+level, level.isReachable());
					levelShouldNotBeReachable = true;
				} 
			}
			if(! levelShouldNotBeReachable) {
				assertTrue("Level not reachable: "+level, level.isReachable());
			}
		}
	}
}
