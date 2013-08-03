package com.strategames.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.utils.Textures;

public class Grid extends Image {

	private Sprite gridPoint;
	private float GRIDSIZE = 32f;
	
	private float deltaHorizontal = 32f;
	private float deltaVertical = 32f;
	
	private int screenWidth;
	private int screenHeight;
	
	private float halfWidth;
	private float halfHeight;
	
	public Grid() {
		this.gridPoint = new Sprite(Textures.gridPoint);
		this.halfWidth = this.gridPoint.getWidth() / 2f;
		this.halfHeight = this.gridPoint.getHeight() / 2f;
	}
	
	public void calculateGridSize(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		float hor = (int) screenWidth / GRIDSIZE;
		float ver = (int) screenHeight / GRIDSIZE;
		
		this.deltaHorizontal= screenWidth / hor;
		this.deltaVertical = screenHeight / ver;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		for( float x = 0; x < this.screenWidth; x += this.deltaHorizontal) {
			for( float y = 0; y < this.screenHeight; y += this.deltaVertical) {
				this.gridPoint.setPosition(x - this.halfWidth, y - this.halfHeight);
				this.gridPoint.draw(batch, parentAlpha);
			}
		}
	}
	
	public Vector2 getGridPoint(float x, float y) {
		Vector2 gridPoint = new Vector2();
		
		int hor = (int) (x / this.deltaHorizontal);
		int ver = (int) (y / this.deltaVertical);
		gridPoint.x = hor * this.deltaHorizontal;
		gridPoint.y = ver * this.deltaVertical;
		
		return gridPoint;
	}
}
