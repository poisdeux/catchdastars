package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.actors.Balloon;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.actors.Star;
import com.strategames.catchdastars.actors.Wall;
import com.strategames.catchdastars.utils.Level;

public class CatchDaStars extends Game {
	private Vector2 gravity;
	private float gravityFactor = 10;
	private Box2DDebugRenderer debugRenderer;
	private Camera camera;
	
	
	static final float WORLD_TO_BOX = 0.01f;
	static final float BOX_TO_WORLD = 100f;

	private boolean accelerometerAvailable;

	public CatchDaStars() {
	}

	public void update(float delta) {
		World world = getWorld();
		if( this.accelerometerAvailable ) {
			this.gravity.set(Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());
			this.gravity.mul(this.gravityFactor);
			world.setGravity(gravity);
		} 

		world.step(1/45f, 6, 2);
		this.debugRenderer.render(world, this.camera.combined);	
	}

	@Override
	public void setupStage(Stage stage) {
		this.camera = stage.getCamera();
		this.debugRenderer = new Box2DDebugRenderer();

		this.gravity = new Vector2();
		this.gravity.set(0, -this.gravityFactor);

		setWorld(new World(this.gravity, true));
		
		stage.clear();

		Level level = getCurrentLevel();
		ArrayList<GameObject> gameObjects = level.getGameObjects();

		if( gameObjects != null ) {
			for(GameObject gameObject : gameObjects ) {
				addGameObject(gameObject);
			}
		}

		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);
	}

	@Override
	public ArrayList<GameObject> availableGameObjects() {
		ArrayList<GameObject> objects = new ArrayList<GameObject>();
		objects.add(Balloon.create(null, 0, 0, Balloon.Type.BLUE));
		objects.add(Star.create(null, 0, 0, Star.Type.BLUE));
		objects.add(Star.create(null, 0, 0, Star.Type.YELLOW));
		objects.add(Star.create(null, 0, 0, Star.Type.RED));
		objects.add(Wall.create(null, 0, 0, 1, Wall.Type.HORIZONTAL));
		objects.add(Wall.create(null, 0, 0, 1, Wall.Type.VERTICAL));
		return objects;
	}

	
}
