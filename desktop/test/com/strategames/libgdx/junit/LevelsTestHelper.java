package com.strategames.libgdx.junit;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.Levels;

public class LevelsTestHelper {
	
	/** 
	 *              _____ _____ _____ 
	 *             | 2,3 | 3,3 | 4,3 |
	 *             |  >  |  >  |  \/ |
	 *  _____ _____|_____|_____|_____|_____ _____
	 * | 0,2 | 1,2 | 2,2 |     | 4,2 | 5,2 | 6,2 | 
	 * |  >  |  >  |  ^  |     |  >  |  >  | \/  |
	 * |_____|_____|_____|     |_\/__|_____|_____|
	 * | 0,1 |                 | 4,1 |     | 6,1 |
	 * |  ^  |                 |  ^  |     | \/  |
	 * |_____|      _____ _____|_\/__|_____|_____|_____
	 * | 0,0 |     | 2,0 | 3,0 | 4,0 | 5,0 | 6,0 | 7,0 |
	 * |  ^  |     |  >  |  <  | <^> |  <  | < > |     |
	 * |_____|     |_____|_____|_____|_____|_____|_____|
	 *  Start
	 */
	
	static final private int[][] positions = {
			{0,0},{0,1},{0,2},{1,2},{2,2},
			{2,3},{3,3},{4,3},{4,2},{5,2},
			{6,2},{6,1},{6,0},{7,0},{5,0},
			{4,0},{4,1},{3,0},{2,0}
	};
	
	/**
	 * 
	 */
	static final private int[][] doors = {
			{0,1},{0,2},{1,2},{2,2},{2,3},
			{3,3},{4,3},{4,2},{5,2,4,1},{6,2},
			{6,1},{6,0},{5,0,7,0},{},{4,0},
			{3,0,4,1,5,0},{4,2,4,0},{2,0},{3,0}
	};
	
	public static Levels createLevels() {
		Levels levels = new Levels();
		Vector2 viewSize = new Vector2(3.1f, 18.1f);
		Vector2 worldSize = new Vector2(9.3f, 36.2f);
		for(int i = 0; i < positions.length; i++) {
			Level level = new Level();
			level.setPosition(positions[i][0], positions[i][1]);
			level.setName(level.getPositionAsString());
			level.setViewSize(viewSize);
			level.setWorldSize(worldSize);
			
			int[] levelDoors = doors[i];
			for(int j = 0; j < levelDoors.length; j += 2) {
				Door door = new Door();
				door.setNextLevelPosition(levelDoors[j], levelDoors[j+1]);
				level.addDoor(door);
			}
			
			for(GameObject object : createGameObjects()) {
				level.addGameObject(object);
			}
			
			levels.addLevel(level);
		}
		return levels;
	}
	
	
	/**
	 * 
	 * @param index
	 * @return position in grid coordinates (int[0]=x, int[1]=y)
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public static int[] getPositions(int index) throws ArrayIndexOutOfBoundsException {
		return positions[index];
	}
	
	/**
	 * 
	 * @param positionIndex index of level you want to get the doors from
	 * @return int array which holds tuples containing the position of levels the doors provide access to.
	 * <br/>Example: positionIndex=12 returns int[4]={5,0,7,0} which means the level at index 12 has two doors. 
	 * First leads to level at 5,0 and the second to level at 7,0 
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public static int[] getDoors(int positionIndex) throws ArrayIndexOutOfBoundsException {
		return doors[positionIndex];
	}
	
	private static Array<GameObject> createGameObjects() {
		Array<GameObject> gameObjects = new Array<GameObject>();
		
		GameObjectTestClass o = new GameObjectTestClass();
		o.setPosition(3, 1);
		gameObjects.add(o);
		
		o = new GameObjectTestClass();
		o.setPosition(1, 1);
		gameObjects.add(o);
		
		o = new GameObjectTestClass();
		o.setPosition(6, 3);
		gameObjects.add(o);
		
		return gameObjects;
	}
	
}
