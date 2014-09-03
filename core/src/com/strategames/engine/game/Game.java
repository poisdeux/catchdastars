package com.strategames.engine.game;

import java.util.ArrayList;
import java.util.Stack;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.dialogs.LevelCompleteDialog;
import com.strategames.catchdastars.screens.LevelEditorMenuScreen;
import com.strategames.catchdastars.screens.LevelEditorScreen;
import com.strategames.catchdastars.screens.LevelScreen;
import com.strategames.catchdastars.screens.MainMenuScreen;
import com.strategames.catchdastars.screens.SettingsScreen;
import com.strategames.catchdastars.screens.SplashScreen;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.interfaces.ExportImport;
import com.strategames.engine.interfaces.MusicSelector;
import com.strategames.engine.interfaces.OnMusicFilesReceivedListener;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.tweens.ActorAccessor;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelLoader;
import com.strategames.engine.utils.LevelLoader.OnLevelLoadedListener;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Textures;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.LevelFailedDialog;

abstract public class Game extends com.badlogic.gdx.Game implements OnClickListener, ContactListener, OnMusicFilesReceivedListener {
	public enum GAME_STATE {
		NONE, RUNNING, PAUSED
	};
	private GAME_STATE gameState = GAME_STATE.NONE;

	private enum LEVEL_STATE {
		NONE, INPROGRESS, FAILED, COMPLETE
	};
	
	private LEVEL_STATE levelState = LEVEL_STATE.NONE;
	
	public static final float FRAMES_PER_SECOND = 1/60f;
	public static final float BOX2D_UPDATE_FREQUENCY = 1f/30f;
	private final int BOX2D_VELOCITY_ITERATIONS = 6;
	private final int BOX2D_POSITION_ITERATIONS = 3;
	private WorldThread worldThread;

	public static final float BOX_TO_WORLD = 100f;
	public static final float WORLD_TO_BOX = 1/BOX_TO_WORLD;

	public static final float GRAVITY = 9.81f;

	private Array<GameObject> gameObjectsForDeletion;
	private Array<GameObject> gameObjectsForAddition;
	private Array<GameObject> gameObjectsInGame;

	private AssetManager manager;

	private int levelNumber;

	private Level level; 

	private World world;

	private Vector3 worldSize = new Vector3(0f, 0f, 0f);

	private ExportImport exportimport;
	private MusicSelector musicSelector;

	private String title;

	private Stack<Screen> backStack;

	private Screen currentScreen;
	private Screen newScreen;

	private int totalScore;

	private Stage stageActors;

	private FPSLogger fpsLogger;

	public Game() {
		this.title = "No name game";
		this.manager = new AssetManager();
		this.gameObjectsForDeletion = new Array<GameObject>();
		this.gameObjectsForAddition = new Array<GameObject>();
		this.gameObjectsInGame = new Array<GameObject>();
		this.backStack = new Stack<Screen>();
		this.fpsLogger = new FPSLogger();
		this.worldThread = new WorldThread(this, BOX2D_UPDATE_FREQUENCY, BOX2D_VELOCITY_ITERATIONS, BOX2D_POSITION_ITERATIONS);
		registerTweens();
	}

	public Game(String title) {
		this();
		this.title = title;
	}

	@Override
	public void create() {
		showSplashScreen();
	}

