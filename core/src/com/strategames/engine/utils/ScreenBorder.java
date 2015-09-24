/**
 * 
 * Copyright 2013 Martijn Brekhof
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

package com.strategames.engine.utils;

import com.badlogic.gdx.math.Vector3;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.types.Wall;
import com.strategames.engine.gameobject.types.WallHorizontal;
import com.strategames.engine.gameobject.types.WallVertical;

public class ScreenBorder {

	static public void create(Level level, GameEngine game) {
		Vector3 worldSize = game.getWorldSize();
		
		Wall wTop = new WallHorizontal();
		wTop.setGame(game);
		wTop.setLength(worldSize.x + WallHorizontal.WIDTH);
		wTop.setPosition(-wTop.getHalfHeight(), worldSize.y - wTop.getHalfHeight());
		wTop.setBorder(true);
		wTop.setupImage();
		wTop.setupBody();
		level.addGameObject(wTop);
		
		Wall wBottom = (Wall) wTop.copy();
		wBottom.setPosition(wTop.getX(), -wBottom.getHalfHeight());
		wBottom.setBorder(true);
		level.addGameObject(wBottom);
		
		Wall wLeft = new WallVertical();
		wLeft.setGame(game);
		wLeft.setPartSize(WallVertical.HEIGHT);
		wLeft.setLength(worldSize.y - WallVertical.HEIGHT);
		wLeft.setPosition(-wLeft.getHalfWidth(), wLeft.getHalfWidth());
		wLeft.setBorder(true);
		wLeft.setupImage();
		wLeft.setupBody();
		level.addGameObject(wLeft);
		
		Wall wRight = (Wall) wLeft.copy();
		wRight.setPosition(worldSize.x - wRight.getHalfWidth(), wLeft.getY());
		wRight.setBorder(true);
		wRight.setupImage();
		wRight.setupBody();
		level.addGameObject(wRight);
	}
}
