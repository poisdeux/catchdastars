package com.strategames.catchdastars;

import java.util.ArrayList;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Rectangle;
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
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.dialogs.LevelCompleteDialog;
import com.strategames.catchdastars.gameobjects.BalloonBlue;
import com.strategames.catchdastars.gameobjects.BalloonRed;
import com.strategames.catchdastars.gameobjects.StarBlue;
import com.strategames.catchdastars.gameobjects.StarRed;
import com.strategames.catchdastars.gameobjects.StarYellow;
import com.strategames.catchdastars.screens.LevelEditorMenuScreen;
import com.strategames.catchdastars.screens.LevelEditorScreen;
import com.strategames.catchdastars.screens.SettingsScreen;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Balloon;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.gameobject.types.Icecube;
import com.strategames.engine.gameobject.types.LeaveScreenSensor;
import com.strategames.engine.gameobject.types.Star;
import com.strategames.engine.gameobject.types.Wall;
import com.strategames.engine.gameobject.types.WallHorizontal;
import com.strategames.engine.gameobject.types.WallVertical;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.tweens.GameObjectAccessor;
import com.strategames.engine.utils.Collectable;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.Textures;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.ErrorDialog;
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

	private boolean showScore;

	private int[] nextLevelPosition;

	public CatchDaStars() {
		super();
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
		setViewSize(new Vector2(5.1f, 8.1f));
	}

	@Override
	public void create() {
		this.gravityVector = new Vector2();
		this.gravityVector.set(0, -GRAVITY);

		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);

