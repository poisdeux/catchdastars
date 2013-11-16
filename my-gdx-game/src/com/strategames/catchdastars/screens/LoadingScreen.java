package com.strategames.catchdastars.screens;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.Textures;

public class LoadingScreen extends AbstractScreen {

	private Screen screenToLoad;
	
	private boolean animationFinished = false;
	
	private Image loadingImage;
	private Image dotImage1;
	private Image dotImage2;
	private Image dotImage3;
	
	/**
	 * 
	 * @param screenToLoad Screen that should be started after loading assets
	 * @param game
	 * @param levelNumber
	 */
	public LoadingScreen(Screen screenToLoad, Game game, int levelNumber) {
		super(game);

		this.screenToLoad = screenToLoad;
		
		game.setLevelNumber(levelNumber);
	}

	@Override
	protected void setupActors(Stage stage) {

	}

	@Override
	protected void setupUI(Stage stage) {
		this.loadingImage = new Image(Textures.Loading);
		this.dotImage1 = new Image(Textures.dot);
		this.dotImage2 = new Image(Textures.dot);
		this.dotImage3 = new Image(Textures.dot);
		
		float x = (stage.getWidth() - (this.loadingImage.getWidth() + ( 3 * this.dotImage1.getWidth() ) ) ) / 2f;
		float y = (stage.getHeight() - this.loadingImage.getHeight()) / 2f;
		
		this.loadingImage.setX(x);
		this.loadingImage.setY(y);
		
		float padding = 10f;
		
		x += this.loadingImage.getWidth() + padding;
		this.dotImage1.setX(x);
		this.dotImage1.setY(y);
		this.dotImage1.getColor().a = 0f;
		this.dotImage1.addAction( sequence(
				fadeIn( 0.5f )));
		
		x += this.dotImage1.getWidth() + padding;
		this.dotImage2.setX(x);
		this.dotImage2.setY(y);
		this.dotImage2.getColor().a = 0f;
		this.dotImage2.addAction( sequence(
				delay( 0.5f ),
				fadeIn( 0.5f )));
		
		x += this.dotImage1.getWidth() + padding;
		this.dotImage3.setX(x);
		this.dotImage3.setY(y);
		this.dotImage3.getColor().a = 0f;
		this.dotImage3.addAction( sequence(
				delay( 1f ),
				fadeIn( 0.5f ),
				new Action() {

					@Override
					public boolean act(float arg0) {
						animationFinished = true;
						return true;
					}
					
				}));
		
		
		stage.addActor(this.loadingImage);
		stage.addActor(this.dotImage1);
		stage.addActor(this.dotImage2);
		stage.addActor(this.dotImage3);
	}

	@Override
	public void render(float delta) {
		this.stageUIActors.act();
		super.render(delta);
		
		if ( this.animationFinished ) {
			
//			game.setCurrentLevel(this.assetManager.get(Level.getLocalPath(this.levelNumber), Level.class));
			
			Game game = getGame();
			game.loadLevel();
			game.setScreen(this.screenToLoad);
		} 
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
}