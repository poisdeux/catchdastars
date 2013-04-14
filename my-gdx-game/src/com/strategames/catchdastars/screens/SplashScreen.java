package com.strategames.catchdastars.screens;


import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.SplashImage;

public class SplashScreen extends AbstractScreen {
	
	public SplashScreen(Game game) {
		super(game);
	}

	private SplashImage splashImage;

	@Override
	public void show( )
	{
		
		this.splashImage = new SplashImage(); 

		// configure the fade-in/out effect on the splash image
		AlphaAction fadein = Actions.fadeIn(0.75f);
		DelayAction delay = Actions.delay(0.75f);
		AlphaAction fadeout = Actions.fadeOut(0.75f);
		
		SequenceAction sequence = new SequenceAction();
		sequence.addAction(fadein);
		sequence.addAction(delay);
		sequence.addAction(fadeout);

		sequence.addAction(new Action() {
			
			@Override
			public boolean act(float delta) {
				Game game = getGame();
				game.setScreen(new MenuScreen(game));
				return true;
			}
		});
		this.splashImage.addAction(sequence);

		getGame().addUIElement( this.splashImage );
	}

	@Override
	public void dispose()
	{
		super.dispose();
		this.splashImage.remove();
	}
}