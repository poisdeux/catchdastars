package com.strategames.catchdastars.screens;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

public class SplashScreen extends AbstractScreen implements InputProcessor {

	private AssetManager assetManager;
	private boolean finishedSetupAssets = false;

	public SplashScreen(Game game) {
		super(game);

		this.assetManager = getGame().getManager();

		Gdx.input.setInputProcessor(this);
	}

	private Image splashImage;

	@Override
	protected void setupActors(Stage stage) {

	}

	@Override
	protected void setupUI(Stage stage) {
		Texture texture = new Texture( "images/splashscreen.png" );

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
							game.setScreen(new MainMenuScreen(game));
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
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if ( this.finishedSetupAssets ) {
			getGame().setScreen(new MainMenuScreen(getGame()));
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}