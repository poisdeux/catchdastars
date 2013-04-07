package com.strategames.catchdastars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.screens.LevelScreen;
import com.strategames.catchdastars.utils.Textures;

abstract public class Game extends com.badlogic.gdx.Game {
	public Game() {
//		this.stage = new Stage(0, 0, true);
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
			setScreen( new LevelScreen(this));
//			setScreen(new LevelEditorMenuScreen(this));
		}
	}
	
	@Override
	public void create() {
		Gdx.app.log( "Game", "create() called" );
		Textures.load();
	}
	
	abstract public void setupStage(Stage stage);
	
	abstract public void update(float delta);
	
}
