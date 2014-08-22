package com.strategames.engine.utils;

import com.badlogic.gdx.math.Vector3;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobjects.Wall;
import com.strategames.engine.gameobjects.WallHorizontal;
import com.strategames.engine.gameobjects.WallVertical;

public class ScreenBorder {

	static public void create(Game game) {
		Vector3 worldSize = game.getWorldSize();	
		
		Wall wTop = new WallHorizontal();
		wTop.setGame(game);
		wTop.setLength(worldSize.x + WallHorizontal.WIDTH);
		wTop.setPosition(-wTop.getHalfHeight(), worldSize.y - wTop.getHalfHeight());
		wTop.setBorder(true);
		wTop.setupImage();
		wTop.setupBody();
		game.getLevel().addGameObject(wTop);
		
		Wall wBottom = (Wall) wTop.copy();
		wBottom.setPosition(wTop.getX(), -wBottom.getHalfHeight());
		wBottom.setBorder(true);
		game.getLevel().addGameObject(wBottom);
		
		Wall wLeft = new WallVertical();
		wLeft.setGame(game);
		wLeft.setPartSize(WallVertical.HEIGHT);
		wLeft.setLength(worldSize.y - WallVertical.HEIGHT);
		wLeft.setPosition(-wLeft.getHalfWidth(), wLeft.getHalfWidth());
		wLeft.setBorder(true);
		wLeft.setupImage();
		wLeft.setupBody();
		game.getLevel().addGameObject(wLeft);
		
		Wall wRight = (Wall) wLeft.copy();
		wRight.setPosition(worldSize.x - wRight.getHalfWidth(), wLeft.getY());
		wRight.setBorder(true);
		wRight.setupImage();
		wRight.setupBody();
		game.getLevel().addGameObject(wRight);
	}
}
