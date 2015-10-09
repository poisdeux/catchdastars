/**
 * 
 * Copyright 2014 Martijn Brekhof
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

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.tests.desktop.libgdx.junit.GameObjectTestClass;
import com.strategames.catchdastars.tests.desktop.libgdx.junit.LevelTestHelper;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.utils.Level;

public class LevelTest {
	private Level level;

	@Before
	public void setUp() throws Exception {
		this.level = LevelTestHelper.createLevel();
	}

	@Test
	public void equalTest() {
		Level level1 = LevelTestHelper.createRandomLevel();
		Level level2 = LevelTestHelper.createRandomLevel();
		assertFalse("Different levels are considered equal", level1.equals(level2));

		level1 = LevelTestHelper.createLevel();
		level2 = LevelTestHelper.createLevel();
		testEqual(level1, level2);
		
		int[] oldPos = level1.getPosition();
		level1.setPosition(oldPos[0] + 1, oldPos[0]);
		testNotEqual(level1, level2);
		level1.setPosition(oldPos[0], oldPos[1]);
		
		Door door = new Door();
		level1.addGameObject(door);
		testNotEqual(level1, level2);
		level1.removeGameObject(door);
		
		GameObject object = new GameObjectTestClass();
		level1.addGameObject(object);
		testNotEqual(level1, level2);
		level1.removeGameObject(object);
		
		Vector2 v = level1.getViewSize();
		level1.setViewSize(v.add(0, 1));
		testNotEqual(level1, level2);
		level1.setViewSize(v.sub(0, 1));
		
		Vector2 w = level1.getWorldSize();
		level1.setWorldSize(w.add(0, 1));
		testNotEqual(level1, level2);
		level1.setWorldSize(w.sub(0, 1));

		level1.addAccessibleBy(1, 2);
		level2.addAccessibleBy(2,3);
		testNotEqual(level1, level2);
		level1.delAccessibleBy(1, 2);
		level2.delAccessibleBy(2,3);
	}

	@Test
	public void accessibleByTest() {
		Array<int[]> positions = this.level.getAccessibleBy();
		assertTrue(positions.size == 0);

		this.level.addAccessibleBy(0, 0);
		positions = this.level.getAccessibleBy();
		assertTrue(positions.size == 1);
		int[] pos = positions.get(0);
		assertTrue((pos[0] == 0) && (pos[1] == 0));

		this.level.addAccessibleBy(2, 3);
		positions = this.level.getAccessibleBy();
		assertTrue(positions.size == 2);
		pos = positions.get(1);
		assertTrue((pos[0] == 2) && (pos[1] == 3));

		this.level.addAccessibleBy(0, 0); //should not be added again
		positions = this.level.getAccessibleBy();
		assertTrue(positions.size == 2);
		pos = positions.get(0);
		assertTrue((pos[0] == 0) && (pos[1] == 0));
		pos = positions.get(1);
		assertTrue((pos[0] == 2) && (pos[1] == 3));
	}

	private void testEqual(Level level1, Level level2) {
		assertTrue("Level1 is not considered equal to level2", level1.equals(level2));
		assertTrue("Level2 is not considered equal to level1", level2.equals(level1));
	}
	
	private void testNotEqual(Level level1, Level level2) {
		assertFalse("Level1 is not considered equal to level2", level1.equals(level2));
		assertFalse("Level2 is not considered equal to level1", level2.equals(level1));
	}
}
