package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.actors.Balloon;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.actors.GameObject.Type;
import com.strategames.catchdastars.actors.Icecube;
import com.strategames.catchdastars.actors.Star;
import com.strategames.catchdastars.actors.Wall;
import com.strategames.catchdastars.screens.AbstractScreen;
import com.strategames.catchdastars.utils.Collectable;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.Textures;
import com.strategames.ui.LevelCompleteDialog;
import com.strategames.ui.LevelFailDialog;
import com.strategames.ui.LevelPauseDialog;

public class CatchDaStars extends Game {
	private Vector2 gravityVector;

	private Box2DDebugRenderer debugRenderer;
	private Camera camera;
	private World world;

	private Stage stageActors;

	private ArrayList<GameObject> availableGameObjects;

	private boolean accelerometerAvailable;
	private boolean gameOn;

	private Collectable redCollectables;
	private Collectable blueCollectables;
	private Collectable goldCollectables;
	private int amountOfBlueBalloons;
	private int amountOfRedBalloons;

	private final int scorePerBalloon = 10;
	private final int scorePerBlueStar = 1;
	private final int scorePerRedStar = 1;
	private final int scorePerGoldStar = 5;

	private GameObject collidingGameObject1;
	private GameObject collidingGameObject2;

	public CatchDaStars() {
		this.redCollectables = new Collectable();
		this.blueCollectables = new Collectable();
		this.goldCollectables = new Collectable();
	}

	@Override
	public void pauseGame() {
		super.pauseGame();
		showLevelPausedDialog();
	}
	
	public void update(float delta, Stage stage) {
		if( this.accelerometerAvailable ) {
			//Accelerometer ranges from -10 to 10. This roughly equals gravity so we do not
			//normalize and then multiply the vector with gravity for performance sake
			this.gravityVector.set(Gdx.input.getAccelerometerY(), -Gdx.input.getAccelerometerX());
			this.world.setGravity(gravityVector);
		}

//		this.debugRenderer.render(world, this.camera.combined);
		
		super.update(delta, stage);
	}

	@Override
	public void setupStage(Stage stage) {
		this.stageActors = stage;

		System.gc(); //hint the garbage collector that now is a good time to collect

		this.camera = stage.getCamera();

		this.debugRenderer = new Box2DDebugRenderer();

		this.gravityVector = new Vector2();
		this.gravityVector.set(0, -GRAVITY);

		this.world = new World(this.gravityVector, true);
		setWorld(this.world);

		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);

		initLevel();

