package com.strategames.catchdastars;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.actors.Balloon;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.actors.GameObject.Type;
import com.strategames.catchdastars.actors.Star;
import com.strategames.catchdastars.actors.Wall;
import com.strategames.catchdastars.screens.AbstractScreen;
import com.strategames.catchdastars.utils.Collectable;
import com.strategames.catchdastars.utils.Level;

public class CatchDaStars extends Game {
	private Vector2 gravity;
	private float gravityFactor = 10;
	private Box2DDebugRenderer debugRenderer;
	private Camera camera;
	private World world;
	
	private Stage stageActors;
	private Stage stageLevelCompleteActors;
	private Stage stageLevelFailedActors;
	
	private ArrayList<GameObject> gameObjectsForDeletion;
	
	private boolean accelerometerAvailable;
	
	
	private Collectable redCollectables;
	private Collectable blueCollectables;
	private Collectable goldCollectables;
	private int amountOfBlueBalloons;
	private int amountOfRedBalloons;

	public CatchDaStars() {
		this.redCollectables = new Collectable();
		this.blueCollectables = new Collectable();
		this.goldCollectables = new Collectable();
		
		this.gameObjectsForDeletion = new ArrayList<GameObject>();
	}

	public void update(float delta, Stage stage) {
		if( this.accelerometerAvailable ) {
			this.gravity.set(Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());
			this.gravity.mul(this.gravityFactor);
			this.world.setGravity(gravity);
		} 

		stageActors.act();
		
		this.world.step(1/45f, 6, 2);
		this.debugRenderer.render(world, this.camera.combined);
		
		if( this.levelFailed ) {
			stageLevelFailedActors.act();
		} else if ( this.levelComplete ) {
			stageLevelCompleteActors.act();
		}
		
		Iterator<GameObject> itr = this.gameObjectsForDeletion.iterator();
		while(itr.hasNext()) {
			GameObject object = itr.next();
			object.deleteBody();
			itr.remove();
		}
	}

	@Override
	public void setupStage(Stage stage) {
		this.stageActors = stage;
		
		System.gc(); //hint the garbage collector that now is a good time to collect
		
		this.camera = stage.getCamera();
		this.debugRenderer = new Box2DDebugRenderer();

		this.gravity = new Vector2();
		this.gravity.set(0, -this.gravityFactor);

		this.world = new World(this.gravity, true);
		setWorld(this.world);

		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);

		this.levelComplete = false;
		this.levelFailed = false;
		
