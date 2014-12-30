package com.strategames.engine.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.libgdx.junit.GameObjectTestClass;
import com.strategames.libgdx.junit.LevelTestHelper;

public class LevelTest {
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
