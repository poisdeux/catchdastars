package com.strategames.catchdastars.tests.desktop.libgdx.junit;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.strategames.engine.utils.Level;

public class LevelTestHelper {

	private static Random rand = new Random();
	
	/**
	 * Creates a basic level
	 * <br/>
	 * position = 1,4
	 * name = MyLevel
	 * reachable = false
	 * viewsize = 3,5
	 * worldsize = 5,7
	 * gameobjects = {@link GameObjectTestHelper#createGameObjects()}
	 * @return
	 */
	public static Level createLevel() {
		Level level = new Level();
		level.setPosition(0,0);
		level.setReachable(false);
		level.setViewSize(new Vector2(3, 5));
		level.setWorldSize(new Vector2(5, 7));
		level.setGameObjects(GameObjectTestHelper.createGameObjects());
		return level;
	}
	
	/**
	 * Creates a random level
	 * @return
	 */
	public static Level createRandomLevel() {
		Level level = new Level();
		
		int x = rand.nextInt(50);
		int y = rand.nextInt(50);
		level.setPosition(x, y);
		
		level.setReachable(rand.nextBoolean());
		
		x = rand.nextInt(50);
		y = rand.nextInt(50);
		level.setViewSize(new Vector2(x, y));
		
		x = rand.nextInt(50);
		y = rand.nextInt(50);
		level.setWorldSize(new Vector2(x, y));

		x = rand.nextInt(50);
		y = rand.nextInt(50);
		level.addAccessibleBy(x, y);

		level.setGameObjects(GameObjectTestHelper.createRandomGameObjects());
		return level;
	}
	
	
}
