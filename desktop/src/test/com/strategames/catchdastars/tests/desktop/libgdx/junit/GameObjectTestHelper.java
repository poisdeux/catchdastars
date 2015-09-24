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

import com.badlogic.gdx.utils.Array;
import com.strategames.engine.gameobject.GameObject;

public class GameObjectTestHelper {
	private static Random rand = new Random();
	
	/**
	 * Creates three gameobjects of type GameObjectTestClass
	 * 1: pos = 2,4
	 * 2: pos = 4,2
	 * 3: pos = 0.4, 12
	 * @return
	 */
	public static Array<GameObject> createGameObjects() {
		Array<GameObject> gameObjects = new Array<GameObject>();

		GameObjectTestClass o = new GameObjectTestClass();
		o.setPosition(2, 4);
		gameObjects.add(o);

		o = new GameObjectTestClass();
		o.setPosition(4, 2);
		gameObjects.add(o);

		o = new GameObjectTestClass();
		o.setPosition(0.4f, 12.0f);
		gameObjects.add(o);

		return gameObjects;
	}

	/**
	 * Creates a gameobject at a random position
	 * @return
	 */
	public static GameObject createRandomGameObject() {
		GameObjectTestClass o = new GameObjectTestClass();
		int x = rand.nextInt(50);
		int y = rand.nextInt(50);
		o.setPosition(x, y);
		return o;
	}

	/**
	 * Creates an array with three gameobjects with random positions
	 * @return
	 */
	public static Array<GameObject> createRandomGameObjects() {
		Array<GameObject> gameObjects = new Array<GameObject>();

		gameObjects.add(createRandomGameObject());
		gameObjects.add(createRandomGameObject());
		gameObjects.add(createRandomGameObject());

		return gameObjects;
	}
}
