package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.strategames.engine.game.Game;
import com.strategames.engine.game.GameTestClass;
import com.strategames.engine.screens.GdxTestRunner2;

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
		testIfEqual(this.gameObject, this.gameObject.copy());
	}
	
	/**
	 * TODO Drawing is performed on a separate thread so we cannot check for exceptions
	 * here. Need to fix this somehow. Replace methods/classes that require OpenGL context by 
	 * mock objects? (e.g. Sprite class)
	 */
	@Test
	public void testDraw() {
		Game game = new GameTestClass();
		game.setWorld(new World(new Vector2(0,1), true));
		this.gameObject.setGame(game);
		this.gameObject.setup();
		SpriteBatch batch = new SpriteBatch();
		batch.begin();
		this.gameObject.draw(batch, 1);
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
			assertFalse(this.gameObject.getClass().getName() + ": Bodies are equal", object1.getBody() == object2.getBody());
		}
		assertTrue(this.gameObject.getClass().getName() + ": Drawable not equal: "+ object1.getDrawable() + " != " +object2.getDrawable(), object1.getDrawable() == object2.getDrawable());
		assertTrue(this.gameObject.getClass().getName() + ": X position not equal: "+object1.getX() +"!="+ object2.getX(), object1.getX() == object2.getX());
		assertTrue(this.gameObject.getClass().getName() + ": Y position not equal: "+object1.getY() +"!="+ object2.getY(), object1.getY() == object2.getY());
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
