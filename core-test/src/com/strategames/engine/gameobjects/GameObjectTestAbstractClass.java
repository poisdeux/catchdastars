package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.strategames.desktop.ApplicationSetupAbstractClass;
import com.strategames.engine.game.Game;
import com.strategames.engine.game.GameTestClass;

abstract public class GameObjectTestAbstractClass extends ApplicationSetupAbstractClass {
	private GameObject gameObject;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		this.gameObject = createGameObject();
		this.gameObject.setPosition(2, 4);
	}

	abstract GameObject createGameObject();

	@Test
	public void testSetupWithoutGame() {
		this.gameObject.setup();
		assertNull(this.gameObject.getBody());
	}
	
	@Test
	public void testSetupWithoutWorld() {
		this.gameObject.setGame(getGame());
		this.gameObject.setup();
		assertNull("Body for " + this.gameObject.getClass().getName() + " is not null", this.gameObject.getBody());
	}
	
	@Test
	public void testSetupWithWorld() {
		Game game = getGame();
		game.setWorld(new World(new Vector2(0,1), true));
		this.gameObject.setGame(game);
		this.gameObject.setup();
		assertNotNull("Body for " + this.gameObject.getClass().getName() + " is not null", this.gameObject.getBody());
	}
	
	@Test
	public void testCopySimpleObject() {
		Balloon copy = (Balloon) this.gameObject.copy();
		testIfEqual(this.gameObject, copy);
	}
	
	@Test
	public void testCopyFullObject() {
		GameTestClass game = new GameTestClass();
		game.setWorld(new World(new Vector2(0,1), true));
		this.gameObject.setGame(game);
		this.gameObject.setup();
		Balloon copy = (Balloon) this.gameObject.copy();
		testIfEqual(this.gameObject, copy);
	}
	
	private void testIfEqual(GameObject object1, GameObject object2) {
		if( ( object1.getBody() != null ) || ( object2.getBody() != null ) ) {
			assertFalse(this.gameObject.getClass().getName() + ": Bodies are equal", object1.getBody() == object2.getBody());
		}
		assertTrue(this.gameObject.getClass().getName() + ": Drawable not equal", object1.getDrawable() == object2.getDrawable());
		assertTrue(this.gameObject.getClass().getName() + ": X position not equal", object1.getX() == object2.getX());
		assertTrue(this.gameObject.getClass().getName() + ": Y position not equal", object1.getY() == object2.getY());
		assertForEquality(object1, object2);
	}
	
	/**
	 * Implement any assertions specific for this subclass of GameObject
	 * @param b1
	 * @param b2
	 */
	abstract public void assertForEquality(GameObject b1, GameObject b2);
}
