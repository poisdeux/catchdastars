package com.strategames.ui.helpers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.engine.utils.Textures;

public class Grid extends Image {

	private float GRIDSIZE = 0.30f;

	private float width;
	private float height;

	private float halfWidth = GRIDSIZE/2f;
	private float halfHeight = GRIDSIZE/2f;

	public Grid(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		for( float y = 0; y < this.height; y += GRIDSIZE) {
			for( float x = 0; x < this.width; x += GRIDSIZE) {
				batch.draw(Textures.gridPoint, x - this.halfWidth, y - this.halfHeight, GRIDSIZE, GRIDSIZE);
//				batch.draw(Textures.gridPoint, x, y, GRIDSIZE, GRIDSIZE);
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
