package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.actors.Balloon;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.actors.Star;
import com.strategames.catchdastars.actors.Wall;
import com.strategames.catchdastars.screens.AbstractScreen;
import com.strategames.catchdastars.utils.Level;

public class CatchDaStars extends Game {
	private Vector2 gravity;
	private float gravityFactor = 10;
	private Box2DDebugRenderer debugRenderer;
	private Camera camera;
	
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
				
		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);
		
		loadLevel();
	}

	@Override
	public void reset() {
		AbstractScreen screen = (AbstractScreen) getScreen();
		Stage stage = screen.getStageActors();
		
		Array<Actor> actors = stage.getActors();
		for( Actor actor : actors ) {
			GameObject gameObject = (GameObject) actor;
			gameObject.deleteBody();
		}
		
		stage.clear();
		
		loadLevel();
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

	private void loadLevel() {
		Level level = Level.loadLocal(getCurrentLevel().getLevelNumber());
		setCurrentLevel(level);
			
		ArrayList<GameObject> gameObjects = level.getGameObjects();

		if( gameObjects != null ) {
			for(GameObject gameObject : gameObjects ) {
				addGameObject(gameObject);
			}
		}

	}
}
