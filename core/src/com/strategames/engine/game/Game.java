package com.strategames.engine.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.interfaces.ExportImport;
import com.strategames.engine.interfaces.MusicSelector;
import com.strategames.engine.interfaces.OnMusicFilesReceivedListener;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.screens.LevelEditorMenuScreen;
import com.strategames.engine.screens.LevelEditorScreen;
import com.strategames.engine.screens.LevelScreen;
import com.strategames.engine.screens.MainMenuScreen;
import com.strategames.engine.screens.SettingsScreen;
import com.strategames.engine.screens.SplashScreen;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelLoader;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Sounds;
import com.strategames.engine.utils.Textures;
import com.strategames.engine.utils.LevelLoader.OnLevelLoadedListener;

abstract public class Game extends com.badlogic.gdx.Game implements ContactListener, OnMusicFilesReceivedListener {
	public final int GAME_STATE_RUNNING = 0;
	public final int GAME_STATE_PAUSED = 1;
	private int gameState = GAME_STATE_PAUSED;

	public static final float UPDATE_FREQUENCY_SECONDS = 1f/45f;
	public static final float UPDATE_FREQUENCY_MILLISECONDS = UPDATE_FREQUENCY_SECONDS * 1000f;

	public static final float BOX_TO_WORLD = 100f;
	public static final float WORLD_TO_BOX = 1/BOX_TO_WORLD;

	public static final float GRAVITY = 9.81f;

	private ArrayList<GameObject> gameObjectsForDeletion;

	private AssetManager manager;

	private int levelNumber;

	private Level level; 

	private World world;

	private Vector3 worldSize;

	private ExportImport exportimport;
	private MusicSelector musicSelector;
	
	private String title;

	private Stack<Screen> backStack;

	private int totalScore;
	
	public Game() {
		this.title = "No name game";
		this.manager = new AssetManager();
		this.gameObjectsForDeletion = new ArrayList<GameObject>();
		this.backStack = new Stack<Screen>();
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
	public void resume() {
		super.resume();
		//		if( Gdx.app.getType() == ApplicationType.Android ) {
		//			AssetManager manager = getManager();
		//			Sounds.load(manager);
		//			Textures.load(manager);
		//			manager.finishLoading();
		//			Sounds.setup(manager);
		//			Textures.setup(manager);
		//		}
	}

	@Override
	public void pause() {
		super.pause();
		//		if( Gdx.app.getType() == ApplicationType.Android ) {
		//			AssetManager manager = getManager();
		//			Sounds.dispose(manager);
		//			Textures.dispose(manager);
		//		}
	}

	public void pauseGame() {
		this.gameState = GAME_STATE_PAUSED;
		MusicPlayer.getInstance().pause();
	}

	public void resumeGame() {
		this.gameState = GAME_STATE_RUNNING;
		MusicPlayer.getInstance().resume();
	}

	public void startGame() {
		this.gameState = GAME_STATE_RUNNING;
		MusicPlayer.getInstance().resume();
	}

	public boolean isRunning() {
		return this.gameState == GAME_STATE_RUNNING;
	}

	public boolean isPaused() {
		return this.gameState == GAME_STATE_PAUSED;
	}

	public int getGameState() {
		return this.gameState;
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

	@Override
	public void dispose() {
		super.dispose();
		Sounds.getInstance().dispose(getManager());
		Textures.getInstance().dispose(getManager());
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

	public Screen peepBackstack() {
		return this.backStack.peek();
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

	/**
	 * Tries to get level from AssetManager. If AssetManager has no level available
	 * the level set through {@link #setLevel(Level)} is returned.
	 * @return Level or null if not available or set.
	 */
	public Level getLevel() {
		//		Level level;
		//		try {
		//			level = getManager().get(Level.getLocalPath(this.levelNumber), Level.class);
		//		} catch( Exception e ) {
		//			level = null;
		//		}
		//		
		//		if( level == null ) {
		//			return this.level;
		//		} else {
		//			return level;
		//		}
		return this.level;
	}

	/**
	 * Loads the level synchronously.
	 * The level loaded is the level with
	 * level number set by {@link #setLevelNumber(int)}
	 * <br/>
	 * Use {@link #getLevel()} to retrieve the level when AssetManager has finished
	 */
	public void loadLevel(OnLevelLoadedListener listener) {
		//		getManager().load(Level.getLocalPath(this.levelNumber), Level.class);
		loadLevelSync(listener);
	}

	private void loadLevelAsync(final OnLevelLoadedListener listener) {
		LevelLoader.loadLocalAsync(getLevelNumber(), new OnLevelLoadedListener() {

			@Override
			public void onLevelLoaded(Level level) {
				setLevel(level);
				if( listener != null ) {
					listener.onLevelLoaded(level);
				}
			}
		});
	}

	private void loadLevelSync(final OnLevelLoadedListener listener) {
		setLevel(LevelLoader.loadLocalSync(this.levelNumber));
		if( listener != null ) {
			listener.onLevelLoaded(getLevel());
		}
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

//	public ArrayList<GameObject> getGameObjects() {
//		if( this.level == null )
//			return null;
//		
//		return this.level.getGameObjects();
//	}

	/**
	 * Use this to add User interface elements that do not require collision detection nor physics
	 * Example: score bar, buttons, background images/animations
	 * @param actor
	 */
	public void addUIElement(Actor actor) {
		AbstractScreen screen = (AbstractScreen) getScreen();
		screen.getStageUIElements().addActor(actor);
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
	}

	public void update(float delta, Stage stage) {
		if( this.gameState == GAME_STATE_RUNNING ) {
			this.world.step(UPDATE_FREQUENCY_SECONDS, 6, 2);
		}

		if( ! this.world.isLocked() ) {
			for (GameObject object : this.gameObjectsForDeletion ) {
				object.remove();
				object.deleteBody();
			}
			this.gameObjectsForDeletion.clear();
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
			if( this.gameState == GAME_STATE_RUNNING ) {
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
		Screen screen = new MainMenuScreen(this);
		setScreen( screen );
		addToBackstack(screen);
	}

	public void showSplashScreen() {
		setScreen(new SplashScreen(this));
	}

	public void startLevel(int level) {
		setLevelNumber(level);
		showLevelScreen();
	}

	public void startLevel(Level level) {
		setLevel(level);
		showLevelScreen();
	}

	private void showLevelScreen() {
		LevelScreen screen = new LevelScreen(this);
		setScreen( screen );

		//Make sure LevelScreen is only added once to the backstack to prevent
		//going back to a previous level if user quits level
		if( ! ( peepBackstack() instanceof LevelScreen ) ) {
			addToBackstack(screen);
		}
	}

	/**
	 * Starts the LevelEditor screen
	 * TODO remove level argument if it is not really used as 
	 * level is already available in the game class
	 * @param level
	 */
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
	 */
	public void stopScreen() {
		popBackstack();
		setScreen(peepBackstack());
	}

	@Override
	public void onMusicFilesReceived() {
		MusicPlayer player = MusicPlayer.getInstance();
		player.setLibrary(this.musicSelector.getLibrary());
	}
	
	/**
	 * This should return one game object for each type used in the game.
	 * @return
	 */
	abstract public ArrayList<GameObject> getAvailableGameObjects();

	abstract public void initialize();
}
