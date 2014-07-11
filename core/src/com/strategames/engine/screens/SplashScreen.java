package com.strategames.engine.screens;


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
import com.strategames.engine.game.Game;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Sounds;
import com.strategames.engine.utils.Textures;

public class SplashScreen extends AbstractScreen {

	private AssetManager assetManager;
	private boolean finishedSetupAssets = false;
	private Image splashImage;

	public SplashScreen(Game game) {
		super(game);

		this.assetManager = getGame().getManager();
	}
	
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

		Textures.getInstance().load(assetManager);
		Sounds.getInstance().load(assetManager);
		
		stage.addActor( this.splashImage );
		
		MusicPlayer player = MusicPlayer.getInstance();
		player.setLibrary(getGame().getMusicSelector().getLibrary());
	}

	@Override
	public void render(float delta) {
		this.stageUIActors.act();
		super.render(delta);

		if ( this.assetManager.update() && ( ! this.finishedSetupAssets ) ) {
			Textures.getInstance().setup(this.assetManager);
			Sounds.getInstance().setup(this.assetManager);

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