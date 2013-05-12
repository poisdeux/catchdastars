package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.actors.Balloon;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.actors.GameObject.Type;
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
		objects.add(Balloon.create(null, 0, 0, Balloon.ColorType.BLUE));
		objects.add(Star.create(null, 0, 0, Star.ColorType.BLUE));
		objects.add(Star.create(null, 0, 0, Star.ColorType.YELLOW));
		objects.add(Star.create(null, 0, 0, Star.ColorType.RED));
		objects.add(Wall.create(null, 0, 0, 1, Wall.Orientation.HORIZONTAL));
		objects.add(Wall.create(null, 0, 0, 1, Wall.Orientation.VERTICAL));
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
	
	private void handleBalloonCollision(Balloon balloon, GameObject gameObject) {
		Balloon.ColorType balloonColor = balloon.getColorType();
    	
		Type type = gameObject.getType();
    	if( type == Type.STAR ) {
    		Star star = (Star) gameObject;
    		Star.ColorType starColor = star.getColorType();
    		
    		if( ( balloonColor == Balloon.ColorType.BLUE ) && ( starColor == Star.ColorType.BLUE ) ) {
    			star.destroy();
    		} else {
    			// Destroy balloon
    		}
    	}
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture f1=contact.getFixtureA();
        Body b1=f1.getBody();
        Fixture f2=contact.getFixtureB();
        Body b2=f2.getBody();
        GameObject gameObject1 = (GameObject) b1.getUserData();
        GameObject gameObject2 = (GameObject) b2.getUserData();

        Type type1 = gameObject1.getType();
        Type type2 = gameObject2.getType();
        if( ( type1 == Type.BALLOON ) && ( type2 != Type.BALLOON ) ){
        	handleBalloonCollision((Balloon) gameObject1, gameObject2);
        } else if( ( type1 != Type.BALLOON ) && ( type2 == Type.BALLOON ) ){
        	handleBalloonCollision((Balloon) gameObject2, gameObject1);
        } 
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}
}
