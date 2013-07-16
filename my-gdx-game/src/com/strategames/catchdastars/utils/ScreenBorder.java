package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.Wall;

public class ScreenBorder {

	static public void create(Game game) {
		float boxWidth = Game.convertWorldToBox(Gdx.graphics.getWidth());
		float boxHeight = Game.convertWorldToBox(Gdx.graphics.getHeight());
		
		Wall wTop = new Wall(game, 0, 0, boxWidth, Wall.Orientation.HORIZONTAL);
		wTop.increaseSize();
		wTop.increaseSize();
		wTop.setPosition(-wTop.getHalfHeight(), boxHeight - wTop.getHalfHeight());
		game.addGameObject(wTop);
		
		Wall wBottom = (Wall) wTop.createCopy();
		wBottom.setPosition(wTop.getX(), -wBottom.getHalfHeight());
		game.addGameObject(wBottom);
		
		Wall wLeft = new Wall(game, 0, 0, boxHeight, Wall.Orientation.VERTICAL);
		wLeft.decreaseSize();
		wLeft.decreaseSize();
		wLeft.setPosition(-wLeft.getHalfWidth(), wLeft.getY() + wLeft.getHalfWidth());
		game.addGameObject(wLeft);
		
		Wall wRight = (Wall) wLeft.createCopy();
		wRight.setPosition(boxWidth - wRight.getHalfWidth(), wLeft.getY());
		game.addGameObject(wRight);
	}
}
