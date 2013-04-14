package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.screens.LevelEditorMenuScreen;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.Textures;

abstract public class Game extends com.badlogic.gdx.Game {
	private ArrayList<String> levelNames;
	private Level currentLevel;
	private ArrayList<GameObject> availableGameObjects;
	
	public Game() {
		this.levelNames = new ArrayList<String>();
	}
	
	@Override
	public void render() {		
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize( width, height );
		Gdx.app.log( "Game", "Resizing game to: " + width + " x " + height );

		// show the splash screen when the game is resized for the first time;
		// this approach avoids calling the screen's resize method repeatedly
		if( getScreen() == null ) {
//			setScreen( new SplashScreen( this ) );
//			setScreen( new LevelScreen(this));
			setScreen(new LevelEditorMenuScreen(this));
		}
		
		this.availableGameObjects = availableGameObjects();
	}
	
	@Override
	public void create() {
		Gdx.app.log( "Game", "create() called" );
		Textures.load();
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
	
	/**
	 * Returns a single game object for each type used in this game
	 * @return
	 */
	public ArrayList<GameObject> getAvailableGameObjects() {
		return this.availableGameObjects;
	}
	
	/**
	 * This should return one game object for each type used in the game.
	 * @return
	 */
	abstract public ArrayList<GameObject> availableGameObjects();
	
	abstract public void setupStage(Stage stage);
	
	abstract public void update(float delta);

	abstract public void addGameObject(GameObject object);
}
