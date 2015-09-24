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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.StaticBody;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.engine.utils.Textures;

public class Door extends StaticBody {
	public final static float WIDTH = 0.35f;
	public final static float HEIGHT = 0.35f;
	
	private Wall wall;
	private boolean open;
	private int[] entryLevel = new int[2]; // level this door provides access too
	private int[] exitLevel = new int[2]; // level this door can be accessed from
	
	public Door() {
		super(new Vector2(WIDTH, HEIGHT));
	}
	
	@Override
	protected TextureRegion createImage() {
		return Textures.getInstance().passageToNextLevel;
	}
	
	@Override
	protected void setupBody(Body body) {
		Vector2 leftBottom = new Vector2(0, 0);
		Vector2 rightBottom = new Vector2(WIDTH, 0);
		Vector2 rightTop = new Vector2(WIDTH, HEIGHT);
		Vector2 leftTop = new Vector2(0, HEIGHT);
		
		ChainShape chain = new ChainShape();
		chain.createLoop(new Vector2[] {leftBottom, rightBottom, rightTop, leftTop});

		Fixture fixture = body.createFixture(chain, 0.0f);
		fixture.setSensor(true);
	}

	@Override
	protected void writeValues(Json json) {
		json.writeArrayStart("nextLevelPosition");
		json.writeValue(entryLevel[0]);
		json.writeValue(entryLevel[1]);
		json.writeArrayEnd();
	}

	@Override
	protected void readValue(JsonValue jsonData) {
		String name = jsonData.name();
		if( name.contentEquals("nextLevelPosition")) {
			this.entryLevel = jsonData.asIntArray();
		}
	}

	@Override
	public GameObject copy() {
		Door door = new Door();
		door.setPosition(getX(), getY());
		int[] pos = getAccessToPosition();
		door.setAccessTo(pos[0], pos[1]);
		return door;
	}

	@Override
	protected GameObject newInstance() {
		return new Door();
	}

	@Override
	protected ArrayList<ConfigurationItem> createConfigurationItems() {
		return null;
	}

	@Override
	public void increaseSize() {
		
	}

	@Override
	public void decreaseSize() {
		
	}

	@Override
	protected void destroyAction() {
		
	}

	@Override
	public void handleCollision(Contact contact, ContactImpulse impulse,
			GameObject gameObject) {
		
	}

	@Override
	public void loadSounds() {
		
	}
	
	public Wall getWall() {
		return wall;
	}
	
	public void setWall(Wall wall) {
		this.wall = wall;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	/**
	 * Sets the level position this door provides access too
	 * @param x
	 * @param y
	 */
	public void setAccessTo(int x, int y) {
		this.entryLevel[0] = x;
		this.entryLevel[1] = y;
	}
	
	/**
	 * Returns the level position this door provides access too
	 * @return
	 */
	public int[] getAccessToPosition() {
		return entryLevel;
	}
	
	/**
	 * Sets the level position from which this door is accessible
	 * @param exitLevel
	 */
	public void setExitLevel(int[] exitLevel) {
		this.exitLevel = exitLevel;
	}
	
	/**
	 * Returns the level position this door is accessible from
	 * @return
	 */
	public int[] getAccessFromPosition() {
		return exitLevel;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", nextLevelPosition="+entryLevel[0]+","+entryLevel[1];
	}
	
	@Override
	public boolean equals(Object obj) {
		Door door;
		if( obj instanceof Door ) {
			door = (Door) obj;
		} else {
			return false;
		}
		int[] pos = door.getAccessToPosition();
		return pos[0] == entryLevel[0] && pos[1] == entryLevel[1] && 
				super.equals(obj);
	}
}
