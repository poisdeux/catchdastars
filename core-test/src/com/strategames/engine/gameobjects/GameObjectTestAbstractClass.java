package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.strategames.engine.game.GameTestClass;
import com.strategames.engine.utils.Textures;

abstract public class GameObjectTestAbstractClass {
	private GameObject gameObject;
	private static LwjglApplication application;
	private GameTestClass game;
	
	@Before
	public void setUp() throws Exception {
		this.game = new GameTestClass();
		if(this.application == null) {
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.title = "core-test";
			config.width = 504;
			config.height = 800;
			this.application = new LwjglApplication(this.game, config);
		}
		
		//TODO Move this to ScreenTestClass as GL context does not seem to be available here just yet
		AssetManager assetManager = this.game.getManager();
		if( assetManager == null ) {
			throw new Exception("Could not get assetManager from game");
		}
		
		Textures textures = Textures.getInstance();
		if( textures == null ) {
			throw new Exception("Could not get textures instance");
		}
		textures.addToAssetManager(assetManager);
		while( ! assetManager.update() ) {};
		textures.setup(assetManager);
		
		this.gameObject = createGameObject();
		this.gameObject.setPosition(2, 4);
	}

	abstract GameObject createGameObject();
	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetupWithoutGame() {
		this.gameObject.setup();
		assertNull(this.gameObject.getBody());
	}
	
	@Test
	public void testSetupWithoutWorld() {
		this.gameObject.setGame(this.game);
		this.gameObject.setup();
		assertNull("Body for " + this.gameObject.getClass().getName() + " is not null", this.gameObject.getBody());
	}
	
	@Test
	public void testSetupWithWorld() {
		game.setWorld(new World(new Vector2(0,1), true));
		this.gameObject.setGame(this.game);
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
		this.gameObject.setGame(this.game);
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
	}
	
	/**
	 * Implement any assertions specific for this subclass of GameObject
	 * @param b1
	 * @param b2
	 */
	abstract public void assertForEquality(GameObject b1, GameObject b2);
}
