package com.strategames.libgdx.junit;

import com.badlogic.gdx.math.Vector2;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;

public class GameTestHelper {
	
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
	
	/**
	 * Creates a game with the following levels
	 * <pre>
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
	 *  </pre>
	 *  The >, <, ^, and \/ mean the level contains a door leading to a next level
	 *  <br/>
	 *  >   level to the left: x + 1
	 *  <   level to the right: x - 1
	 *  ^   level above: y + 1
	 *  \/  level below: y - 1
	 *  <br/>
	 * @return
	 */
	public static Game createGame() {
		Game game = new Game();
		Vector2 viewSize = new Vector2(3.1f, 18.1f);
		Vector2 worldSize = new Vector2(9.3f, 36.2f);
		for(int i = 0; i < positions.length; i++) {
			Level level = new Level();
			level.setPosition(positions[i][0], positions[i][1]);
			level.setViewSize(viewSize);
			level.setWorldSize(worldSize);
			
			int[] levelDoors = doors[i];
			for(int j = 0; j < levelDoors.length; j += 2) {
				Door door = new Door();
				door.setEntryLevel(levelDoors[j], levelDoors[j+1]);
				level.addGameObject(door);
			}
			
			for(GameObject object : GameObjectTestHelper.createRandomGameObjects()) {
				level.addGameObject(object);
			}
			
			game.addLevel(level);
		}
		return game;
	}
	
	
	
	/**
	 * 
	 * @param index
	 * @return position in grid coordinates (int[0]=x, int[1]=y)
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public static int[] getPosition(int index) throws ArrayIndexOutOfBoundsException {
		return positions[index];
	}
	
	public static int getAmountOfPositions() {
		return positions.length;
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
}
