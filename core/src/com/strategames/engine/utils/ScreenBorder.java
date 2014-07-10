package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.strategames.catchdastars.Game;
import com.strategames.gameobjects.Wall;

public class ScreenBorder {

	static public void create(Game game) {
		Vector3 worldSize = game.getWorldSize();	
		
		Wall wTop = new Wall(game, 0, 0, worldSize.x, Wall.Orientation.HORIZONTAL);
		wTop.increaseSize();
		wTop.increaseSize();
		wTop.setPosition(-wTop.getHalfHeight(), worldSize.y - wTop.getHalfHeight());
		wTop.setBorder(true);
		game.getLevel().addGameObject(wTop);
		
		Wall wBottom = (Wall) wTop.copy();
		wBottom.setPosition(wTop.getX(), -wBottom.getHalfHeight());
		wBottom.setBorder(true);
		game.getLevel().addGameObject(wBottom);
		
		Wall wLeft = new Wall(game, 0, 0, worldSize.y, Wall.Orientation.VERTICAL);
		wLeft.decreaseSize();
		wLeft.decreaseSize();
		wLeft.setPosition(-wLeft.getHalfWidth(), wLeft.getY() + wLeft.getHalfWidth());
		wLeft.setBorder(true);
		Gdx.app.log("ScreenBorder","create: wLeft="+wLeft);
		game.getLevel().addGameObject(wLeft);
		
		Wall wRight = (Wall) wLeft.copy();
		wRight.setPosition(worldSize.x - wRight.getHalfWidth(), wLeft.getY());
		wRight.setBorder(true);
		Gdx.app.log("ScreenBorder","create: wRight="+wRight);
		game.getLevel().addGameObject(wRight);
	}
}
