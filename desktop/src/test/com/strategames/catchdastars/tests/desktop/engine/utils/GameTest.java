/**
 * 
 * Copyright 2015 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.catchdastars.tests.desktop.engine.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.tests.desktop.libgdx.junit.GameTestHelper;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.Score;

public class GameTest {
	private Game game;

	@Before
	public void setUp() throws Exception {
		this.game = GameTestHelper.createGame();
	}

	@Test
	public void getCurrentLevelTest() {
		this.game.setCurrentLevelPosition(new int[]{1, 2});
		Level level = this.game.getCurrentLevel();

		assertTrue(level != null);

		int[] pos = level.getPosition();
		assertTrue((pos[0] == 1) && (pos[1] == 2));
	}

	@Test
	public void resetTest() {
		Score score = this.game.getScore();
		score.setCumulatedScore(12);
		assertTrue(score.getCumulatedScore() == 12);

		this.game.reset();

		int[] pos = this.game.getCurrentLevelPosition();
		assertTrue((pos[0] == 0) && (pos[1] == 0));

		score = this.game.getScore();
		assertTrue(score.getCumulatedScore() == 0);
	}

	@Test
	public void getsetaddLevelTest() {
		Level level = GameTestHelper.createLevel(3);
		int[] pos = level.getPosition();
		this.game.setLevel(level);

		Level sLevel = this.game.getLevel(pos[0], pos[1]);

		assertTrue(sLevel.equals(level));

		Level nLevel = GameTestHelper.createLevel(3);
		this.game.addLevel(nLevel);
		sLevel = this.game.getLevel(pos[0], pos[1]);
		assertTrue(nLevel != level); // make sure nLevel is really a new instance
		assertTrue(sLevel == level); // new level should not have been added by addLevel
	}


	@Test
	public void getLevelsTest() {
		int amountOfLevels = GameTestHelper.getAmountOfPositions();
		HashMap<String, Level> levels = this.game.getLevels();
		assertTrue("Amount of levels returned by getLevels() ("+levels.size()+") is not equal to the amount of levels set ("+amountOfLevels+")", levels.size() == amountOfLevels);
		for(int i = 0; i < amountOfLevels; i++) {
			int[] origPosition = GameTestHelper.getPosition(i);
			Level level = this.game.getLevel(origPosition[0], origPosition[1]);
			assertFalse("Level="+level+" not found in game", level == null);
			this.game.deleteLevel(origPosition[0], origPosition[1]);
		}
	}
}
