package com.strategames.catchdastars;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.strategames.catchdastars.screens.LevelScreen;
import com.strategames.catchdastars.screens.SplashScreen;
import com.strategames.catchdastars.utils.Textures;

public class CatchDaStarsGame extends Game implements ApplicationListener {

	@Override
	public void render() {		
		super.render();

	}

	@Override
	public void resize(int width, int height) {
		super.resize( width, height );
		Gdx.app.log( "CatchDaStarsGame", "Resizing game to: " + width + " x " + height );

		// show the splash screen when the game is resized for the first time;
		// this approach avoids calling the screen's resize method repeatedly
		if( getScreen() == null ) {
//			setScreen( new SplashScreen( this ) );
			setScreen( new LevelScreen(this, 1));
		}
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void create() {
		Gdx.app.log( "CatchDaStarsGame", "create() called" );
		Textures.load();
	}
}
