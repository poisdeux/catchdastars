package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
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
		wTop.setLength(worldSize.x);
		wTop.increaseSize();
		wTop.increaseSize();
		wTop.setPosition(-wTop.getHalfHeight(), worldSize.y - wTop.getHalfHeight());
		wTop.setBorder(true);
		wTop.setup();
		game.getLevel().addGameObject(wTop);
		
		Wall wBottom = (Wall) wTop.copy();
		wBottom.setPosition(wTop.getX(), -wBottom.getHalfHeight());
		wBottom.setBorder(true);
		game.getLevel().addGameObject(wBottom);
		
		Wall wLeft = new WallVertical();
		wLeft.setGame(game);
		wLeft.setLength(worldSize.y);
		wLeft.decreaseSize();
		wLeft.decreaseSize();
		wLeft.setPosition(-wLeft.getHalfWidth(), wLeft.getY() + wLeft.getHalfWidth());
		wLeft.setBorder(true);
		wLeft.setup();
		Gdx.app.log("ScreenBorder","create: wLeft="+wLeft);
		game.getLevel().addGameObject(wLeft);
		
		Wall wRight = (Wall) wLeft.copy();
		wRight.setPosition(worldSize.x - wRight.getHalfWidth(), wLeft.getY());
		wRight.setBorder(true);
		wRight.setup();
		Gdx.app.log("ScreenBorder","create: wRight="+wRight);
		game.getLevel().addGameObject(wRight);
	}
}
