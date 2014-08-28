package com.strategames.catchdastars.screens;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.io.FileNotFoundException;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.engine.game.Game;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.tweens.ActorAccessor;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Textures;

public class SplashScreen extends AbstractScreen {

	private boolean finishedSetupAssets = false;
	private boolean finishedStartupAnimation = false;
	
	private Image splashImage;
	
	public SplashScreen(Game game) {
		super(game, null);
		MusicPlayer player = MusicPlayer.getInstance();
		player.setLibrary(getGame().getMusicSelector().getLibrary());
	}

	@Override
	protected void setupActors(Stage stage) {

	}

	@Override
	protected void setupUI(Stage stage) {
		Texture texture = Textures.getInstance().getSplashScreen();

		this.splashImage = new Image(texture);

		this.splashImage.setPosition(stage.getWidth()/2f - this.splashImage.getWidth()/2f, 
				stage.getHeight()/2f - this.splashImage.getHeight()/2f);

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
			
			if( this.finishedStartupAnimation ) {
				getGame().showMainMenu();
			}
		} 
	}


	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected Timeline createShowAnimation() {
		this.splashImage.setOrigin(this.splashImage.getWidth() / 2f, this.splashImage.getHeight() / 2f);
		this.splashImage.setScale(0f);
		
		Timeline timeline = Timeline.createParallel();
//		timeline.push(Tween.to(this.splashImage, ActorAccessor.ROTATE, 1.5f).target(3600));
		timeline.push(Tween.to(this.splashImage, ActorAccessor.SCALE, 1.5f).target(1f, 1f));
		timeline.setCallbackTriggers(TweenCallback.COMPLETE);
		timeline.setCallback(new TweenCallback() {
			
			@Override
			public void onEvent(int arg0, BaseTween<?> arg1) {
				if( arg0 == TweenCallback.COMPLETE ) {
					finishedStartupAnimation = true;
					
					if( finishedSetupAssets ) {
						getGame().showMainMenu();
					}
				}
			}
		});
		return timeline;
	}

	@Override
	protected Timeline createHideAnimation() {
		Timeline timeline = Timeline.createSequence();
		timeline.push(Tween.to(this.splashImage, ActorAccessor.ALPHA, 1f).target(0f));
		return timeline;
	}
}