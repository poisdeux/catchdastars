package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.utils.GameFile;

public class CatchDaStars extends Game {
	private Vector2 gravity;
	private float gravityFactor = 10;
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private Camera camera;
	
	static final float WORLD_TO_BOX = 0.01f;
	static final float BOX_TO_WORLD = 100f;

	private boolean accelerometerAvailable;
	
	public CatchDaStars() {
		
	}

	public void update(float delta) {
		if( this.accelerometerAvailable ) {
			gravity.set(Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());
			gravity.mul(this.gravityFactor);
			world.setGravity(gravity);
		} 

		this.world.step(1/45f, 6, 2);
		this.debugRenderer.render(this.world, this.camera.combined);	
	}
	
	@Override
	public void setupStage(Stage stage) {
		Gdx.app.log("CatchDaStars", "setting up the stage");
		
		this.camera = stage.getCamera();
		this.debugRenderer = new Box2DDebugRenderer();
		
		this.gravity = new Vector2();
		this.gravity.set(0, -this.gravityFactor);

		this.world = new World(this.gravity, true);
		
		stage.clear();

		GameFile gameFile = new GameFile();
		ArrayList<GameObject> gameObjects = gameFile.loadInternal(1);
		
		for(GameObject gameObject : gameObjects ) {
			gameObject.setup(this.world);
			stage.addActor(gameObject);
		}
		
//		this.stars = new ArrayList<Star>();
//		this.stars.add(Star.create(this.world, 400, 200, Star.Type.RED));
//
//		for(Star star : this.stars ) {
//			stage.addActor(star);
//		}
//		
//		this.balloons = new ArrayList<Balloon>();
//		this.balloons.add(Balloon.create(this.world, (stage.getWidth() - 48) / 2f, 20f, Balloon.Type.BLUE));
//
//		for(Balloon balloon : this.balloons ) {
//			stage.addActor(balloon);
//		}
//
//		this.walls = new ArrayList<Wall>();
//		this.walls.add(Wall.create(this.world, 400, 240, 200f, Type.HORIZONTAL));
//		this.walls.add(Wall.create(this.world, 440, 240,	100f, Type.VERTICAL));
//
//		for(Wall wall : this.walls ) {
//			stage.addActor(wall);
//		}
		
		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);
	}
}
