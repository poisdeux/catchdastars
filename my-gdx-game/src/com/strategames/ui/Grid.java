package com.strategames.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.screens.AbstractScreen;
import com.strategames.catchdastars.utils.Textures;

public class Grid extends Image {

	private float GRIDSIZE = Game.convertBoxToWorld(0.30f);

	private float deltaHorizontal = GRIDSIZE;
	private float deltaVertical = GRIDSIZE;

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
		
		this.deltaHorizontal= this.screenWidth / GRIDSIZE;
		this.deltaVertical = this.screenHeight / GRIDSIZE;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		for( float y = 0; y < this.screenHeight; y += this.deltaVertical) {
			for( float x = 0; x < this.screenWidth; x += this.deltaHorizontal) {
				batch.draw(Textures.gridPoint, x - this.halfWidth, y - this.halfHeight, GRIDSIZE*2f, GRIDSIZE*2f);
			}
		}
	}

	/**
	 * Maps vector on grid
	 * @param v Vector in screen coordinate system that should be mapped. Note that v will be changed.
	 */
	public void map(Vector2 v) {
		int hor = (int) (v.x / this.deltaHorizontal);
		int ver = (int) (v.y / this.deltaVertical);
		v.x = hor * this.deltaHorizontal;
		v.y = ver * this.deltaVertical;
	}
}
