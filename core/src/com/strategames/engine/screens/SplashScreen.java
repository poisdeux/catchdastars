package com.strategames.engine.screens;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.io.FileNotFoundException;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.engine.game.Game;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Textures;

public class SplashScreen extends AbstractScreen {

	public SplashScreen(Game game) {
		super(game);
		MusicPlayer player = MusicPlayer.getInstance();
		player.setLibrary(getGame().getMusicSelector().getLibrary());
	}

	private boolean finishedSetupAssets = false;
	private Image splashImage;
	
	@Override
	protected void setupActors(Stage stage) {

	}

	@Override
	protected void setupUI(Stage stage) {
		Texture texture = Textures.getInstance().getSplashScreen();

		this.splashImage = new Image(texture);

		this.splashImage.setPosition(0, 
				Gdx.graphics.getHeight()/2 - this.splashImage.getHeight()/2);

		this.splashImage.addAction( fadeIn( 0.75f ) );

		this.splashImage.getColor().a = 0f;

		try {
			Textures.getInstance().addAllToAssetManager(getGame().getManager());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Sounds.getInstance().addToAssetManager(assetManager);
		
		stage.addActor( this.splashImage );
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if ( getGame().getManager().update() && ( ! this.finishedSetupAssets ) ) {
			try {
				Textures.getInstance().setup();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.finishedSetupAssets = true;
			
			this.splashImage.addAction( sequence(
					delay( 0.75f ),
					fadeOut( 0.75f ),
					new Action() {

						@Override
						public boolean act(float delta) {
							getGame().showMainMenu();
							return true;
						}
					}));
		} 
	}


//	@Override
//	public void dispose()
//	{
//		super.dispose();
//		this.splashImage.remove();
//	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected Timeline createShowAnimation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Timeline createHideAnimation() {
		// TODO Auto-generated method stub
		return null;
	}
}