package com.strategames.catchdastars.screens;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

public class SplashScreen extends AbstractScreen {

	private AssetManager assetManager;
	private boolean finishedSetupAssets = false;
	private Image splashImage;

	public SplashScreen(AbstractScreen previousScreen, Game game) {
		super(previousScreen, game);

		this.assetManager = getGame().getManager();

//		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	protected void setupActors(Stage stage) {

	}

	@Override
	protected void setupUI(Stage stage) {
		Texture texture = Textures.getSplashScreen();

		this.splashImage = new Image(texture);

		this.splashImage.setPosition(0, 
				Gdx.graphics.getHeight()/2 - this.splashImage.getHeight()/2);

		this.splashImage.addAction( fadeIn( 0.75f ));

		this.splashImage.getColor().a = 0f;

		stage.addActor( this.splashImage );

		//Now setup assets to load in background
		Textures.load(this.assetManager);
		Sounds.load(this.assetManager);
	}

	@Override
	public void render(float delta) {
		this.stageUIActors.act();
		super.render(delta);

		if ( this.assetManager.update() && ( ! this.finishedSetupAssets ) ) {
			Textures.setup(this.assetManager);
			Sounds.setup(this.assetManager);

			this.finishedSetupAssets = true;
			
			this.splashImage.addAction( sequence(
					delay( 0.75f ),
					fadeOut( 0.75f ),
					new Action() {

						@Override
						public boolean act(float delta) {
							Game game = getGame();
							game.setScreen(new MainMenuScreen(null, game));
							return true;
						}
					}));
		} 
	}


	@Override
	public void dispose()
	{
		super.dispose();
		this.splashImage.remove();
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
}