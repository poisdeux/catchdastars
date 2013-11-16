package com.strategames.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.screens.AbstractScreen;
import com.strategames.catchdastars.utils.Textures;

public class Grid extends Image {

	private float GRIDSIZE = Game.convertBoxToWorld(0.30f);

	private float screenWidth;
	private float screenHeight;

	private float halfWidth = GRIDSIZE/2f;
	private float halfHeight = GRIDSIZE/2f;

	public Grid(Game game) {
//		setDrawable(new TextureRegionDrawable(Textures.gridPoint));
//		setScaling(Scaling.stretch);
//		setWidth(GRIDSIZE);
//		setHeight(GRIDSIZE);
		
		AbstractScreen screen = (AbstractScreen) game.getScreen();
		this.screenWidth = screen.getScreenWidth();
		this.screenHeight = screen.getScreenHeight();
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		for( float y = 0; y < this.screenHeight; y += GRIDSIZE) {
			for( float x = 0; x < this.screenWidth; x += GRIDSIZE) {
				batch.draw(Textures.gridPoint, x - this.halfWidth, y - this.halfHeight, GRIDSIZE, GRIDSIZE);
			}
		}
	}

	/**
	 * Maps vector on grid
	 * @param v Vector in screen coordinate system that should be mapped. Note that v will be changed.
	 */
	public void map(Vector2 v) {
		int hor = (int) (v.x / GRIDSIZE);
		int ver = (int) (v.y / GRIDSIZE);
		v.x = hor * GRIDSIZE;
		v.y = ver * GRIDSIZE;
	}
}
