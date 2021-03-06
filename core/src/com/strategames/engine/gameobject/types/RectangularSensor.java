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

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.StaticBody;
import com.strategames.engine.utils.ConfigurationItem;

public class RectangularSensor extends StaticBody {

	private Vector2 end;
	private Vector2 start;

	/**
	 * Create a rectangular sensor object. By default its size will be that
	 * of the world as set in Game. Use {@link #setStart(Vector2)} and {@link #setEnd(Vector2)} to change size 
	 * @param size is ignored so passing null would be best. 
	 */
	public RectangularSensor(Vector2 size) {
		super(size);
	}

	@Override
	protected TextureRegion createImage() {
		return null;
	}

	public void setEnd(Vector2 end) {
		this.end = end;
	}

	public void setStart(Vector2 start) {
		this.start = start;
	}

	@Override
	protected void setupBody(Body body) {

		if( this.start == null ) {
			this.start = new Vector2(0, 0);
		}
		
		if( this.end == null ) {
			GameEngine game = getGame();
			Vector3 worldSize = game.getWorldSize();
			this.end = new Vector2(worldSize.x, worldSize.y).add(Wall.WIDTH, Wall.HEIGHT);
		}
		
		Vector2 leftBottom = new Vector2(start.x, start.y);
		Vector2 rightBottom = new Vector2(end.x, start.y);
		Vector2 rightTop = new Vector2(end.x, end.y);
		Vector2 leftTop = new Vector2(start.x, end.y);

		ChainShape chain = new ChainShape();
		chain.createLoop(new Vector2[] {leftBottom, rightBottom, rightTop, leftTop});
		Fixture fixture = body.createFixture(chain, 0.0f);
		fixture.setSensor(true);
	}

	@Override
	protected void writeValues(Json json) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readValue(JsonValue jsonData) {
		// TODO Auto-generated method stub

	}

	@Override
	public GameObject copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GameObject newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void increaseSize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void decreaseSize() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void destroyAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse,
			GameObject gameObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadSounds() {
		// TODO Auto-generated method stub

	}
}
