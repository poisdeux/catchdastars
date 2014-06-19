package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
import com.strategames.catchdastars.utils.LevelLoader;
import com.strategames.catchdastars.utils.Textures;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.LevelCompleteDialog;
import com.strategames.ui.dialogs.LevelFailedDialog;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

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

	private GameObject collidingGameObject1;
	private GameObject collidingGameObject2;

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
	public void initialize() {
		System.gc(); //hint the garbage collector that now is a good time to collect

		Level level = getLevel();

		if ( level == null ) {
			return;
		}

		//Reset world
		this.world = new World(this.gravityVector, true);
		setWorld(this.world);
		
		//Make sure Box2D world is not updated while we add and remove objects
		int prevGameState = getGameState();
		setGameState(GAME_STATE_PAUSED);

//		this.stageActors.clear();

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

		setGameState(prevGameState);
	}

	@Override
	public void pauseGame() {
		super.pauseGame();
		darkenActors(0.5f);
	}

	@Override
	public void resumeGame() {
		super.resumeGame();
		darkenActors(1f);
	}

	@Override
	public void startGame() {
		super.startGame();
		darkenActors(1f);
	}

	private int calculateScore() {
		int blueCollectablesScore = this.amountOfBlueBalloons * this.blueCollectables.getCollected() * this.scorePerBlueStar;
		int redCollectablesScore = this.amountOfRedBalloons * this.redCollectables.getCollected() * this.scorePerRedStar;
		int goldCollectablesScore = this.goldCollectables.getCollected() * this.scorePerGoldStar;
		int blueBalloonsScore = this.amountOfBlueBalloons * this.scorePerBalloon;
		int redBalloonsScore = this.amountOfRedBalloons * this.scorePerBalloon;
		return blueCollectablesScore + redCollectablesScore + goldCollectablesScore +
				blueBalloonsScore + redBalloonsScore;
	}

	private void showLevelCompleteDialog() {
		darkenActors(0.4f);

		Stage stage = ((AbstractScreen) getScreen()).getStageUIElements();

		LevelCompleteDialog levelCompleteDialog = new LevelCompleteDialog(stage, this, ((AbstractScreen) getScreen()).getSkin(), getTotalScore());

		levelCompleteDialog.add(new Image(Textures.blueBalloon), this.amountOfBlueBalloons, this.scorePerBalloon);
		levelCompleteDialog.add(new Image(Textures.redBalloon), this.amountOfRedBalloons, this.scorePerBalloon);
		levelCompleteDialog.add(new Image(Textures.starBlue), this.blueCollectables.getCollected(), this.scorePerBlueStar);
		levelCompleteDialog.add(new Image(Textures.starRed), this.redCollectables.getCollected(), this.scorePerRedStar);
		levelCompleteDialog.add(new Image(Textures.starYellow), this.goldCollectables.getCollected(), this.scorePerGoldStar);

		levelCompleteDialog.setOnClickListener(this);

		levelCompleteDialog.create();

		levelCompleteDialog.show();

		setTotalScore(getTotalScore() + calculateScore());
	}

	private void showLevelFailedDialog() {
		AbstractScreen screen = (AbstractScreen) getScreen();

		LevelFailedDialog dialog = new LevelFailedDialog(screen.getStageUIElements(), screen.getSkin());
		dialog.setOnClickListener(this);
		dialog.create();
		dialog.show();
	}

	private void darkenActors(float factor) {
		Level level = getLevel();
		if( level == null )
			return;
		
		ArrayList<GameObject> gameObjects = level.getGameObjects();
		if ( gameObjects == null )
			return;

		for( GameObject gameObject : gameObjects ) {
			Color color = gameObject.getColor();
			gameObject.setColor(color.r, color.g, color.b, factor);
		}
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

		objects.add(new Balloon(this, 0, 0, Balloon.ColorType.BLUE));
		objects.add(new Balloon(this, 0, 0, Balloon.ColorType.RED));
		objects.add(new Star(this, 0, 0, Star.ColorType.BLUE));
		objects.add(new Star(this, 0, 0, Star.ColorType.YELLOW));
		objects.add(new Star(this, 0, 0, Star.ColorType.RED));
		objects.add(new Wall(this, 0, 0, WORLD_TO_BOX, Wall.Orientation.HORIZONTAL));
		objects.add(new Wall(this, 0, 0, WORLD_TO_BOX, Wall.Orientation.VERTICAL));
		objects.add(new Icecube(this, 0, 0));

		this.availableGameObjects = objects;

		return this.availableGameObjects;
	}

	private void destroyBalloon(Balloon balloon, Balloon.ColorType color) {
		balloon.destroy();
		deleteGameObject(balloon);
		if( color == Balloon.ColorType.BLUE ) {
			this.amountOfBlueBalloons--;
		} else if( color == Balloon.ColorType.RED ) {
			this.amountOfRedBalloons--;
		}
	}

	private void handleBalloonCollision(Contact contact, ContactImpulse impulse, Balloon balloon, GameObject gameObject) {
		if( ! isRunning() ) {
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
				destroyBalloon(balloon, balloonColor);
			}
		} else if ( type == Type.WALL ) {
			balloon.handleCollision(contact, impulse, gameObject);
		} else if ( type == Type.ROCK ) {
			Icecube icecube = (Icecube) gameObject;
			//The higher the velocity of the icecube the higher the chance the balloon
			//will pop
			float vel = icecube.getBody().getLinearVelocity().len2();
			float ranVel = MathUtils.random(Icecube.maxVelocitySquared - 6000);
			if ( vel >= ranVel ) {
				destroyBalloon(balloon, balloonColor);
			}
		}


		if( ( this.amountOfBlueBalloons < 1 ) && ( ! this.blueCollectables.allCollected() ) ) {
			pauseGame();
			showLevelFailedDialog();
		}

		if( ( this.amountOfRedBalloons < 1 ) && ( ! this.redCollectables.allCollected() ) ) {
			pauseGame();
			showLevelFailedDialog();
		}

		//Check if all collectables have been retrieved
		if( this.blueCollectables.allCollected() &&
				this.redCollectables.allCollected() &&
				this.goldCollectables.allCollected() ) {
			pauseGame();
			showLevelCompleteDialog();
		}
	}

	@Override
	public void beginContact(Contact contact) {
		this.collidingGameObject1 = (GameObject) contact.getFixtureA().getBody().getUserData();
		this.collidingGameObject2 = (GameObject) contact.getFixtureB().getBody().getUserData();
		Type type1 = this.collidingGameObject1.getType();
		Type type2 = this.collidingGameObject2.getType();
		if( ( type1 == Type.BALLOON ) && ( type2 != Type.BALLOON ) ) {
			handleBalloonCollision(contact, null, (Balloon) this.collidingGameObject1, this.collidingGameObject2);
		} else if(( type2 == Type.BALLOON ) && ( type1 != Type.BALLOON )) {
			handleBalloonCollision(contact, null, (Balloon) this.collidingGameObject2, this.collidingGameObject1);
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
		Type type1 = this.collidingGameObject1.getType();
		Type type2 = this.collidingGameObject2.getType();
		if( type1 == Type.ROCK ) {
			this.collidingGameObject1.handleCollision(contact, impulse, this.collidingGameObject2);
		}
		if( type2 == Type.ROCK ) {
			this.collidingGameObject2.handleCollision(contact, impulse, this.collidingGameObject1);
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

