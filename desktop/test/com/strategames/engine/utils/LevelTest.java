package com.strategames.engine.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.strategames.libgdx.junit.LevelsTestHelper;

public class LevelTest {
	Level level;
	
	@Before
	public void setUp() throws Exception {
		this.level = createLevel();
	}
	
	@Test
	public void equalTest() {
		fail("Not yet implemented");
	}

	
	private Level createLevel() {
		Level level = new Level();
		level.setPosition(2, 4);
		level.setName("MyLevel");
		level.setReachable(false);
		level.setViewSize(new Vector2(3, 5));
		level.setWorldSize(new Vector2(1, 2));
		return level;
	}
}
