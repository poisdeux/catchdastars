package com.strategames.engine.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.libgdx.junit.LevelsTestHelper;

public class LevelsTest {
	private Game game;

	@Before
	public void setUp() throws Exception {
		this.game = LevelsTestHelper.createLevels();
	}

	@Test
	public void getLevelsTest() {
		int amountOfLevels = LevelsTestHelper.getAmountOfPositions();
		HashMap<String, Level> levels = this.game.getLevels();
		assertTrue("Amount of levels returned by getLevels() ("+levels.size()+") is not equal to the amount of levels set ("+amountOfLevels+")", levels.size() == amountOfLevels);
		for(int i = 0; i < amountOfLevels; i++) {
			int[] origPosition = LevelsTestHelper.getPosition(i);
			Level level = this.game.getLevel(origPosition[0], origPosition[1]);
			assertFalse("Level="+level+" not found in game", level == null);
			this.game.deleteLevel(origPosition[0], origPosition[1]);	
		}
	}

	@Test
	public void markLevelsReachableTestAllReachable() {
		this.game.markLevelsReachable();
		Collection<Level> levels = this.game.getLevels().values();
		for(Level level : levels) {
			assertTrue("Level not reachable: "+level, level.isReachable());
		}
	}

	@Test
	public void markLevelsReachableNotAllReachable() {
		Collection<Level> levels = this.game.getLevels().values();
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
		this.game.markLevelsReachable();
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
