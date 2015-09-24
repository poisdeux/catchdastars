/**
 * 
 * Copyright 2013 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
				batch.draw(Textures.getInstance().gridPoint, x - this.halfWidth, y - this.halfHeight, GRIDSIZE, GRIDSIZE);
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
