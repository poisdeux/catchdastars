package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
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
import com.strategames.catchdastars.interfaces.Exporter;
import com.strategames.catchdastars.screens.AbstractScreen;
import com.strategames.catchdastars.utils.Collectable;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.Textures;
import com.strategames.ui.dialogs.LevelCompleteDialog;
import com.strategames.ui.dialogs.LevelFailDialog;

public class CatchDaStars extends Game {
	private Vector2 gravityVector;

	private World world;

	private Stage stageActors;

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
		setWorldSize(new Vector2(5.1f, 8.1f));
	}
	
	@Override
	public void create() {
		this.gravityVector = new Vector2();
		this.gravityVector.set(0, -GRAVITY);

		this.world = new World(this.gravityVector, true);
		setWorld(this.world);

		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);
		
		this.debugRenderer = new Box2DDebugRenderer();
		
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
		
		this.debugRenderer.render(world, ((AbstractScreen) getScreen()).getGameCamera().combined);
	}

	@Override
	public void setupStage(Stage stage) {
		this.stageActors = stage;
		
		this.world = new World(this.gravityVector, true);
		setWorld(this.world);
		
		System.gc(); //hint the garbage collector that now is a good time to collect

		initLevel();
	}

	@Override
	public void stopGame() {
		super.stopGame();
		darkenActors(0.5f);
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

	private void showLevelCompleteDialog() {
		darkenActors(0.4f);

		Stage stage = ((AbstractScreen) getScreen()).getStageUIElements();
		
		LevelCompleteDialog levelCompleteDialog = new LevelCompleteDialog(stage, this, ((AbstractScreen) getScreen()).getSkin(), 0);

		levelCompleteDialog.add(new Image(Textures.blueBalloon), this.amountOfBlueBalloons, this.scorePerBalloon);
		levelCompleteDialog.add(new Image(Textures.redBalloon), this.amountOfRedBalloons, this.scorePerBalloon);
		levelCompleteDialog.add(new Image(Textures.starBlue), this.blueCollectables.getCollected(), this.scorePerBlueStar);
		levelCompleteDialog.add(new Image(Textures.starRed), this.redCollectables.getCollected(), this.scorePerRedStar);
		levelCompleteDialog.add(new Image(Textures.starYellow), this.goldCollectables.getCollected(), this.scorePerGoldStar);

		levelCompleteDialog.create();
		
		levelCompleteDialog.show();
		
	}

	private void showLevelFailedDialog() {
		AbstractScreen screen = (AbstractScreen) getScreen();
		
		LevelFailDialog dialog = new LevelFailDialog(screen.getStageUIElements(), this, screen.getSkin());
		dialog.create();
		dialog.show();
	}
	
	private void darkenActors(float factor) {
		if ( this.stageActors == null )
			return;
		
		Array<Actor> actors = this.stageActors.getActors();
		int size = actors.size;
		for(int i = 0; i < size; i++) {
			Actor actor = actors.get(i);
			Color color = actor.getColor();
			actor.setColor(color.r, color.g, color.b, factor);
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
	
	private void initLevel() {
		Level level = getLevel();

		if ( level == null ) {
			return;
		}

		//Make sure Box2D world is not updated while we add and remove objects
		int prevGameState = getGameState();
		setGameState(GAME_STATE_PAUSED);
		
//		//Remove any current actors to make sure we are not left with
//		//any broken parts from breakable objects
//		Array<Actor> actors = this.stageActors.getActors();
//		for( Actor actor : actors ) {
//			GameObject gameObject = (GameObject) actor;
//			gameObject.deleteBody();
//			gameObject.remove();
////			deleteGameObject(gameObject);
//		}

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
		
		setGameState(prevGameState);
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
			stopGame();
			showLevelFailedDialog();
		}

		if( ( this.amountOfRedBalloons < 1 ) && ( ! this.redCollectables.allCollected() ) ) {
			stopGame();
			showLevelFailedDialog();
		}

		//Check if all collectables have been retrieved
		if( this.blueCollectables.allCollected() &&
				this.redCollectables.allCollected() &&
				this.goldCollectables.allCollected() ) {
			stopGame();
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
//		Gdx.app.log("CatchDaStars", "beginContact: type1="+type1.name()+", type2="+type2.name());
		if( ( type1 == Type.BALLOON ) && ( type2 != Type.BALLOON ) ) {
			handleBalloonCollision(contact, null, (Balloon) this.collidingGameObject1, this.collidingGameObject2);
		} else if(( type2 == Type.BALLOON ) && ( type1 != Type.BALLOON )) {
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

