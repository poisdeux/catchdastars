package com.strategames.catchdastars.game;

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
import com.strategames.catchdastars.screens.editor.GameEditorScreen;
import com.strategames.catchdastars.screens.editor.LevelEditorScreen;
import com.strategames.catchdastars.screens.editor.SelectGameScreen;
import com.strategames.catchdastars.screens.game.LevelScreen;
import com.strategames.catchdastars.screens.game.SettingsScreen;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Balloon;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.gameobject.types.Icecube;
import com.strategames.engine.gameobject.types.RectangularSensor;
import com.strategames.engine.gameobject.types.Star;
import com.strategames.engine.gameobject.types.Wall;
import com.strategames.engine.gameobject.types.WallHorizontal;
import com.strategames.engine.gameobject.types.WallVertical;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.storage.LevelWriter;
import com.strategames.engine.tweens.GameObjectAccessor;
import com.strategames.engine.utils.Collectable;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.Score;
import com.strategames.engine.utils.Textures;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.dialogs.LevelFailedDialog;
import com.strategames.ui.dialogs.LevelPausedDialog;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

public class CatchDaStars extends GameEngine implements OnClickListener {
	private Vector2 gravityVector;

	private World world;

	private Array<GameObject> availableGameObjects;

	private boolean accelerometerAvailable;

    private final String BLUE_BALLOON = "01_balloonBlue";
    private final String RED_BALLOON = "02_balloonRed";
    private final String BLUE_STAR = "03_starBlue";
    private final String RED_STAR = "04_starRed";
    private final String GOLD_STAR = "05_starGold";

	private Collectable redCollectables;
	private Collectable blueCollectables;
	private Collectable goldCollectables;
	private int amountOfBlueBalloons;
	private int amountOfRedBalloons;
	private int amountOfBlueBalloonsFromPreviousLevel;
	private int amountOfRedBalloonsFromPreviousLevel;
	private int amountBalloonsInGame;
    private Score score = new Score();

	private Box2DDebugRenderer debugRenderer;

    private boolean doorsOpen;

	private int[] nextLevelPosition;

	private Level level;
	
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
        super.create();

        this.gravityVector = new Vector2();
		this.gravityVector.set(0, -GRAVITY);

