package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.strategames.engine.game.Game;
import com.strategames.engine.game.GameTestClass;
import com.strategames.engine.screens.GdxTestRunner2;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.engine.utils.Textures;

@RunWith(GdxTestRunner2.class)
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
		this.gameObject.setup();
		assertNull(this.gameObject.getBody());
	}

	@Test
	public void testSetupWithoutWorld() {
		this.gameObject.setGame(new GameTestClass());
		assertNull("Body for " + this.gameObject.getClass().getName() + " ", this.gameObject.getBody());
	}

	@Test
	public void testSetupWithWorld() {
		Game game = new GameTestClass();;
		game.setWorld(new World(new Vector2(0,1), true));
		this.gameObject.setGame(game);
		this.gameObject.setup();
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
		this.gameObject.setup();
		GameObject copy = this.gameObject.copy();
		copy.setup();
		testIfEqual(this.gameObject, copy);
	}

	@Test
	public void testCreatingMultipleGameObjects() {
		GameObject o1 = new GameObject1TestClass();
		GameObject o2 = new GameObject2TestClass();
		o1.setPosition(2f, 1f);
		o2.setPosition(1f, 2f);
		o1.setup();
		o2.setup();
		testIfNotEqual(o1, o2);
	}
	
	@Test
	public void testDraw() {
		Game game = new GameTestClass();
		game.setWorld(new World(new Vector2(0,1), true));
		this.gameObject.setGame(game);
		this.gameObject.setup();
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
	
	private void testIfNotEqual(GameObject object1, GameObject object2) {
		if( ( object1.getBody() != null ) || ( object2.getBody() != null ) ) {
			assertTrue(this.gameObject.getClass().getName() + ": Bodies are equal", object1.getBody() != object2.getBody());
		}
		assertTrue(this.gameObject.getClass().getName() + ": TextureRegion equal: "+ object1.getTextureRegion() + " == " +object2.getTextureRegion(), object1.getTextureRegion() != object2.getTextureRegion());
		assertTrue(this.gameObject.getClass().getName() + ": X position equal: "+object1.getX() +" == "+ object2.getX(), object1.getX() != object2.getX());
		assertTrue(this.gameObject.getClass().getName() + ": Y position equal: "+object1.getY() +" == "+ object2.getY(), object1.getY() != object2.getY());
	}

	/**
	 * Implement any assertions specific for this subclass of GameObject
	 * @param b1
	 * @param b2
	 */
	abstract public void assertForEquality(GameObject b1, GameObject b2);

	private class GameObject1TestClass extends GameObject {

		public GameObject1TestClass() {
			super(new Vector2(0,0));
		}

		@Override
		protected TextureRegion createTextureRegion() {
			return Textures.getInstance().starBlue;
		}

		@Override
		protected GameObject newInstance() {
			return new GameObject1TestClass();
		}

		@Override
		protected Body setupBox2D() {
			return null;
		}

		@Override
		protected void writeValues(Json json) {
			
		}

		@Override
		protected void readValue(JsonValue jsonData) {
			
		}

		@Override
		public GameObject copy() {
			return null;
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
	}

	private class GameObject2TestClass extends GameObject {

		public GameObject2TestClass() {
			super(new Vector2(1,0));
		}

		@Override
		protected TextureRegion createTextureRegion() {
			return Textures.getInstance().balloonBlue;
		}

		@Override
		protected GameObject newInstance() {
			return new GameObject2TestClass();
		}

		@Override
		protected Body setupBox2D() {
			return null;
		}

		@Override
		protected void writeValues(Json json) {
			
		}

		@Override
		protected void readValue(JsonValue jsonData) {
			
		}

		@Override
		public GameObject copy() {
			return null;
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
	}
}
