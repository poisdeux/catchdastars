package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.screens.AbstractScreen;
import com.strategames.catchdastars.screens.SplashScreen;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.LevelLoader;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

abstract public class Game extends com.badlogic.gdx.Game implements ContactListener {
	public static float UPDATE_FREQUENCY_SECONDS = 1f/45f;
	public static float UPDATE_FREQUENCY_MILLISECONDS = UPDATE_FREQUENCY_SECONDS * 1000f;

	public static final float BOX_TO_WORLD = 50f;
	public static final float WORLD_TO_BOX = 1/BOX_TO_WORLD;
	
	
	private AssetManager manager;

	private ArrayList<String> levelNames;
	private int levelNumber;
	
	private Level level; 
	
	private World world;

	public Game() {
		this.levelNames = new ArrayList<String>();
		this.manager = new AssetManager();
		
		this.manager.setLoader(Level.class, new LevelLoader(new InternalFileHandleResolver()));
	}

	@Override
	public void create() {
		setScreen(new SplashScreen(this));
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

	@Override
	public void dispose() {
		super.dispose();
		Sounds.dispose(getManager());
		Textures.dispose(getManager());
	}

	static public float convertWorldToBox(float x) {
		return x * WORLD_TO_BOX;
	}
	
	static public float convertBoxToWorld(float x) {
		return x * BOX_TO_WORLD;
	}
	
	public int getAmountOfLevels() {
		return this.levelNames.size();
	}

	public int getLevelNumber() {
		return levelNumber;
	}
	
	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}

	public void setLevel(Level level) {
		this.level = level;
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
	 * Loads level using the AssetManager. The level loaded is the level with
	 * level number set by {@link #setLevelNumber(int)}
	 * <br/>
	 * Use {@link #getLevel()} to retrieve the level when AssetManager has finished
	 */
	public void loadLevel() {
//		getManager().load(Level.getLocalPath(this.levelNumber), Level.class);
		this.level = Level.loadLocal(this.levelNumber);
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
	
	
	public ArrayList<String> getLevelNames() {
		return this.levelNames;
	}

	public void addLevel(String name) {
		this.levelNames.add(name);
	}

	public void setWorld(World world) {
		this.world = world;
		this.world.setContactListener(this);
	}

	public World getWorld() {
		return world;
	}

	/**
	 * Use this to add game objects that require collision detection and physics to calculate the objects new position.
	 * @param object the actual game object
	 */
	public void addGameObject(GameObject object) {
		object.setWorld(getWorld());
		object.setup();
		AbstractScreen screen = (AbstractScreen) getScreen();
		screen.getStageActors().addActor(object);
	}

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
	 * This should return one game object for each type used in the game.
	 * @return
	 */
	abstract public ArrayList<GameObject> getAvailableGameObjects();

	abstract public void setupStage(Stage stage);

	abstract public void reset();

	abstract public void update(float delta, Stage stage);
}
