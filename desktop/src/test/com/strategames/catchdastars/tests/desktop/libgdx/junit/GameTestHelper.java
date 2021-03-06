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

import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.storage.GameMetaData;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;

public class GameTestHelper {

	static final private int[][] levelPositions = {
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
		GameMetaData gameMetaData = new GameMetaData();
		gameMetaData.setName("TestGame");
		gameMetaData.setDesigner("TestDesigner");
        gameMetaData.setAdditionalInfo("item1", "23");
        gameMetaData.setAdditionalInfo("43", "testing");
		Game game = new Game(gameMetaData);

		for(int i = 0; i < levelPositions.length; i++) {
			Level level = createLevel(i);
			level.setGameMetaData(gameMetaData);
			game.addLevel(level);
		}

		for(int i = 0; i < doors.length; i++) {
			int[] levelDoors = doors[i];
			int[] levelPos = levelPositions[i];
			for (int j = 0; j < levelDoors.length; j += 2) {
				Level level = game.getLevel(levelDoors[j], levelDoors[j+1]);
				level.addAccessibleBy(levelPos[0], levelPos[1]);
			}
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
		return levelPositions[index];
	}

	public static int getAmountOfPositions() {
		return levelPositions.length;
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

	public static Level createLevel(int pos) {
		Level level = LevelTestHelper.createLevel();
		level.setPosition(levelPositions[pos][0], levelPositions[pos][1]);

		int[] levelDoors = doors[pos];
		for(int j = 0; j < levelDoors.length; j += 2) {
			Door door = new Door();
			door.setAccessTo(levelDoors[j], levelDoors[j + 1]);
			level.addGameObject(door);
		}

		return level;
	}
}