		loadLevel();
	}

	@Override
	public void setupLevelCompleteStage(Stage stage) {
		this.stageLevelCompleteActors = stage;
	}
	
	@Override
	public void setupLevelFailedStage(Stage stage) {
		this.stageLevelFailedActors = stage;
		
		Skin skin = ((AbstractScreen) getScreen()).getSkin();
		Label gameOverLabel = new Label("Game Over", skin);
		float xMiddle = (Gdx.graphics.getWidth() / 2) - (gameOverLabel.getWidth() / 2);
		gameOverLabel.setPosition(xMiddle, Gdx.graphics.getHeight() / 2);
		
		gameOverLabel.addAction( sequence( fadeIn( 0.25f ) ) );
		
		gameOverLabel.getColor().a = 0f;
		
		stage.addActor(gameOverLabel);
	}
	
	@Override
	public void reset() {
		System.gc(); //hint the garbage collector that now is a good time to collect
		
		AbstractScreen screen = (AbstractScreen) getScreen();
		Stage stage = screen.getStageActors();

		Array<Actor> actors = stage.getActors();
		for( Actor actor : actors ) {
			GameObject gameObject = (GameObject) actor;
			gameObject.deleteBody();
		}

		this.levelComplete = false;
		this.levelFailed = false;
		
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

	private void updateLevelFailedStage() {
		
	}
	
	private void loadLevel() {
		Level level = Level.loadLocal(getCurrentLevel().getLevelNumber());
		setCurrentLevel(level);

		this.redCollectables = new Collectable();
		this.blueCollectables = new Collectable();
		this.goldCollectables = new Collectable();
		
		this.amountOfBlueBalloons = 0;
		this.amountOfRedBalloons = 0;

		ArrayList<GameObject> gameObjects = level.getGameObjects();

		if( gameObjects != null ) {
			for(GameObject gameObject : gameObjects ) {
				addGameObject(gameObject);
				GameObject.Type type = gameObject.getType();
				if( type == GameObject.Type.STAR ) {
					Star star = (Star) gameObject;
					Star.ColorType color = star.getColorType();
					if( color == Star.ColorType.BLUE ) {
						this.blueCollectables.setTotal(this.blueCollectables.getTotal() + 1);
					} else if( color == Star.ColorType.RED ) {
						this.redCollectables.setTotal(this.redCollectables.getTotal() + 1);
					} else if( color == Star.ColorType.YELLOW ) {
						this.goldCollectables.setTotal(this.goldCollectables.getTotal() + 1);
					}
				} else if( type == GameObject.Type.BALLOON ) {
					Balloon balloon = (Balloon) gameObject;
					Balloon.ColorType color = balloon.getColorType();
					if( color == Balloon.ColorType.BLUE ) {
						this.amountOfBlueBalloons++;
					} else if( color == Balloon.ColorType.RED ) {
						this.amountOfRedBalloons++;
					}
				}
			}
		}

	}

	private void handleBalloonCollision(Contact contact, Balloon balloon, GameObject gameObject) {
		Balloon.ColorType balloonColor = balloon.getColorType();

		Type type = gameObject.getType();
		if( type == Type.STAR ) {
			Star star = (Star) gameObject;
			Star.ColorType starColor = star.getColorType();

			if( starColor == Star.ColorType.YELLOW ) {
				star.destroy();
				this.gameObjectsForDeletion.add(star);
				this.goldCollectables.collect();
			} else if( ( balloonColor == Balloon.ColorType.BLUE ) && ( starColor == Star.ColorType.BLUE ) ) {
				star.destroy();
				this.gameObjectsForDeletion.add(star);
				this.blueCollectables.collect();
			} else if( ( balloonColor == Balloon.ColorType.RED ) && ( starColor == Star.ColorType.RED ) ) {
				star.destroy();
				this.gameObjectsForDeletion.add(star);
				this.redCollectables.collect();
			} else {
				balloon.destroy();
				this.gameObjectsForDeletion.add(balloon);
				if( balloonColor == Balloon.ColorType.BLUE ) {
					this.amountOfBlueBalloons--;
				} else if( balloonColor == Balloon.ColorType.RED ) {
					this.amountOfRedBalloons--;
				}
			}
		} else if ( type == Type.WALL ) {
			balloon.handleCollision(contact, gameObject);
		}
		
		if( ( this.amountOfBlueBalloons < 1 ) && ( ! this.blueCollectables.allCollected() ) ) {
			this.levelFailed = true;
			Gdx.app.log("CatchDaStars", "handleBalloonCollision: blue level failed");
		}
		
		if( ( this.amountOfRedBalloons < 1 ) && ( ! this.redCollectables.allCollected() ) ) {
			this.levelFailed = true;
			Gdx.app.log("CatchDaStars", "handleBalloonCollision: red level failed: " + this.redCollectables);
		}
		
		//Check if all collectables have been retrieved
		if( this.blueCollectables.allCollected() &&
				this.redCollectables.allCollected() &&
				this.goldCollectables.allCollected() ) {
			this.levelComplete = true;
			Gdx.app.log("CatchDaStars", "handleBalloonCollision: level completed");
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
			handleBalloonCollision(contact, (Balloon) gameObject1, gameObject2);
		} else if( ( type1 != Type.BALLOON ) && ( type2 == Type.BALLOON ) ){
			handleBalloonCollision(contact, (Balloon) gameObject2, gameObject1);
		}
	}

	@Override
	public void endContact(Contact contact) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		

	}
}
