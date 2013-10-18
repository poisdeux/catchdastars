package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.Wall;
import com.strategames.catchdastars.screens.AbstractScreen;

public class ScreenBorder {

	static public void create(Game game) {
		Screen screen = game.getScreen();
		float width;
		float height;
		if( screen instanceof AbstractScreen ) {
			AbstractScreen abstractScreen = (AbstractScreen) screen;
			width = abstractScreen.getGameWidth();
			height = abstractScreen.getGameHeight();
		} else {
			width = Gdx.graphics.getWidth();
			height = Gdx.graphics.getHeight();
		}
//		float boxWidth = Game.convertWorldToBox(width);
//		float boxHeight = Game.convertWorldToBox(height);
		float boxWidth = width;
		float boxHeight = height;
		
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
