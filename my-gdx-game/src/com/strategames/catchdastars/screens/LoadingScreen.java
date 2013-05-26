package com.strategames.catchdastars.screens;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.LevelLoader;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

public class LoadingScreen extends AbstractScreen {

	private AssetManager assetManager;
	private boolean finishedLoading = false;
	private boolean animationFinished = false;
	private int levelNumber;
	
	private Image loadingImage;
	private Image dotImage1;
	private Image dotImage2;
	private Image dotImage3;
	
	public LoadingScreen(Game game, int levelNumber) {
		super(game);

		this.assetManager = getGame().getManager();
		this.levelNumber = levelNumber;
	}

	@Override
	protected void setupUI(Stage stage) {

	}

	@Override
	protected void setupActors(Stage stage) {
		this.loadingImage = new Image(new Texture( "images/Loading.png" ));
		this.dotImage1 = new Image(new Texture( "images/dot.png" ));
		this.dotImage2 = new Image(new Texture( "images/dot.png" ));
		this.dotImage3 = new Image(new Texture( "images/dot.png" ));
		
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
		
		this.assetManager.setLoader(Level.class, new LevelLoader(new InternalFileHandleResolver()));
		this.assetManager.load(Level.getLocalPath(this.levelNumber), Level.class);
	}

	@Override
	public void render(float delta) {
		this.stageActors.act();
		super.render(delta);
		Gdx.app.log("LoadingScreen", "render");
		
		if ( this.assetManager.update() && this.animationFinished ) {
			Game game = getGame();
			
			game.setCurrentLevel(this.assetManager.get(Level.getLocalPath(this.levelNumber), Level.class));
			
			this.finishedLoading = true;
			Textures.setup(this.assetManager);
			Sounds.setup(this.assetManager);

			game.setScreen(new LevelScreen(game));
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