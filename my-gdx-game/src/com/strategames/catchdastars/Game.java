package com.strategames.catchdastars;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.screens.LevelEditorMenuScreen;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.Textures;

abstract public class Game extends com.badlogic.gdx.Game {
	private ArrayList<String> levelNames;
	private Level currentLevel;
	
	public Game() {
		this.levelNames = new ArrayList<String>();
		
//		FileHandle dir = Level.getInternalLevelsDir();
//		FileHandle[] files = dir.list();
//		for(FileHandle file : files) {
//			Level level = Level.loadInternal(Integer.parseInt(file.name()));
//			if( level != null ) {
//				this.levelNames.add(level.getName());
//			}
//		}
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
	
	abstract public void setupStage(Stage stage);
	
	abstract public void update(float delta);
	
}
