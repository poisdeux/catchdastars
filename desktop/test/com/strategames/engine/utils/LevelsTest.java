package com.strategames.engine.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.storage.GameMetaData;
import com.strategames.libgdx.junit.GameTestHelper;

public class LevelsTest {
	private GameMetaData gameMetaData;

	@Before
	public void setUp() throws Exception {
		this.gameMetaData = GameTestHelper.createGame();
	}

	@Test
	public void getLevelsTest() {
		int amountOfLevels = GameTestHelper.getAmountOfPositions();
		HashMap<String, Level> levels = this.gameMetaData.getLevels();
		assertTrue("Amount of levels returned by getLevels() ("+levels.size()+") is not equal to the amount of levels set ("+amountOfLevels+")", levels.size() == amountOfLevels);
		for(int i = 0; i < amountOfLevels; i++) {
			int[] origPosition = GameTestHelper.getPosition(i);
			Level level = this.gameMetaData.getLevel(origPosition[0], origPosition[1]);
			assertFalse("Level="+level+" not found in game", level == null);
			this.gameMetaData.deleteLevel(origPosition[0], origPosition[1]);
		}
	}

	@Test
	public void markLevelsReachableTestAllReachable() {
		this.gameMetaData.markLevelsReachable();
		Collection<Level> levels = this.gameMetaData.getLevels().values();
		for(Level level : levels) {
			assertTrue("Level not reachable: "+level, level.isReachable());
		}
	}

	@Test
	public void markLevelsReachableNotAllReachable() {
		Collection<Level> levels = this.gameMetaData.getLevels().values();
		for(Level level : levels) {
			int[] pos = level.getPosition();
			//Remove door from 5,2 to 6,2
			if((pos[0] == 5) && (pos[1] == 2)) {
				Array<Door> doors = level.getDoors();
				for(Door door : doors) {
					level.removeGameObject(door);
				}
			}
		}
		int[][] posNotReachable = {{6,2},{6,1},{6,0},{7,0}};
		this.gameMetaData.markLevelsReachable();
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