//		this.debugRenderer = new Box2DDebugRenderer();

		//We create a dummy world to make sure Box2D library is loaded before
		//we create any game objects. This might be fixed in a future version
		//of libgdx.
		this.world = new World(this.gravityVector, true);
		setWorld(this.world);

		super.create();
	}

	@Override
	public void updateWorld() {
		if( this.accelerometerAvailable ) {
			//Accelerometer ranges from -10 to 10. This roughly equals gravity so we do not
			//normalize and then multiply the vector with gravity for performance sake
			this.gravityVector.set(-Gdx.input.getAccelerometerX(), -Gdx.input.getAccelerometerY());
			this.world.setGravity(gravityVector);
		}
	}

	public void updateScreen(float delta, Stage stage) {
		super.updateScreen(delta, stage);
		Icecube.playRocksHitSound();

		if( this.showScore ){
			showScore = false;
			showLevelCompleteDialog();
		}

//		this.debugRenderer.render(world, ((AbstractScreen) getScreen()).getGameCamera().combined);
	}

	@Override
	public void startGame() {
		showScore = false;
		super.startGame();
	}

	@Override
	public boolean setup(Stage stage) {
		System.gc(); //hint the garbage collector that now is a good time to collect

		Level level = getLevel();
		if( level == null ) {
			Gdx.app.log("CatchDaStars", "setup: level==null");
			return false;
		}

		Array<GameObject> gameObjects = level.getGameObjects();
		if ( gameObjects.size == 0 ) {
			Gdx.app.log("CatchDaStars", "setup: gameobjects is empty for level="+level);
			return false;
		}

		//Reset world
		this.world = new World(this.gravityVector, true);
		setWorld(this.world);

		this.redCollectables = new Collectable();
		this.blueCollectables = new Collectable();
		this.goldCollectables = new Collectable();

		this.amountOfBlueBalloons = 0;
		this.amountOfRedBalloons = 0;

		Array<Wall> border = new Array<Wall>();

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
			} else if( gameObject instanceof Wall ) {
				Wall w = (Wall) gameObject;
				if(w.isBorder()) {
					border.add(w);
				}
			}
			addGameObject(gameObject, stage);
		}

		Array<Door> doors = level.getDoors();
		//Setup doors
		for(int i = 0; i < doors.size; i++) {
			Door door = doors.get(i);
			Rectangle rectangle = door.getBoundingRectangle();
			for(int j = 0; j < border.size; j++) {
				Wall wall = border.get(j);
				Rectangle rectangleBorder = wall.getBoundingRectangle();
				if(rectangle.overlaps(rectangleBorder)) {
					door.setWall(wall);
				}
			}
			door.setVisible(false);
			addGameObject(door, stage);
		}

		return true;
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

	public void showLevelEditor() {
		Screen screen = new LevelEditorScreen(this);
		setScreen(screen);
		addToBackstack(screen);
	}

	public void showLevelEditorMenu() {
		Screen screen = new LevelEditorMenuScreen(this);
		setScreen(screen);
		addToBackstack(screen);
	}

	public void showSettings() {
		Screen screen = new SettingsScreen(this);
		setScreen(screen);
		addToBackstack(screen);
	}

	@Override
	public void levelComplete() {
		openDoors();
		//Place sensors at doors
		//showLevelCompleteDialog when user passes through door
	}

	private void openDoors() {
		AbstractScreen screen = ((AbstractScreen) getScreen());
		Stage stage = screen.getStageActors();

		Array<Door> doors = getLevel().getDoors();
		for(int i = 0; i < doors.size; i++) {
			Door door = doors.get(i);
			door.setOpen(true);
			Wall w = door.getWall();
			if(w instanceof WallVertical) {
				openVerticalWall(w, door, screen, stage);
			} else {
				openHorizontalWall(w, door, screen, stage);
			}
		}

		//Setup screen leaving sensor. We must make sure balloon is out of screen when
		//sensor is hit
		float margin = Wall.WIDTH * 1.6f;
		LeaveScreenSensor sensor = new LeaveScreenSensor(null);
		sensor.setStart(new Vector2(-margin, -margin));
		sensor.setEnd(new Vector2(getWorldSize().x, getWorldSize().y).add(margin, margin));
		addGameObject(sensor, stage);
	}

	private void openVerticalWall(Wall w, Door door, AbstractScreen screen, Stage stage) {
		Vector2 cutPoint = new Vector2(door.getX(), door.getY());

		Wall bottom = new WallVertical();
		bottom.setPosition(w.getX(), w.getY());
		bottom.setLength(cutPoint.y - w.getY());
		addGameObject(bottom, stage);

		Wall top = new WallVertical();
		top.setPosition(cutPoint.x, cutPoint.y);
		top.setLength(w.getLength() - bottom.getLength());
		addGameObject(top, stage);

		w.setCanBeRemoved(true);
		deleteGameObject(w);

		Timeline timeline = Timeline.createSequence();
		timeline.push(Tween.to(top, GameObjectAccessor.POSITION_Y, 1f)
				.target(cutPoint.y + door.getHeight()));
		screen.startTimeline(timeline);
	}

	private void openHorizontalWall(Wall w, Door door, AbstractScreen screen, Stage stage) {
		Vector2 cutPoint = new Vector2(door.getX(), door.getY());

		Wall left = new WallHorizontal();
		left.setPosition(w.getX(), w.getY());
		left.setLength(cutPoint.x - w.getX());
		addGameObject(left, stage);

		Wall right = new WallHorizontal();
		right.setPosition(cutPoint.x, cutPoint.y);
		right.setLength(w.getLength() - left.getLength());
		addGameObject(right, stage);

		w.setCanBeRemoved(true);
		deleteGameObject(w);

		Timeline timeline = Timeline.createSequence();
		timeline.push(Tween.to(right, GameObjectAccessor.POSITION_X, 1f)
				.target(cutPoint.x + door.getWidth()));
		screen.startTimeline(timeline);
	}

	private void showLevelCompleteDialog() {
		/**
		 * TODO: refactor to check if all levels have been completed
		 */
		//		if( getLevelPosition() >= LevelLoader.getLastLevelNumber() ) {
		//			setScreen(new GameCompleteScreen(this, screen.getStageActors()));
		//		} else {
		LevelCompleteDialog levelCompleteDialog = new LevelCompleteDialog(((AbstractScreen) getScreen()).getStageUIActors(), this, ((AbstractScreen) getScreen()).getSkin(), getTotalScore());

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
		objects.add(new Door());

		for(GameObject object : objects) {
			object.setGame(this);
			object.setupImage();
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

	private void handleSensorCollision(Balloon balloon, GameObject object) {
		if( ! isRunning() ) {
			return;
		}

		if( object instanceof Star ) {
			GameObject star = (Star) object;
			if( star instanceof StarYellow ) {
				if( ! star.isHit() ) {
					star.startRemoveAnimation();
					deleteGameObject(star);
					this.goldCollectables.collect(star);
				}
			} else if( ( balloon instanceof BalloonBlue ) && ( star instanceof StarBlue ) ) {
				if( ! star.isHit() ) {
					star.startRemoveAnimation();
					deleteGameObject(star);
					this.blueCollectables.collect(star);
				}
			} else if( ( balloon instanceof BalloonRed ) && ( star instanceof StarRed ) ) {
				if( ! star.isHit() ) {
					star.startRemoveAnimation();
					deleteGameObject(star);
					this.redCollectables.collect(star);
				}
			} else {
				destroyBalloon(balloon);
			}
		} else if( object instanceof Door ) {
			this.nextLevelPosition = ((Door) object).getNextLevelPosition();
		} else if ( object instanceof LeaveScreenSensor ) {
			if( ! object.isHit() ) {
				object.setHit(true);
				showScore = true;
			}
		}

		if( ( this.amountOfBlueBalloons < 1 ) && ( ! this.blueCollectables.allCollected() ) ) {
			setLevelFailed();
		}

		if( ( this.amountOfRedBalloons < 1 ) && ( ! this.redCollectables.allCollected() ) ) {
			setLevelFailed();
		}

		//Check if all collectables have been retrieved
		if( this.blueCollectables.allCollected() &&
				this.redCollectables.allCollected() &&
				this.goldCollectables.allCollected() ) {
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
				/**
				 * TODO: determine game completed and create scroll animation to new level
				 */
				startLevel(this.nextLevelPosition[0], this.nextLevelPosition[1]);
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
		} else  if( dialog instanceof ErrorDialog ) {
			switch( which ) {
			case ErrorDialog.BUTTON_CLOSE:
				stopScreen();
			}
		}
	}
}

