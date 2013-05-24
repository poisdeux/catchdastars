package com.strategames.catchdastars.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.Textures;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class SplashScreen extends AbstractScreen implements InputProcessor {
	
	private AssetManager assetManager;
	
	public SplashScreen(Game game) {
		super(game);

		this.assetManager = game.getManager();
		
		Gdx.input.setInputProcessor(this);
	}

	private Image splashImage;

	@Override
	protected void setupUI(Stage stage) {

	}

	@Override
	protected void setupActors(Stage stage) {
		Texture texture = new Texture( "images/splashscreen.png" );

		this.splashImage = new Image(texture);
		this.splashImage.addAction( sequence( fadeIn( 0.75f ), 
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
		this.splashImage.setPosition(0, 
				Gdx.graphics.getHeight()/2 - this.splashImage.getHeight()/2);

		this.splashImage.getColor().a = 0f;
		
		stage.addActor( this.splashImage );
		
		//Now setup assets to load in background
		this.assetManager.load("packed/pack.atlas", TextureAtlas.class);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (this.assetManager.update()) {
			Textures.setup(this.assetManager.get("packed/pack.atlas", TextureAtlas.class));
			Game game = getGame();
			game.setScreen(new MainMenuScreen(game));
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
		getGame().setScreen(new MainMenuScreen(getGame()));
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