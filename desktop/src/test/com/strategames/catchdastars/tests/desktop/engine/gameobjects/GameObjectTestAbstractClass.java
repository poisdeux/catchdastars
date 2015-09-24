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

package com.strategames.catchdastars.tests.desktop.engine.gameobjects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.strategames.catchdastars.tests.desktop.libgdx.junit.GameTestClass;
import com.strategames.catchdastars.tests.desktop.libgdx.junit.GdxTestRunner;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.utils.ConfigurationItem;

@RunWith(GdxTestRunner.class)
abstract public class GameObjectTestAbstractClass {
	private GameObject gameObject;

	@Before
	public void setUp() throws Exception {
		this.gameObject = createGameObject();
		this.gameObject.setPosition(2, 4);
	}

	public GameObject getGameObject() {
		return gameObject;
	}

	abstract GameObject createGameObject();

	@Test
	public void testSetupWithoutGame() {
		this.gameObject.setupImage();
		this.gameObject.setupBody();
		assertNotNull(this.gameObject.getDrawable());
		assertNull(this.gameObject.getBody());
	}

	@Test
	public void testSetupWithoutWorld() {
		this.gameObject.setGame(new GameTestClass());
		this.gameObject.setupImage();
		this.gameObject.setupBody();
		assertNotNull(this.gameObject.getDrawable());
		assertNull(this.gameObject.getBody());
	}

	@Test
	public void testSetupWithWorld() {
		GameEngine game = new GameTestClass();;
		game.setWorld(new World(new Vector2(0,1), true));
		this.gameObject.setGame(game);
		this.gameObject.setupImage();
		this.gameObject.setupBody();
		assertNotNull("Body for " + this.gameObject.getClass().getName() + " is null", this.gameObject.getBody());
	}

	@Test
	public void testCopySimpleObject() {
		testIfEqual(this.gameObject, this.gameObject.copy());
	}

	@Test
	public void testCopyFullObject() {
		GameTestClass game = new GameTestClass();
		game.setWorld(new World(new Vector2(0,1), true));
		this.gameObject.setGame(game);
		this.gameObject.setupImage();
		this.gameObject.setupBody();
		GameObject copy = this.gameObject.copy();
		copy.setupImage();
		copy.setupBody();
		testIfEqual(this.gameObject, copy);
	}
	
	@Test
	public void testDraw() {
		GameEngine game = new GameTestClass();
		game.setWorld(new World(new Vector2(0,1), true));
		this.gameObject.setGame(game);
		this.gameObject.setupImage();
		this.gameObject.setupBody();
		SpriteBatch batch = new SpriteBatch();
		batch.begin();
		try {
		this.gameObject.draw(batch, 1);
		} catch (Exception e) {
			throw new AssertionError("Method draw threw an exception: "+e.getMessage());
		}
		batch.end();
	}

	@Test
	public void testSetWidth() {
		float width = 1.3f;
		this.gameObject.setWidth(width);
		assertTrue("Width is "+this.gameObject.getWidth()+", but should be "+width, this.gameObject.getWidth() == width);
		assertTrue("HalfWidth is "+this.gameObject.getHalfWidth()+", but should be "+width/2f, this.gameObject.getHalfWidth() == (width/2f));
	}
	
	@Test
	public void testConfigurationItems() {
		this.gameObject.initializeConfigurationItems();
		ArrayList<ConfigurationItem> configurationItems = gameObject.getConfigurationItems();
		if( configurationItems == null ) {
			return;
		}
		
		for( ConfigurationItem item : configurationItems ) {
			
			ConfigurationItem.Type type = item.getType();
			assertNotNull("Configuration item type is null", type);
			
			if( type == ConfigurationItem.Type.TEXT ) {
				
				String text = item.getValueText();
				assertTrue("Text configuration item is null", text != null);
				
			} else if( type == ConfigurationItem.Type.NUMERIC ) {
				
				float number = item.getValueNumeric();
				
			} else if( type == ConfigurationItem.Type.NUMERIC_RANGE ) {
				
				item.getMinValue();
				item.getMaxValue();
				assertTrue("Numeric range configuration item stepsize not greater than 0: "+item.getStepSize(), item.getStepSize() > 0);
			
			}
		}
	}
	
	private void testIfEqual(GameObject object1, GameObject object2) {
		if( ( object1.getBody() != null ) || ( object2.getBody() != null ) ) {
			assertTrue(this.gameObject.getClass().getName() + ": Bodies are equal", object1.getBody() != object2.getBody());
		}
		assertTrue(this.gameObject.getClass().getName() + ": TextureRegion not equal: "+ object1.getTextureRegion() + " != " +object2.getTextureRegion(), object1.getTextureRegion() == object2.getTextureRegion());
		assertTrue(this.gameObject.getClass().getName() + ": X position not equal: "+object1.getX() +" != "+ object2.getX(), object1.getX() == object2.getX());
		assertTrue(this.gameObject.getClass().getName() + ": Y position not equal: "+object1.getY() +" != "+ object2.getY(), object1.getY() == object2.getY());
		//		assertTrue(this.gameObject.getClass().getName() + ": Color not equal", object1.getColor() == object2.getColor());
		assertForEquality(object1, object2);
	}

	/**
	 * Implement any assertions specific for this subclass of GameObject
	 * @param b1
	 * @param b2
	 */
	abstract public void assertForEquality(GameObject b1, GameObject b2);
}
