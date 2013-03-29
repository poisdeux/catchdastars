package com.me.mygdxgame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.me.mygdxgame.gameobjects.SplashImage;

public class SplashScreen
extends
    AbstractScreen
{
private Texture splashTexture;

public SplashScreen(
    Game game )
{
    super( game );
}

@Override
public void show()
{
    super.show();

    // load the texture with the splash image
    splashTexture = new Texture( "splash.png" );

    // set the linear texture filter to improve the image stretching
    splashTexture.setFilter( TextureFilter.Linear, TextureFilter.Linear );
}

@Override
public void resize(
    int width,
    int height )
{
    super.resize( width, height );

    stage.clear();
    SplashImage splashImage = new SplashImage(); 
 
    // configure the fade-in/out effect on the splash image
    AlphaAction fadein = Actions.fadeIn(0.75f);
    DelayAction delay = Actions.delay(0.75f);
    AlphaAction fadeout = Actions.fadeOut(0.75f);
    
    SequenceAction sequence = new SequenceAction();
    sequence.addAction(fadein);
    sequence.addAction(delay);
    sequence.addAction(fadeout);
    
    splashImage.addAction(sequence);
    splashImage.addListener(new EventListener() {
		
		@Override
		public boolean handle(Event event) {
			game.setScreen( new MenuScreen(game) );
			return true;
		}
	});    		

    stage.addActor( splashImage );
}

@Override
public void dispose()
{
    super.dispose();
    splashTexture.dispose();
}
}