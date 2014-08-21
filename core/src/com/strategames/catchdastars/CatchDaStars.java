package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.gameobjects.BalloonBlue;
import com.strategames.catchdastars.gameobjects.BalloonRed;
import com.strategames.catchdastars.gameobjects.StarBlue;
import com.strategames.catchdastars.gameobjects.StarRed;
import com.strategames.catchdastars.gameobjects.StarYellow;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobjects.Balloon;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.gameobjects.Icecube;
import com.strategames.engine.gameobjects.Star;
import com.strategames.engine.gameobjects.WallHorizontal;
import com.strategames.engine.gameobjects.WallVertical;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.utils.Collectable;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelLoader;
import com.strategames.engine.utils.Textures;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.LevelCompleteDialog;
import com.strategames.ui.dialogs.LevelFailedDialog;

public class CatchDaStars extends Game implements OnClickListener {
	private Vector2 gravityVector;

	private World world;

	private ArrayList<GameObject> availableGameObjects;

	private boolean accelerometerAvailable;

	private Collectable redCollectables;
	private Collectable blueCollectables;
	private Collectable goldCollectables;
	private int amountOfBlueBalloons;
	private int amountOfRedBalloons;

	private final int scorePerBalloon = 10;
	private final int scorePerBlueStar = 1;
	private final int scorePerRedStar = 1;
	private final int scorePerGoldStar = 5;

	private Box2DDebugRenderer debugRenderer;

	public CatchDaStars() {
		setTitle("Catch Da Stars");
		this.redCollectables = new Collectable();
		this.blueCollectables = new Collectable();
		this.goldCollectables = new Collectable();

		/**
		 * World at widescreen aspect ratio making sure grid fits nicely with width 0.3
		 * 8.1/0.3 = 27 (crosses horizontally)
		 * 5.1/0.3 = 17 (crosses vertically)
		 */
		setWorldSize(new Vector3(5.1f, 8.1f, 0f));
	}

	@Override
	public void create() {
		this.gravityVector = new Vector2();
		this.gravityVector.set(0, -GRAVITY);

		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);

		this.debugRenderer = new Box2DDebugRenderer();

		//We create a dummy world to make sure Box2D library is loaded before
		//we create any game objects. This might be fixed in a future version
		//of libgdx.
		this.world = new World(this.gravityVector, true);
		setWorld(this.world);

