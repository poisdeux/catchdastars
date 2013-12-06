package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.Wall;

public class ScreenBorder {

	static public void create(Game game) {
		Vector2 worldSize = game.getWorldSize();	
		
		Gdx.app.log("ScreenBorder", "create: worldSize="+worldSize);
		
		Wall wTop = new Wall(game, 0, 0, worldSize.x, Wall.Orientation.HORIZONTAL);
		wTop.increaseSize();
		wTop.increaseSize();
		wTop.setPosition(-wTop.getHalfHeight(), worldSize.y - wTop.getHalfHeight());
		game.addGameObject(wTop);
		
		Wall wBottom = (Wall) wTop.createCopy();
		wBottom.setPosition(wTop.getX(), -wBottom.getHalfHeight());
		game.addGameObject(wBottom);
		
		Wall wLeft = new Wall(game, 0, 0, worldSize.y, Wall.Orientation.VERTICAL);
		wLeft.decreaseSize();
		wLeft.decreaseSize();
		wLeft.setPosition(-wLeft.getHalfWidth(), wLeft.getY() + wLeft.getHalfWidth());
		game.addGameObject(wLeft);
		
		Wall wRight = (Wall) wLeft.createCopy();
		wRight.setPosition(worldSize.x - wRight.getHalfWidth(), wLeft.getY());
		game.addGameObject(wRight);
	}
}
