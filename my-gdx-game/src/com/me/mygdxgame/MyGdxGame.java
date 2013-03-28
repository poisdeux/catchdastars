package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.gameobjects.Balloon;
import com.me.mygdxgame.gameobjects.GameObject;
import com.me.mygdxgame.gameobjects.Wall;
import com.me.mygdxgame.gameobjects.Wall.Type;

public class MyGdxGame implements ApplicationListener {
	private float viewportWidth = 800;
	private float viewportHeight = 480;
	private float gravityFactor = 10;

	private ArrayList<Wall> walls;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Vector2 gravity;

	private World world;
	static final float WORLD_TO_BOX = 0.01f;
	static final float BOX_TO_WORLD = 100f;
	private Box2DDebugRenderer debugRenderer;

	boolean accelerometerAvailable;

	@Override
	public void create() {		
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, viewportWidth, viewportHeight);

		this.batch = new SpriteBatch();
		this.batch.setProjectionMatrix(camera.combined);

		Textures.load();

		this.gravity = new Vector2();
		gravity.set(0, -this.gravityFactor);
		
		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);

		this.world = new World(gravity, true);
		
		this.debugRenderer = new Box2DDebugRenderer();

		Balloon b = Balloon.newInstance(world, (viewportWidth - 48) / 2f, 20f);
		
		this.walls = new ArrayList<Wall>();
		
		this.walls.add(new Wall(world,
				(800 - (Textures.bricksHorizontal.getRegionWidth() * 5))/2f,
				(480 - Textures.bricksHorizontal.getRegionHeight())/2f,
				200f,
				Type.HORIZONTAL)
		);
		
		this.walls.add(new Wall(world,
				(880 - Textures.bricksHorizontal.getRegionWidth())/2f,
				(480 - (Textures.bricksHorizontal.getRegionHeight() * 5))/2f,
				100f,
				Type.VERTICAL)
		);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0, 0.0f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		if( Gdx.app.getType() == ApplicationType.Android ) {
			gravity.set(Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());
			//		orientation.nor();
			gravity.mul(this.gravityFactor);
			world.setGravity(gravity);
		} 

		this.world.step(1/45f, 6, 2);
		this.debugRenderer.render(world, camera.combined);
		
		Iterator<Body> bi = world.getBodies();

		this.batch.begin();
		while (bi.hasNext()){
			Body b = bi.next();

			// Get the bodies user data - in this example, our user 
			// data is an instance of the Entity class
			GameObject o = (GameObject) b.getUserData();

			if (o != null) {
				boolean objectRequiresTranslation = false;
				
				Vector2 position = b.getPosition();
				float x = position.x;
				float y = position.y;
				
				if( o instanceof Balloon ) {
					if( x < 0 ) {
						x = 800;
						objectRequiresTranslation = true;
					} else if ( x > 800 ) {
						x = 0;
						objectRequiresTranslation = true;
					}

					if( y < 0 ) {
						y = 480;
						objectRequiresTranslation = true;
					} else if ( y > 480 ) {
						y = 0;
						objectRequiresTranslation = true;
					}
					
					o.applyForce(this.world.getGravity().mul(b.getMass()).mul(-5f));
				}
				
				o.setAngle(MathUtils.radiansToDegrees * b.getAngle());
				o.setPosition(x, y, objectRequiresTranslation);
				o.draw(batch);
			}
		}

		Iterator<Wall> wi = walls.iterator();
		
		while(wi.hasNext()) {
			Wall w = wi.next();
			w.draw(batch);
		}
		
		this.batch.end();		

		
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
