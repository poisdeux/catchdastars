package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.screens.AbstractScreen;
import com.strategames.catchdastars.screens.SplashScreen;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.Textures;

abstract public class Game extends com.badlogic.gdx.Game {
	private ArrayList<String> levelNames;
	private Level currentLevel;
	private ArrayList<GameObject> availableGameObjects;
	private World world;
	
	public Game() {
		this.levelNames = new ArrayList<String>();
	}
	
	@Override
	public void create() {
		Textures.load();
		setScreen(new SplashScreen(this));
		this.availableGameObjects = availableGameObjects();
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
	}
	
	public World getWorld() {
		return world;
	}
	
	/**
	 * Returns a single game object for each type used in this game
	 * @return
	 */
	public ArrayList<GameObject> getAvailableGameObjects() {
		return this.availableGameObjects;
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
	
//	public ArrayList<GameObject> getGameObjects() {
//		return this.stageActors;
//	}
	
	/**
	 * This should return one game object for each type used in the game.
	 * @return
	 */
	abstract public ArrayList<GameObject> availableGameObjects();
	
	abstract public void setupStage(Stage stage);
	
	abstract public void update(float delta);
}
