package com.strategames.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.utils.Textures;

public class Grid extends Image {

	private Sprite gridPoint;
	private float GRIDSIZE = 0.30f;
	
	private float deltaHorizontal = 32f;
	private float deltaVertical = 32f;
	
	private float worldWidth;
	private float worldHeight;
	
	private float halfWidth;
	private float halfHeight;
	
	public Grid() {
		this.gridPoint = new Sprite(Textures.gridPoint);
		this.halfWidth = this.gridPoint.getWidth() / 2f;
		this.halfHeight = this.gridPoint.getHeight() / 2f;
	}
	
	public void calculateGridSize(float worldWidth, float worldHeight) {
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		
		float hor = (int) worldWidth / GRIDSIZE;
		float ver = (int) worldHeight / GRIDSIZE;
		
		this.deltaHorizontal= worldWidth / hor;
		this.deltaVertical = worldHeight / ver;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		for( float x = 0; x < this.worldWidth; x += this.deltaHorizontal) {
			for( float y = 0; y < this.worldHeight; y += this.deltaVertical) {
				this.gridPoint.setPosition(x - this.halfWidth, y - this.halfHeight);
				this.gridPoint.draw(batch, parentAlpha);
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
