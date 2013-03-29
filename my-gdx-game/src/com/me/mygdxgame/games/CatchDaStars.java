package com.me.mygdxgame.games;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.me.mygdxgame.screens.MenuScreen;
import com.me.mygdxgame.screens.SplashScreen;

public class CatchDaStars extends Game {

	public SplashScreen getSplashScreen()
    {
        return new SplashScreen( this );
    }
	
	public MenuScreen getMenuScreen() {
		 return new MenuScreen( this );
	}
	
	public void create() {
		setScreen( getSplashScreen() );
	}
	
	public void setScreen(
	        Screen screen )
	    {
	        super.setScreen( screen );
	        Gdx.app.log( "CatchDaStars", "Setting screen: " + screen.getClass().getSimpleName() );
	    }
}