		this.accelerometerAvailable = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);

		//		this.debugRenderer = new Box2DDebugRenderer();

		//We create a dummy world to make sure Box2D library is loaded before
		//we create any game objects. This might be fixed in a future version
		//of libgdx.
		this.world = new World(this.gravityVector, true);
		setWorld(this.world);
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

		//		this.debugRenderer.render(world, ((AbstractScreen) getScreen()).getGameCamera().combined);
	}

	@Override
	public void resetGame() {
		this.amountOfBlueBalloons = 0;
		this.amountOfRedBalloons = 0;
        this.doorsOpen = false;
		super.resetGame();
	}
	
	@Override
	public void resetLevel() {
		this.amountOfBlueBalloons = this.amountOfBlueBalloonsFromPreviousLevel;
		this.amountOfRedBalloons = this.amountOfRedBalloonsFromPreviousLevel;
        this.doorsOpen = false;
		super.resetLevel();
	}
	
	@Override
	public boolean setup(Stage stage) {
		System.gc(); //hint the garbage collector that now is a good time to collect

		Game game = getGame();
		if( game == null ) {
			Gdx.app.log("CatchDaStars", "setup: game==null");
			return false;
		}

		int[] pos = game.getCurrentLevelPosition();
		this.level = game.getLevel(pos[0], pos[1]);
		if( this.level == null ) {
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

		int tmpAmountOfBlueBalloons = this.amountOfBlueBalloonsFromPreviousLevel = this.amountOfBlueBalloons;
		int tmpAmountOfRedBalloons = this.amountOfRedBalloonsFromPreviousLevel = this.amountOfRedBalloons;
		
		this.amountOfBlueBalloons = 0;
		this.amountOfRedBalloons = 0;
		this.amountBalloonsInGame = 0;
		
		
		Array<Wall> border = new Array<Wall>();

		for(GameObject gameObject : gameObjects ) {
			if( gameObject instanceof Star ) {
				addStar((Star) gameObject, stage);
			} else if( gameObject instanceof Balloon ) {
				if( ! gameObject.isNew() ) { // Add surviving balloons from previous level
					if( gameObject instanceof BalloonBlue ) {
						if( tmpAmountOfBlueBalloons-- > 0 ) {
							addBalloon((Balloon) gameObject, stage);
						}
					} else if( gameObject instanceof BalloonRed ) {
						if( tmpAmountOfRedBalloons-- > 0 ) {
							addBalloon((Balloon) gameObject, stage);
						}
					}
				} else { // add new balloons 
					addBalloon((Balloon) gameObject, stage);
				}
			} else if( gameObject instanceof Icecube ) {
				addIcecube((Icecube) gameObject, stage);
			} else if( gameObject instanceof Wall ) {
				Wall w = (Wall) gameObject;
				addWall(w, stage);
				if(w.isBorder()) {
					border.add(w);
				}
			}
		}

		addDoors(level, border, stage);

		setupLeaveScreenSensor(stage);

		return true;
		//		Gdx.app.log("CatchDaStars", "initialize: this.blueCollectables="+this.blueCollectables);
	}

	private void addStar(Star star, Stage stage) {
		if( star instanceof StarBlue ) {
			this.blueCollectables.add();
		} else if( star instanceof StarRed ) {
			this.redCollectables.add();
		} else if( star instanceof StarYellow ) {
			this.goldCollectables.add();
		}
		addGameObject(star, stage);
	}

	private void addBalloon(Balloon balloon, Stage stage) {
		if( balloon instanceof BalloonBlue ) {
			amountOfBlueBalloons++;
		} else if( balloon instanceof BalloonRed ) {
			amountOfRedBalloons++;
		}
		this.amountBalloonsInGame++;
		addGameObject(balloon, stage);
	}

	private void addDoors(Level level, Array<Wall> border, Stage stage) {
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
	}

	private void addIcecube(Icecube icecube, Stage stage) {
		icecube.addAllParts();
		addGameObject(icecube, stage);
	}

	private void addWall(Wall wall, Stage stage) {
		addGameObject(wall, stage);
	}

	private void setupLeaveScreenSensor(Stage stage) {
		//We must make sure balloon is out of screen when
		//sensor is hit 
		float margin = Wall.WIDTH * 2f;
		RectangularSensor sensor = new RectangularSensor(null);
		sensor.setStart(new Vector2(-margin, -margin));
		sensor.setEnd(new Vector2(stage.getWidth(), stage.getHeight()).add(margin, margin));
		addGameObject(sensor, stage);
	}

	public void showLevelEditor() {
		Screen screen = new LevelEditorScreen(this);
		setScreen(screen);
		addToBackstack(screen);
	}

	public void showLevelEditorMenu() {
		Screen screen = new GameEditorScreen(this);
		setScreen(screen);
		addToBackstack(screen);
	}

	public void showSelectGameScreen() {
		Screen screen = new com.strategames.catchdastars.screens.game.SelectGameScreen(this);
		setScreen(screen);
		addToBackstack(screen);
	}

    public void showGameMenuScreen() {
        Screen screen = new com.strategames.catchdastars.screens.game.GameMenuScreen(this);
        setScreen(screen);
        addToBackstack(screen);
    }

	public void showGameEditorScreen() {
		Screen screen = new SelectGameScreen(this);
		setScreen(screen);
		addToBackstack(screen);
	}
	
	public void showSettings() {
		Screen screen = new SettingsScreen(this);
		setScreen(screen);
		addToBackstack(screen);
	}

    @Override
    public void calculateScore(Score score) {
        Textures textures = Textures.getInstance();
        score.addItem(BLUE_BALLOON, new Image(textures.balloonBlue), 10, amountOfBlueBalloons);
        score.addItem(RED_BALLOON, new Image(textures.balloonRed), 10, amountOfRedBalloons);
        score.addItem(BLUE_STAR, new Image(textures.starBlue), 1,  this.blueCollectables.getAmountCollected());
        score.addItem(RED_STAR, new Image(textures.starRed), 1, this.redCollectables.getAmountCollected());
        score.addItem(GOLD_STAR, new Image(textures.starYellow), 5, this.goldCollectables.getAmountCollected());
    }

    private void openDoors() {
        if( this.doorsOpen ) {
            return;
        }

        this.doorsOpen = true;

		AbstractScreen screen = ((AbstractScreen) getScreen());
		Stage stage = screen.getStageActors();

		Array<Door> doors = this.level.getDoors();
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
		getWorldThread().startTimeline(timeline); // as Wall has a body which is moved we need to make sure we do not move while worldstep is running
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
		getWorldThread().startTimeline(timeline); // as Wall has a body which is moved we need to make sure we do not move while worldstep is running
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
	public Array<GameObject> getAvailableGameObjects() {
		if( this.availableGameObjects != null ) {
			return this.availableGameObjects;
		}

		Array<GameObject> objects = new Array<GameObject>();

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

	/**
	 * TODO destroyBalloon called multiple times when balloon is destroyed
	 * @param balloon
	 */
	private void destroyBalloon(Balloon balloon) {
		if( balloon.isToBeDestroyed() ) {
            return;
		}

        balloon.destroy();
        deleteGameObject(balloon);

		if( balloon instanceof BalloonBlue ) {
			amountOfBlueBalloons--;
		} else if( balloon instanceof BalloonRed ) {
			amountOfRedBalloons--;
		}
		this.amountBalloonsInGame--;
		
	}

	private void handleSensorCollision(Balloon balloon, GameObject object) {
		if( ! isRunning() ) {
			return;
		}

//		Gdx.app.log("CatchDaStars", "handleSensorCollision: START balloon="+balloon+"\nobject="+object);


		if( object instanceof Star ) {
			GameObject star = (Star) object;
			if( star instanceof StarYellow ) {
				if( ! star.isToBeDestroyed() ) {
					star.destroy();
					deleteGameObject(star);
					this.goldCollectables.collect(star);
				}
			} else if( ( balloon instanceof BalloonBlue ) && ( star instanceof StarBlue ) ) {
				if( ! star.isToBeDestroyed() ) {
					star.destroy();
					deleteGameObject(star);
					this.blueCollectables.collect(star);
				}
			} else if( ( balloon instanceof BalloonRed ) && ( star instanceof StarRed ) ) {
				if( ! star.isToBeDestroyed() ) {
					star.destroy();
					deleteGameObject(star);
					this.redCollectables.collect(star);
				}
			} else {
				destroyBalloon(balloon);
			}
		} else if( object instanceof Door ) {
			this.nextLevelPosition = ((Door) object).getNextLevelPosition();
		} else if ( object instanceof RectangularSensor ) {
			if( balloon.isInGame() ) {
				getWorldThread().setGameObjectInactive(balloon);
				balloon.setInGame(false);
				if( --this.amountBalloonsInGame < 1 ) {
					setLevelCompleted();
				}
			}
		}

		if( ( amountOfBlueBalloons < 1 ) && ( amountOfRedBalloons < 1 ) ) {
			setLevelFailed();
		}

		//Check if all collectables have been retrieved
		if( this.blueCollectables.allCollected() &&
				this.redCollectables.allCollected() &&
				this.goldCollectables.allCollected() ) {
            openDoors();
		}
		
//		Gdx.app.log("CatchDaStars", "handleSensorCollision: END");
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
//		Gdx.app.log("CatchDaStars", "beginContact: START");
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		GameObject collidingGameObject1 = (GameObject) fixtureA.getBody().getUserData();
		GameObject collidingGameObject2 = (GameObject) fixtureB.getBody().getUserData();
		if( ( collidingGameObject1 instanceof Balloon ) && ( fixtureB.isSensor() ) ) {
			handleSensorCollision((Balloon) collidingGameObject1, collidingGameObject2);
		} else if(( collidingGameObject2 instanceof Balloon ) && ( fixtureA.isSensor() )) {
			handleSensorCollision((Balloon) collidingGameObject2, collidingGameObject1);
		}
//		Gdx.app.log("CatchDaStars", "beginContact: END");
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
//		Gdx.app.log("CatchDaStars", "postSolve: START");
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
//		Gdx.app.log("CatchDaStars", "postSolve: END");
	}

	@Override
	public void onClick(Dialog dialog, int which) {
        Gdx.app.log("CatchDaStars", "Dialog clicked: which="+which);
		dialog.hide();
		if( dialog instanceof LevelCompleteDialog ) {
			switch( which ) {
			case LevelCompleteDialog.BUTTON_NEXT_CLICKED:
				/**
				 * TODO: determine game completed and create scroll animation to new level
				 */
				startLevel(this.nextLevelPosition);
				break;
			case LevelCompleteDialog.BUTTON_QUIT_CLICKED:
				stopScreen();
				break;
			}
		} else if( dialog instanceof LevelFailedDialog ) {
			switch( which ) {
			case LevelFailedDialog.BUTTON_RETRY_CLICKED:
                Gdx.app.log("CatchDaStars", "LevelFailedDialog retry");
				resetLevel();
				break;
			case LevelFailedDialog.BUTTON_QUIT_CLICKED:
				stopScreen();
				break;
			}
		} else if( dialog instanceof LevelPausedDialog ) {
			switch( which ) {
			case LevelPausedDialog.BUTTON_QUIT_CLICKED:
				stopScreen();
				break;
			case LevelPausedDialog.BUTTON_RESUME_CLICKED:
				resumeGame();
				break;
			case LevelPausedDialog.BUTTON_RETRY_CLICKED:
				resetGame();
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

