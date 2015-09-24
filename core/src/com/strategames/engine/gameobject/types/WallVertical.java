/**
 * 
 * Copyright 2014 Martijn Brekhof
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

package com.strategames.engine.gameobject.types;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.utils.Textures;

public class WallVertical extends Wall {
	private Textures textures = Textures.getInstance();
	
	private static TextureRegion textureRegion;
	
	private int amountOfParts;
	
	public WallVertical() {
		super();
		setPartSize(HEIGHT);
	}
	
	@Override
	public void setLength(float length) {
		if( length < HEIGHT ) {
			super.setLength(HEIGHT); //Make sure length is not smaller than a single block
		} else {
			super.setLength(length);
		}
		
		setHeight(getLength());
		setWidth(WIDTH);
		this.amountOfParts = (int) (length / HEIGHT);
	}
	
	@Override
	protected TextureRegion createImage() {
		if( textureRegion == null ) {
			this.textures.bricksVertical.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			textureRegion = new TextureRegion(this.textures.bricksVertical);
		}
		return textureRegion;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		Color color = getColor();
		batch.setColor(color);
		batch.draw(this.textures.bricksVertical, getX(), getY(), WIDTH, getLength(), 0, 0, -1, this.amountOfParts);
	}

	@Override
	protected GameObject newInstance() {
		Wall wall = new WallVertical();
		wall.setPartSize(HEIGHT);
		return wall;
	}
}
