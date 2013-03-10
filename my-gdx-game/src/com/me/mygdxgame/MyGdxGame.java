package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.gameobjects.Balloon;
import com.me.mygdxgame.gameobjects.GameObject;
import com.me.mygdxgame.gameobjects.Wall;
import com.me.mygdxgame.gameobjects.Wall.Type;

public class MyGdxGame implements ApplicationListener {
	private Balloon balloon;
	private Wall wall;
	private List<Rectangle> walls;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector2 orientation;
	
	private World world;
	static final float WORLD_TO_BOX = 0.01f;
	static final float BOX_TO_WORLD = 100f;
	private Box2DDebugRenderer debugRenderer;
	
	boolean accelerometerAvailable;

	@Override
	public void create() {		
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, 800, 480);
		
		this.batch = new SpriteBatch();
		this.batch.setProjectionMatrix(camera.combined);
		
		Textures.load();
		
		this.orientation = new Vector2();

		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);
		
		this.world = new World(new Vector2(0, 10), true);
		
		this.debugRenderer = new Box2DDebugRenderer();
		
		this.balloon = new Balloon(world, (800 - 48) / 2f, 20f);
		this.wall = new Wall(world,
				(800 - Textures.bricksHorizontal.getRegionWidth())/2f,
				(480 - Textures.bricksHorizontal.getRegionHeight())/2f,
				Type.HORIZONTAL);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0, 0.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		orientation.set(Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerY());
		orientation.nor();
		
		Iterator<Body> bi = world.getBodies();
		
		this.batch.begin();
		while (bi.hasNext()){
		    Body b = bi.next();

		    // Get the bodies user data - in this example, our user 
		    // data is an instance of the Entity class
		    GameObject o = (GameObject) b.getUserData();

		    if (o != null) {
		        o.setPosition(b.getPosition().x, b.getPosition().y);
		        o.setAngle(MathUtils.radiansToDegrees * b.getAngle());
		        o.draw(batch);
		    }
		}

		this.wall.draw(batch);
		this.batch.end();		
		
		this.world.step(1/45f, 6, 2);
		this.debugRenderer.render(world, camera.combined);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
