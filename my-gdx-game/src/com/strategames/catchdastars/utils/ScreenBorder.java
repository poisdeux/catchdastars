package com.strategames.catchdastars.utils;

import com.badlogic.gdx.Gdx;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.Wall;

public class ScreenBorder {

	static public void create(Game game) {
		float boxWidth = Game.convertWorldToBox(Gdx.graphics.getWidth());
		float boxHeight = Game.convertWorldToBox(Gdx.graphics.getHeight());
		Wall wBottom = new Wall(game, 0, 0, boxWidth, Wall.Orientation.HORIZONTAL);
		game.addGameObject(wBottom);
		Wall wTop = new Wall(game, 0, boxHeight, boxWidth, Wall.Orientation.HORIZONTAL);
		game.addGameObject(wTop);
		Wall wLeft = new Wall(game, 0, 0, boxHeight, Wall.Orientation.VERTICAL);
		wLeft.decreaseSize();
		wLeft.decreaseSize();
		wLeft.setPosition(wLeft.getX(), wLeft.getY() - wLeft.getStepSize());
		game.addGameObject(wLeft);
		Wall wRight = new Wall(game, boxWidth, 0, boxHeight, Wall.Orientation.VERTICAL);
		wRight.decreaseSize();
		wRight.decreaseSize();
		wRight.setPosition(wLeft.getX(), wLeft.getY() - wLeft.getStepSize());
		game.addGameObject(wRight);
	}
}
