package com.strategames.catchdastars.screens.game;


import java.io.FileNotFoundException;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.Texture;
import com.strategames.engine.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.tweens.ActorAccessor;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Textures;

public class SplashScreen extends AbstractScreen {

	private boolean finishedSetupAssets = false;
	private boolean finishedStartupAnimation = false;
	
	private Image splashImage;
	
	public SplashScreen(GameEngine game) {
		super(game, null);
		MusicPlayer player = MusicPlayer.getInstance();
		player.setLibrary(getGameEngine().getMusicSelector().getLibrary());
	}

	@Override
	protected void setupActors(Stage stage) {

	}

	@Override
	protected void setupUI(Stage stage) {
		Texture texture = Textures.getInstance().getSplashScreen();

		this.splashImage = new Image(texture);

		float stageWidth = stage.getWidth();
		float stageHeight = stage.getHeight();
		
		float ratio = stageWidth / this.splashImage.getWidth();
		this.splashImage.setSize(this.splashImage.getWidth() * ratio, this.splashImage.getHeight() * ratio);
		
		this.splashImage.setPosition(stageWidth/2f - this.splashImage.getWidth()/2f, 
				stageHeight/2f - this.splashImage.getHeight()/2f);

		try {
			Textures.getInstance().addAllToAssetManager(getGameEngine().getManager());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		stage.addActor( this.splashImage );
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if ( getGameEngine().getManager().update() && ( ! this.finishedSetupAssets ) ) {
			try {
				Textures.getInstance().setup();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.finishedSetupAssets = true;
			
			if( this.finishedStartupAnimation ) {
				getGameEngine().showMainMenu();
			}
		} 
	}
	
	@Override
	protected Timeline showAnimation() {
		this.splashImage.setOrigin(this.splashImage.getWidth() / 2f, this.splashImage.getHeight() / 2f);
		this.splashImage.setScale(0f);
		
		Timeline timeline = Timeline.createParallel();
		timeline.push(Tween.to(this.splashImage, ActorAccessor.SCALE, 1.5f).target(1f, 1f));
		timeline.setCallbackTriggers(TweenCallback.COMPLETE);
		timeline.setCallback(new TweenCallback() {
			
			@Override
			public void onEvent(int arg0, BaseTween<?> arg1) {
				if( arg0 == TweenCallback.COMPLETE ) {
					finishedStartupAnimation = true;
					
					if( finishedSetupAssets ) {
						getGameEngine().showMainMenu();
					}
				}
			}
		});
		return timeline;
	}

	@Override
	protected Timeline hideAnimation() {
		Timeline timeline = Timeline.createSequence();
		timeline.push(Tween.to(this.splashImage, ActorAccessor.ALPHA, 1f).target(0f));
		return timeline;
	}
}