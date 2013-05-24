package com.strategames.catchdastars.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;

public class LoaderScreen extends AbstractScreen {

	private Image loadingImage;
	private Image dotImage1;
	private Image dotImage2;
	private Image dotImage3;
	
	private Game game;
	
	public LoaderScreen(Game game) {
		super(game);
		this.game = game;
	}

	@Override
	protected void setupUI(Stage stage) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setupActors(Stage stage) {
		this.loadingImage = new Image(new Texture( "images/Loading.png" ));
		this.dotImage1 = new Image(new Texture( "images/dot.png" ));
		this.dotImage2 = new Image(new Texture( "images/dot.png" ));
		this.dotImage3 = new Image(new Texture( "images/dot.png" ));
		
		float x = (stage.getWidth() - this.loadingImage.getWidth()) / 2f;
		float y = (stage.getHeight() - this.loadingImage.getHeight()) / 2f + 100f;
		
		this.loadingImage.setX(x);
		this.loadingImage.setY(y);
		
		float padding = 10f;
		
		x += stage.getWidth() + padding;
		this.dotImage1.setX(x);
		this.dotImage1.setY(y);

		x += this.dotImage1.getWidth() + padding;
		this.dotImage2.setX(x);
		this.dotImage2.setY(y);
		
		x += this.dotImage1.getWidth() + padding;
		this.dotImage3.setX(x);
		this.dotImage3.setY(y);
		
		stage.addActor(this.loadingImage);
	}
}

