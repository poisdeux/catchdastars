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