		super.create();
	}

	public void update(float delta, Stage stage) {
		if( this.accelerometerAvailable ) {
			//Accelerometer ranges from -10 to 10. This roughly equals gravity so we do not
			//normalize and then multiply the vector with gravity for performance sake
			this.gravityVector.set(-Gdx.input.getAccelerometerX(), -Gdx.input.getAccelerometerY());
			this.world.setGravity(gravityVector);
		}
		Icecube.playRocksHitSound();
		super.update(delta, stage);

		//		this.debugRenderer.render(world, ((AbstractScreen) getScreen()).getGameCamera().combined);
	}

	@Override
	public void setup(AbstractScreen screen) {
		System.gc(); //hint the garbage collector that now is a good time to collect

		super.setup(screen);
		
		Level level = getLevel();
		if( level == null ) {
			Gdx.app.log("CatchDaStars", "setup: level==null");
			return;
		}

		ArrayList<GameObject> gameObjects = level.getGameObjects();
		if ( gameObjects.size() == 0 ) {
			Gdx.app.log("CatchDaStars", "setup: gameobjects is empty for level="+level);
			return;
		}

		//Reset world
		this.world = new World(this.gravityVector, true);
		setWorld(this.world);

		this.redCollectables = new Collectable();
		this.blueCollectables = new Collectable();
		this.goldCollectables = new Collectable();

		this.amountOfBlueBalloons = 0;
		this.amountOfRedBalloons = 0;

		for(GameObject gameObject : gameObjects ) {
			if( gameObject instanceof Star ) {
				if( gameObject instanceof StarBlue ) {
					this.blueCollectables.add();
				} else if( gameObject instanceof StarRed ) {
					this.redCollectables.add();
				} else if( gameObject instanceof StarYellow ) {
					this.goldCollectables.add();
				}
			} else if( gameObject instanceof Balloon ) {
				if( gameObject instanceof BalloonBlue ) {
					this.amountOfBlueBalloons++;
				} else if( gameObject instanceof BalloonRed ) {
					this.amountOfRedBalloons++;
				}
			} else if( gameObject instanceof Icecube ) {
				((Icecube) gameObject).addAllParts();
			}
			gameObject.loadSounds();
			addGameObject(gameObject);
		}

		//		Gdx.app.log("CatchDaStars", "initialize: this.blueCollectables="+this.blueCollectables);
	}

	private int calculateScore() {
		int blueCollectablesScore = this.amountOfBlueBalloons * this.blueCollectables.getCollected().size() * this.scorePerBlueStar;
		int redCollectablesScore = this.amountOfRedBalloons * this.redCollectables.getCollected().size() * this.scorePerRedStar;
		int goldCollectablesScore = this.goldCollectables.getCollected().size() * this.scorePerGoldStar;
		int blueBalloonsScore = this.amountOfBlueBalloons * this.scorePerBalloon;
		int redBalloonsScore = this.amountOfRedBalloons * this.scorePerBalloon;
		return blueCollectablesScore + redCollectablesScore + goldCollectablesScore +
				blueBalloonsScore + redBalloonsScore;
	}

	@Override
	public void showLevelCompleteDialog() {
		Stage stage = ((AbstractScreen) getScreen()).getStageUIActors();

		LevelCompleteDialog levelCompleteDialog = new LevelCompleteDialog(stage, this, ((AbstractScreen) getScreen()).getSkin(), getTotalScore());

		Textures textures = Textures.getInstance();
		levelCompleteDialog.add(new Image(textures.balloonBlue), this.amountOfBlueBalloons, this.scorePerBalloon);
		levelCompleteDialog.add(new Image(textures.balloonRed), this.amountOfRedBalloons, this.scorePerBalloon);
		levelCompleteDialog.add(new Image(textures.starBlue), this.blueCollectables.getCollected().size(), this.scorePerBlueStar);
		levelCompleteDialog.add(new Image(textures.starRed), this.redCollectables.getCollected().size(), this.scorePerRedStar);
		levelCompleteDialog.add(new Image(textures.starYellow), this.goldCollectables.getCollected().size(), this.scorePerGoldStar);

		levelCompleteDialog.setOnClickListener(this);

		levelCompleteDialog.create();

		levelCompleteDialog.show();

		setTotalScore(getTotalScore() + calculateScore());
	}

	@Override
	public void showLevelFailedDialog() {
		AbstractScreen screen = (AbstractScreen) getScreen();

		LevelFailedDialog dialog = new LevelFailedDialog(screen.getStageUIActors(), screen.getSkin());
		dialog.setOnClickListener(this);
		dialog.create();
		dialog.show();
	}

	/**
	 * Beware that you do not change the returned array.
	 * It is created only the first time getAvailableGameObjects
	 * is called!
	 */
	@Override
	public ArrayList<GameObject> getAvailableGameObjects() {
		if( this.availableGameObjects != null ) {
			return this.availableGameObjects;
		}

		ArrayList<GameObject> objects = new ArrayList<GameObject>();

		objects.add(new BalloonBlue());
		objects.add(new BalloonRed());
		objects.add(new StarBlue());
		objects.add(new StarRed());
		objects.add(new StarYellow());
		Icecube icecube = new Icecube();
		icecube.addAllParts();
		objects.add(icecube);
		objects.add(new WallHorizontal());
		objects.add(new WallVertical());

		for(GameObject object : objects) {
			object.setGame(this);
			object.setup();
		}

		this.availableGameObjects = objects;

		return this.availableGameObjects;
	}

	private void destroyBalloon(Balloon balloon) {
		synchronized (balloon) {
			balloon.startRemoveAnimation();
			deleteGameObject(balloon);
			if( balloon instanceof BalloonBlue ) {
				this.amountOfBlueBalloons--;
			} else if( balloon instanceof BalloonBlue ) {
				this.amountOfRedBalloons--;
			}
		}
	}

	private void handleSensorCollision(Balloon balloon, GameObject gameObject) {
		if( ! isRunning() ) {
			return;
		}

		if( gameObject instanceof Star ) {
			if( gameObject instanceof StarYellow ) {
				synchronized (gameObject) {
					if( ! gameObject.isHit() ) {
						gameObject.startRemoveAnimation();
						deleteGameObject(gameObject);
						this.goldCollectables.collect(gameObject);
					}
				}
			} else if( ( balloon instanceof BalloonBlue ) && ( gameObject instanceof StarBlue ) ) {
				if( ! gameObject.isHit() ) {
					gameObject.startRemoveAnimation();
					deleteGameObject(gameObject);
					this.blueCollectables.collect(gameObject);
				}
			} else if( ( balloon instanceof BalloonRed ) && ( gameObject instanceof StarRed ) ) {
				if( ! gameObject.isHit() ) {
					gameObject.startRemoveAnimation();
					deleteGameObject(gameObject);
					this.redCollectables.collect(gameObject);
				}
			} else {
				destroyBalloon(balloon);
			}
		}


		if( ( this.amountOfBlueBalloons < 1 ) && ( ! this.blueCollectables.allCollected() ) ) {
			pauseGame();
			setLevelFailed();
		}

		if( ( this.amountOfRedBalloons < 1 ) && ( ! this.redCollectables.allCollected() ) ) {
			pauseGame();
			setLevelFailed();
		}

		//Check if all collectables have been retrieved
		if( this.blueCollectables.allCollected() &&
				this.redCollectables.allCollected() &&
				this.goldCollectables.allCollected() ) {
			pauseGame();
			setLevelCompleted();
		}
	}

	private void handleBalloonRockCollision(ContactImpulse impulse, Balloon balloon, GameObject gameObject) {
		if ( gameObject instanceof Icecube ) {
			if ( impulse.getNormalImpulses()[0] > 2 ) {
				destroyBalloon(balloon);
			}
		}
	}

	/**
	 * Info on collision handling by Box2D
	 * http://www.iforce2d.net/b2dtut/collision-anatomy
	 */

	/**
	 * beginContact is called when two fixtures make contact
	 */
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		GameObject collidingGameObject1 = (GameObject) fixtureA.getBody().getUserData();
		GameObject collidingGameObject2 = (GameObject) fixtureB.getBody().getUserData();
		//		this.typeCollidingGameObject1 = this.collidingGameObject1.getType();
		//		this.typeCollidingGameObject2 = this.collidingGameObject2.getType();

		if( ( collidingGameObject1 instanceof Balloon ) && ( fixtureB.isSensor() ) ) {
			handleSensorCollision((Balloon) collidingGameObject1, collidingGameObject2);
		} else if(( collidingGameObject2 instanceof Balloon ) && ( fixtureA.isSensor() )) {
			handleSensorCollision((Balloon) collidingGameObject2, collidingGameObject1);
		}
	}

	@Override
	public void endContact(Contact contact) {
	}

	/**
	 * Note that when an object hits a sensor object (like a star)
	 * preSolve and postSolve will not be called
	 */
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		GameObject collidingGameObject1 = (GameObject) contact.getFixtureA().getBody().getUserData();
		GameObject collidingGameObject2 = (GameObject) contact.getFixtureB().getBody().getUserData();
		if( ( collidingGameObject1 instanceof Balloon ) && ( collidingGameObject2 instanceof Icecube ) ) {
			handleBalloonRockCollision(impulse, (Balloon) collidingGameObject1, collidingGameObject2);
		} else if( ( collidingGameObject2 instanceof Balloon ) && ( collidingGameObject1 instanceof Icecube ) ) {
			handleBalloonRockCollision(impulse, (Balloon) collidingGameObject2, collidingGameObject1);
		} else {
			collidingGameObject2.handleCollision(contact, impulse, collidingGameObject1);
			collidingGameObject1.handleCollision(contact, impulse, collidingGameObject2);
		}
	}

	@Override
	public void onClick(Dialog dialog, int which) {
		dialog.hide();
		if( dialog instanceof LevelCompleteDialog ) {
			switch( which ) {
			case LevelCompleteDialog.BUTTON_NEXT_CLICKED:
				if( getLevelNumber() < LevelLoader.getLastLevelNumber() ) {
					startLevel(getLevelNumber() + 1);
				} else {
					//Ooops. User completed game so we should not
					//get to this point but Game end animation should
					//be shown
					Gdx.app.log("CatchDaStars", "onClick: end of game reached");
				}
				break;
			case LevelCompleteDialog.BUTTON_QUIT_CLICKED:
				Gdx.app.log("CatchDaStars", "onClick: BUTTON_QUIT_CLICKED");
				stopScreen();
				break;
			}
		} else if( dialog instanceof LevelFailedDialog ) {
			switch( which ) {
			case LevelFailedDialog.BUTTON_RETRY_CLICKED:
				reset();
				break;
			case LevelFailedDialog.BUTTON_QUIT_CLICKED:
				stopScreen();
				break;
			}
		}
	}
}