	@Override
	public void dispose () {
		if (currentScreen != null) currentScreen.hide();
		try {
			Textures.getInstance().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void pause () {
		if (currentScreen != null) currentScreen.pause();
	}

	@Override
	public void resume () {
		if (currentScreen != null) currentScreen.resume();
	}

	@Override
	public void render () {
		if (currentScreen != null) currentScreen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize (int width, int height) {
		if (currentScreen != null) currentScreen.resize(width, height);
	}

	@Override
	public void setScreen(Screen screen) {
		this.newScreen = screen;
		if( this.currentScreen != null ) {
			this.currentScreen.hide();
		}
		if( ! ( currentScreen instanceof AbstractScreen ) ) {
			this.newScreen.show();
			this.newScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			this.currentScreen = this.newScreen;
		}
	}

	@Override
	public Screen getScreen() {
		return this.currentScreen;
	}

	/**
	 * Notify the Game manager that screen is now hidden
	 * @param currentScreen the screen that is now hidden
	 */
	public void notifyScreenHidden() {
		if( this.newScreen != null ) {
			this.newScreen.show();
			this.newScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			this.currentScreen = this.newScreen;
		}
	}

	public void pauseGame() {
		this.gameState = GAME_STATE.PAUSED;
		MusicPlayer.getInstance().pause();
		if( this.worldThread != null ) {
			this.worldThread.stopThread();
		}
	}

	public void resumeGame() {
		this.gameState = GAME_STATE.RUNNING;
		MusicPlayer.getInstance().resume();
		startBox2DThread();
	}

	public void startGame() {
		this.gameState = GAME_STATE.RUNNING;
		this.levelState = LEVEL_STATE.INPROGRESS;
		MusicPlayer.getInstance().resume();
		startBox2DThread();
	}

	public void setLevelCompleted() {
		this.levelState = LEVEL_STATE.COMPLETE;
	}

	public void setLevelFailed() {
		this.levelState = LEVEL_STATE.FAILED;
	}

	public void setLevelInProgress() {
		this.levelState = LEVEL_STATE.INPROGRESS;
	}
	
	public boolean isRunning() {
		return this.gameState == GAME_STATE.RUNNING;
	}

	public boolean isPaused() {
		return this.gameState == GAME_STATE.PAUSED;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setExporterImporter(ExportImport exportimport) {
		this.exportimport = exportimport;
	}

	public ExportImport getExporterImporter() {
		return this.exportimport;
	}

	public void setMusicSelector(MusicSelector musicSelector) {
		this.musicSelector = musicSelector;
	}

	public void selectMusicFiles() {
		if( this.musicSelector != null ) {
			this.musicSelector.selectMusic(this);
		}
	}

	public MusicSelector getMusicSelector() {
		return musicSelector;
	}

	public void addToBackstack(Screen screen) {
		this.backStack.add(screen);
	}

	public Screen popBackstack() {
		return this.backStack.pop();
	}

	public Screen peekBackStack() {
		if( this.backStack.size() > 0 ) {
			return this.backStack.peek();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return size of the world in meters
	 */
	public Vector3 getWorldSize() {
		return worldSize;
	}

	/**
	 * Note that world size is not the same as screen size
	 * It is the size of the world as used by Box2D
	 * @param worldSize in meters
	 */
	public void setWorldSize(Vector3 worldSize) {
		this.worldSize = worldSize;
	}

	/**
	 * Use this to convert screen pixel sizes to Box2D sizes
	 * @param x size in screen pixels
	 * @return size in Box2D 
	 */
	static public float convertScreenToWorld(float x) {
		return x * WORLD_TO_BOX;
	}

	/**
	 * Use this to convert Box2D sizes to screen pixel sizes 
	 * @param x size in Box2D
	 * @return size in pixels
	 */
	static public float convertWorldToScreen(float x) {
		return x * BOX_TO_WORLD;
	}

	public int getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}

	public void setLevel(Level level) {
		this.level = level;
		if( level != null ) {
			setLevelNumber(level.getLevelNumber());
		}
	}

	public Level getLevel() {
		return this.level;
	}

	/**
	 * Loads the level synchronously.
	 * The level loaded is the level with
	 * level number set by {@link #setLevelNumber(int)}
	 * <br/>
	 * Use {@link #getLevel()} to retrieve the level when AssetManager has finished
	 * 
	 * TODO Why do we need a listener if this call is synchronous?
	 */
	public void loadLevel(OnLevelLoadedListener listener) {
		//		getManager().load(Level.getLocalPath(this.levelNumber), Level.class);
		loadLevelSync(listener);
	}

	/**
	 * Unloads level from AssetManager. The level unloaded is the level
	 * with level number set by {@link #setLevelNumber(int)}
	 */
	public void disposeLevel() {
		AssetManager manager = getManager();
		String filename = manager.getAssetFileName(getLevel());
		if( filename != null ) {
			getManager().unload(filename);
		}
	}

	public void setWorld(World world) {
		this.world = world;
		this.world.setContactListener(this);
	}

	public World getWorld() {
		return world;
	}

	/**
	 * Use this to add User interface elements that do not require collision detection nor physics
	 * Example: score bar, buttons, background images/animations
	 * @param actor
	 */
	public void addUIElement(Actor actor) {
		AbstractScreen screen = (AbstractScreen) getScreen();
		screen.getStageUIActors().addActor(actor);
	}

	public AssetManager getManager() {
		return manager;
	}

	/**
	 * Queues a game object for removal. Note that this happens asynchronously.
	 * @param object the GameObject to be removed
	 */
	public void deleteGameObject(GameObject object) {
		this.gameObjectsForDeletion.add(object);
		this.worldThread.deleteGameObject(object);
	}

	/**
	 * Returns the game objects that have been added using {@link #deleteGameObject(GameObject)}
	 * to be deleted
	 * @return ArrayList<GameObject>
	 */
	public Array<GameObject> getGameObjectsForDeletion() {
		return gameObjectsForDeletion;
	}

	public void addGameObject(GameObject object) {
		object.setGame(this);

		if( this.gameState == GAME_STATE.RUNNING ) {
			this.gameObjectsForAddition.add(object);
			this.worldThread.addGameObject(object);
		} else {
			object.loadSounds();
			object.setupImage();
			object.setupBody();
		}

		synchronized (this.gameObjectsInGame) {
			this.gameObjectsInGame.add(object);
		}

		this.stageActors.addActor(object);
	}

	public Array<GameObject> getGameObjectsForAddition() {
		return gameObjectsForAddition;
	}

	public Array<GameObject> getGameObjectsInGame() {
		return gameObjectsInGame;
	}

	/**
	 * Called by the World thread prior to calling {@link World#step(float, int, int)}
	 */
	public void updateWorld() {

	}

	/**
	 * Called in game during a render cycle
	 * @param delta time in seconds since last cycle
	 * @param stage stage that holds the game actors
	 */
	public void updateScreen(float delta, Stage stage) {
		fpsLogger.log();
		if( this.gameState == GAME_STATE.RUNNING ) {
			//			fixedTimeStep(delta, stage);
			fixedTimeStepInterpolated(delta, stage);
			
			handleDeleteGameObjectsQueue();

			handleAddGameObjectsQueue();

			if( this.levelState == LEVEL_STATE.COMPLETE ) {
				pauseGame();
				showLevelCompleteDialog();
			} else if( this.levelState == LEVEL_STATE.FAILED ) {
				pauseGame();
				showLevelFailedDialog();
			}
		}
	}

	/**
	 * Called when a key was pressed
	 * @param keycode one of the constants in Input.Keys
	 * @return whether the key was processed
	 */
	public boolean handleKeyEvent(int keycode) {
		if((keycode == Keys.BACK) 
				|| (keycode == Keys.ESCAPE)) {
			if( this.gameState == GAME_STATE.RUNNING ) {
				pauseGame();
			} else {
				stopScreen();
			}
			return true;
		}
		return false;
	}

	/**
	 * Resets the game by reloading the level
	 */
	public void reset() {
		setScreen( new LevelScreen(this) );
	}

	public void showMainMenu() {
		Screen s = new MainMenuScreen(this);
		setScreen( s );
		addToBackstack(s);
	}

	public void showSplashScreen() {
		setScreen(new SplashScreen(this));
	}

	public void startLevel(int level) {
		setLevelNumber(level);
		this.levelState = LEVEL_STATE.NONE;
		showLevelScreen();
	}

	public void startLevel(Level level) {
		setLevel(level);
		this.levelState = LEVEL_STATE.NONE;
		showLevelScreen();
	}

	private void loadLevelSync(final OnLevelLoadedListener listener) {
		setLevel(LevelLoader.loadLocalSync(this.levelNumber));
		if( listener != null ) {
			listener.onLevelLoaded(getLevel());
		}
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

	/**
	 * Hides the current screen and shows the previous screen
	 * TODO implement disposing screens when popped from backstack. This must be done after hide animation has finished
	 */
	public void stopScreen() {
		popBackstack();
		setScreen(peekBackStack());
	}

	@Override
	public void onMusicFilesReceived() {
		MusicPlayer player = MusicPlayer.getInstance();
		player.setLibrary(this.musicSelector.getLibrary());
	}

	/**
	 * Called by AbstractScreen when level is loaded and ready to start the game
	 * @param screen that contains the stage with actors
	 * @return true if setup was successful, false otherwise
	 */
	public boolean setup(AbstractScreen screen) {
		this.stageActors = screen.getStageActors();
		return true;
	}

	/**
	 * Override this method to implement a dialog that should be shown
	 * when game is in state {@link #GAME_STATE_COMPLETE}
	 */
	public void showLevelCompleteDialog() {
		Stage stage = ((AbstractScreen) getScreen()).getStageUIActors();

		LevelCompleteDialog levelCompleteDialog = new LevelCompleteDialog(stage, this, ((AbstractScreen) getScreen()).getSkin(), getTotalScore());

		levelCompleteDialog.setOnClickListener(this);

		levelCompleteDialog.create();

		levelCompleteDialog.show();

		setTotalScore(getTotalScore());
	}

	/**
	 * Override this method to implement a custom dialog that should be shown
	 * when game is in state {@link #GAME_STATE_FAILED}
	 */
	public void showLevelFailedDialog() {
		AbstractScreen screen = (AbstractScreen) getScreen();

		LevelFailedDialog dialog = new LevelFailedDialog(screen.getStageUIActors(), screen.getSkin());
		dialog.setOnClickListener(this);
		dialog.create();
		dialog.show();
	}

	private void handleDeleteGameObjectsQueue() {
		Array<GameObject> notDeletedGameObjects = new Array<GameObject>();
		for (GameObject object : this.gameObjectsForDeletion ) {
			if( object.canBeRemoved() ) {
				if( object.remove() ) {
					object.clear();
				}

				synchronized (this.gameObjectsInGame) {
					this.gameObjectsInGame.removeValue(object, true);
				}
			} else {
				notDeletedGameObjects.add(object);
			}
		}
		this.gameObjectsForDeletion = notDeletedGameObjects;
	}
	
	private void handleAddGameObjectsQueue() {
		for(GameObject object : this.gameObjectsForAddition) {
			object.setupImage();
		}
		this.gameObjectsForAddition.clear();
	}
	
	private void fixedTimeStep(float delta, Stage stage) {
		this.world.step(BOX2D_UPDATE_FREQUENCY, 6, 2);
		Array<Actor> actors = stage.getActors();
		int size = actors.size;

		for(int i = 0; i < size; i++) {
			GameObject gameObject = (GameObject) actors.get(i);
			Body body = gameObject.getBody();
			if (body != null) {
				if ( body.getType() == BodyDef.BodyType.DynamicBody ) {

					Vector2 currentPosition = body.getPosition();

					gameObject.setX( currentPosition.x );
					gameObject.setY( currentPosition.y );

					gameObject.setRotation( MathUtils.radiansToDegrees * body.getAngle() );
				}
			}
		}
	}

	private void fixedTimeStepInterpolated(float delta, Stage stage) {
		//		Gdx.app.log("Game", "fixedTimeStepInterpolated: delta="+delta);

		//		if( delta > 0.25f ) { //upper bound on framerate to prevent spiral of death
		//			delta = 0.25f;
		//		}
		//		
		//		accumulator += delta;
		//
		//		while (accumulator >= BOX2D_UPDATE_FREQUENCY) {
		//			this.world.step(BOX2D_UPDATE_FREQUENCY, BOX2D_VELOCITY_ITERATIONS, BOX2D_POSITION_ITERATIONS);
		//			accumulator -= BOX2D_UPDATE_FREQUENCY;
		//		}

		//		interpolateGameObjectsCurrentPosition(accumulator/BOX2D_UPDATE_FREQUENCY, stage);
		if( delta > BOX2D_UPDATE_FREQUENCY ) {
			//Rendering running slower than world updates
			//Should we also use Toast to notify user?
			Gdx.app.log("Game", "Renderer took "+delta+" secondes while world is updated each "+BOX2D_UPDATE_FREQUENCY+" seconds");
		}
		interpolateGameObjectsCurrentPosition(delta/BOX2D_UPDATE_FREQUENCY, stage);
	}

	private void interpolateGameObjectsCurrentPosition(float alpha, Stage stage) {
		Array<Actor> actors = stage.getActors();
		int size = actors.size;

		for(int i = 0; i < size; i++) {
			GameObject gameObject = (GameObject) actors.get(i);
			Body body = gameObject.getBody();
			if (body != null) {
				if ( body.getType() == BodyDef.BodyType.DynamicBody ) {

					Vector2 currentPosition = body.getPosition();

					//---- interpolate: currentState*alpha + previousState * ( 1.0 - alpha )
					gameObject.setX( currentPosition.x * alpha + gameObject.getX() * (1.0f - alpha) );
					gameObject.setY( currentPosition.y * alpha + gameObject.getY() * (1.0f - alpha) );

					gameObject.setRotation( (MathUtils.radiansToDegrees * body.getAngle()) * alpha + gameObject.getRotation() * (1.0f - alpha) );
				}
			}
		}
	}

	private void showLevelScreen() {
		LevelScreen screen = new LevelScreen(this);
		setScreen( screen );

		//Make sure LevelScreen is only added once to the backstack to prevent
		//going back to a previous level if user quits level
		if( ! ( peekBackStack() instanceof LevelScreen ) ) {
			addToBackstack(screen);
		}
	}

	private void registerTweens() {
		Tween.registerAccessor(Actor.class, new ActorAccessor());
	}

	private void startBox2DThread() {
		if( this.worldThread != null ) {
			this.worldThread.stopThread();
		}
		this.worldThread = new WorldThread(this, BOX2D_UPDATE_FREQUENCY, BOX2D_VELOCITY_ITERATIONS, BOX2D_POSITION_ITERATIONS);
		this.worldThread.start();
	}

	/**
	 * This should return one game object for each type used in the game.
	 * @return
	 */
	abstract public ArrayList<GameObject> getAvailableGameObjects();
}
