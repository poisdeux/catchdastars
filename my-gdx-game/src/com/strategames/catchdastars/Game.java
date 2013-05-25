package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.screens.AbstractScreen;
import com.strategames.catchdastars.screens.SplashScreen;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

abstract public class Game extends com.badlogic.gdx.Game implements ContactListener {
	public static float UPDATE_FREQUENCY_SECONDS = 1f/45f;
	public static float UPDATE_FREQUENCY_MILLISECONDS = UPDATE_FREQUENCY_SECONDS * 1000f;

	private AssetManager manager;

	private ArrayList<String> levelNames;
	private Level currentLevel;
	private World world;

	public Game() {
		this.levelNames = new ArrayList<String>();
		this.manager = new AssetManager();
	}

	@Override
	public void create() {
		setScreen(new SplashScreen(this));
	}

	@Override
	public void resume() {
		super.resume();
		if( Gdx.app.getType() == ApplicationType.Android ) {
			AssetManager manager = getManager();
			Sounds.load(manager);
			Textures.load(manager);
			manager.finishLoading();
			Sounds.setup(manager);
			Textures.setup(manager);
		}
	}

	@Override
	public void pause() {
		super.pause();
		if( Gdx.app.getType() == ApplicationType.Android ) {
			AssetManager manager = getManager();
			Sounds.dispose(manager);
			Textures.dispose(manager);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		Sounds.dispose(getManager());
		Textures.dispose(getManager());
	}

	public int getAmountOfLevels() {
		return this.levelNames.size();
	}

	public void setCurrentLevel(Level level) {
		this.currentLevel = level;
	}

	public Level getCurrentLevel() {
		return this.currentLevel;
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