		this.gameOn = true;
	}

	@Override
	public void reset() {
		initLevel();
		this.gameOn = true;
		System.gc(); //hint the garbage collector that now is a good time to collect
	}

	private void showLevelCompleteDialog() {
		darkenActors(0.4f);

		LevelCompleteDialog levelCompleteDialog = new LevelCompleteDialog(this, ((AbstractScreen) getScreen()).getSkin(), 0);

		levelCompleteDialog.add(new Image(Textures.blueBalloon), this.amountOfBlueBalloons, this.scorePerBalloon);
		levelCompleteDialog.add(new Image(Textures.redBalloon), this.amountOfRedBalloons, this.scorePerBalloon);
		levelCompleteDialog.add(new Image(Textures.starBlue), this.blueCollectables.getCollected(), this.scorePerBlueStar);
		levelCompleteDialog.add(new Image(Textures.starRed), this.redCollectables.getCollected(), this.scorePerRedStar);
		levelCompleteDialog.add(new Image(Textures.starYellow), this.goldCollectables.getCollected(), this.scorePerGoldStar);

		((AbstractScreen) getScreen()).showDialog(levelCompleteDialog);
	}
	
	private void showLevelPausedDialog() {
		darkenActors(0.4f);
		
		LevelPauseDialog levelPausedDialog = new LevelPauseDialog(this, ((AbstractScreen) getScreen()).getSkin());

		((AbstractScreen) getScreen()).showDialog(levelPausedDialog);
	}

	private void darkenActors(float factor) {
		Array<Actor> actors = this.stageActors.getActors();
		int size = actors.size;
		for(int i = 0; i < size; i++) {
			Actor actor = actors.get(i);
			Color color = actor.getColor();
			actor.setColor(color.r, color.g, color.b, factor);
		}
	}
	
	@Override
	public ArrayList<GameObject> getAvailableGameObjects() {
		if( this.availableGameObjects != null ) {
			return this.availableGameObjects;
		}

		ArrayList<GameObject> objects = new ArrayList<GameObject>();

		objects.add(Balloon.create(null, 0, 0, Balloon.ColorType.BLUE));
		objects.add(Balloon.create(null, 0, 0, Balloon.ColorType.RED));
		objects.add(Star.create(null, 0, 0, Star.ColorType.BLUE));
		objects.add(Star.create(null, 0, 0, Star.ColorType.YELLOW));
		objects.add(Star.create(null, 0, 0, Star.ColorType.RED));
		objects.add(Wall.create(null, 0, 0, WORLD_TO_BOX, Wall.Orientation.HORIZONTAL));
		objects.add(Wall.create(null, 0, 0, WORLD_TO_BOX, Wall.Orientation.VERTICAL));
		objects.add(Icecube.create(null, 0, 0));

		this.availableGameObjects = objects;

		return this.availableGameObjects;
	}

	private void initLevel() {
		Gdx.app.log("CatchDaStars", "initLevel");
		Level level = getLevel();

		if ( level == null ) {
			return;
		}

		Array<Actor> actors = this.stageActors.getActors();
		for( Actor actor : actors ) {
			GameObject gameObject = (GameObject) actor;
//			gameObject.deleteBody();
			deleteGameObject(gameObject);
		}

		this.stageActors.clear();

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

	private void handleBalloonCollision(Contact contact, ContactImpulse impulse, Balloon balloon, GameObject gameObject) {
		if( ! this.gameOn ) {
			return;
		}

		Balloon.ColorType balloonColor = balloon.getColorType();

		Type type = gameObject.getType();
		if( type == Type.STAR ) {
			Star star = (Star) gameObject;
			Star.ColorType starColor = star.getColorType();

			if( starColor == Star.ColorType.YELLOW ) {
				star.destroy();
				deleteGameObject(star);
				this.goldCollectables.collect();
			} else if( ( balloonColor == Balloon.ColorType.BLUE ) && ( starColor == Star.ColorType.BLUE ) ) {
				star.destroy();
				deleteGameObject(star);
				this.blueCollectables.collect();
			} else if( ( balloonColor == Balloon.ColorType.RED ) && ( starColor == Star.ColorType.RED ) ) {
				star.destroy();
				deleteGameObject(star);
				this.redCollectables.collect();
			} else {
				balloon.destroy();
				deleteGameObject(balloon);
				if( balloonColor == Balloon.ColorType.BLUE ) {
					this.amountOfBlueBalloons--;
				} else if( balloonColor == Balloon.ColorType.RED ) {
					this.amountOfRedBalloons--;
				}
			}
		} else if ( type == Type.WALL ) {
			balloon.handleCollision(contact, impulse, gameObject);
		}

		if( ( this.amountOfBlueBalloons < 1 ) && ( ! this.blueCollectables.allCollected() ) ) {
			this.gameOn = false;
			LevelFailDialog dialog = new LevelFailDialog(this, ((AbstractScreen) getScreen()).getSkin());
			((AbstractScreen) getScreen()).showDialog(dialog);
		}

		if( ( this.amountOfRedBalloons < 1 ) && ( ! this.redCollectables.allCollected() ) ) {
			this.gameOn = false;
			LevelFailDialog dialog = new LevelFailDialog(this, ((AbstractScreen) getScreen()).getSkin());
			((AbstractScreen) getScreen()).showDialog(dialog);
		}

		//Check if all collectables have been retrieved
		if( this.blueCollectables.allCollected() &&
				this.redCollectables.allCollected() &&
				this.goldCollectables.allCollected() ) {
			this.gameOn = false;
			showLevelCompleteDialog();
		}
	}

	@Override
	public void beginContact(Contact contact) {
		//		Gdx.app.log("CatchDaStars", "beginContact");
		this.collidingGameObject1 = (GameObject) contact.getFixtureA().getBody().getUserData();
		this.collidingGameObject2 = (GameObject) contact.getFixtureB().getBody().getUserData();
		Type type1 = this.collidingGameObject1.getType();
		Type type2 = this.collidingGameObject2.getType();
		if( ( type1 == Type.BALLOON ) && ( type2 != Type.BALLOON ) ) {
			handleBalloonCollision(contact, null, (Balloon) this.collidingGameObject1, this.collidingGameObject2);
		} else if( ( type1 != Type.BALLOON ) && ( type2 == Type.BALLOON ) ) {
			handleBalloonCollision(contact, null, (Balloon) this.collidingGameObject2, this.collidingGameObject1);
		} 
	}

	@Override
	public void endContact(Contact contact) {
		//		Gdx.app.log("CatchDaStars", "endContact");
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		//		Gdx.app.log("CatchDaStars", "preSolve");
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		Type type1 = this.collidingGameObject1.getType();
		Type type2 = this.collidingGameObject2.getType();
		if( type1 == Type.ROCK ) {
			this.collidingGameObject1.handleCollision(contact, impulse, this.collidingGameObject2);
		}
		if( type2 == Type.ROCK ) {
			this.collidingGameObject2.handleCollision(contact, impulse, this.collidingGameObject1);
		}
	}
	
	
}

