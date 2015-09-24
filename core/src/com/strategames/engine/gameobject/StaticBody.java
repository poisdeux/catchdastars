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

package com.strategames.engine.gameobject;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

abstract public class StaticBody extends GameObject {

	public StaticBody(Vector2 size) {
		super(size);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TextureRegion createImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Body createBody(World world) {
		BodyDef bd = new BodyDef();
		bd.position.set(getX(), getY());
		bd.type = BodyType.StaticBody;
		Body body = world.createBody(bd);
		setupBody(body);
		return body;
	}
	
	/**
	 * Called after {@link #createTextureRegionDrawable()} to create the Box2D body of the game object.
	 */
	abstract protected void setupBody(Body body);
}
