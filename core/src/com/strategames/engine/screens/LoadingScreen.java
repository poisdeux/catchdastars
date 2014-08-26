package com.strategames.engine.screens;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.engine.game.Game;
import com.strategames.engine.utils.Textures;

public class LoadingScreen extends AbstractScreen {

	private Image loadingImage;
	private Image dotImage1;
	private Image dotImage2;
	private Image dotImage3;
	
	private Stage stageUIActors;
	
	/**
	 * Shows an animation which can be displayed during game loading
	 */
	public LoadingScreen(Game game) {
		super(game, "Loading...");
	}

	@Override
	protected void setupActors(Stage stage) {

	}

	@Override
	protected void setupUI(Stage stage) {
		this.stageUIActors = stage;
		
		Textures textures = Textures.getInstance();
		this.loadingImage = new Image(textures.Loading);
		this.dotImage1 = new Image(textures.dot);
		this.dotImage2 = new Image(textures.dot);
		this.dotImage3 = new Image(textures.dot);
		
		float x = (stage.getWidth() - (this.loadingImage.getWidth() + ( 3 * this.dotImage1.getWidth() ) ) ) / 2f;
		float y = (stage.getHeight() - this.loadingImage.getHeight()) / 2f;
		
		this.loadingImage.setX(x);
		this.loadingImage.setY(y);
		
		float padding = 10f;
		
		x += this.loadingImage.getWidth() + padding;
		this.dotImage1.setX(x);
		this.dotImage1.setY(y);
		
		x += this.dotImage1.getWidth() + padding;
		this.dotImage2.setX(x);
		this.dotImage2.setY(y);
		
		x += this.dotImage1.getWidth() + padding;
		this.dotImage3.setX(x);
		this.dotImage3.setY(y);		
		
		setupDotsAnimation();
		
		stage.addActor(this.loadingImage);
		stage.addActor(this.dotImage1);
		stage.addActor(this.dotImage2);
		stage.addActor(this.dotImage3);
	}

	@Override
	public void render(float delta) {
		this.stageUIActors.act();
		super.render(delta);
	}


	@Override
	public void dispose()
	{
		super.dispose();
		this.loadingImage.remove();
		this.dotImage1.remove();
		this.dotImage2.remove();
		this.dotImage3.remove();
	}
	
	private void setupDotsAnimation() {
		this.dotImage1.getColor().a = 0f;
		this.dotImage1.addAction( sequence(
				fadeIn( 0.5f )));
		
		this.dotImage2.getColor().a = 0f;
		this.dotImage2.addAction( sequence(
				delay( 0.5f ),
				fadeIn( 0.5f )));
		
		this.dotImage3.getColor().a = 0f;
		this.dotImage3.addAction( sequence(
				delay( 1f ),
				fadeIn( 0.5f ),
				new Action() {

					@Override
					public boolean act(float arg0) {
						setupDotsAnimation(); // loop indefinitely
						return true;
					}
					
				}));
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