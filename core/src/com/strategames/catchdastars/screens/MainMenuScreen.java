package com.strategames.catchdastars.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.engine.game.Game;
import com.strategames.engine.screens.AbstractScreen;

public class MainMenuScreen extends AbstractScreen {

	public MainMenuScreen(Game game) {
		super(game, "Welcome to Catch Da Stars!");
	}

	@Override
	protected void setupUI(Stage stage) {
		final Game game = getGame();
		
		float x = stage.getWidth() / 2f;
		float y = 600f;
		
		TextButton button = new TextButton( "New game", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				game.setTotalScore(0);
				game.startLevel(1);
			}
		});
		button.setPosition(x - (button.getWidth() / 2f), y);
		stage.addActor(button);
		y -= button.getHeight() + 20f;
		
		button = new TextButton( "Settings", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().showSettings();
			}
		});
		button.setPosition(x - (button.getWidth() / 2f), y);
		stage.addActor(button);
		y -= button.getHeight() + 20f;
		
		button = new TextButton( "High Scores", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
			}
		} );
		button.setPosition(x - (button.getWidth() / 2f), y);
		stage.addActor(button);
		y -= button.getHeight() + 20f;
		
		button = new TextButton( "Game editor", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().showLevelEditorMenu();
			}
		} );
		button.setPosition(x - (button.getWidth() / 2f), y);
		stage.addActor(button);
	}

	@Override
	protected void setupActors(Stage stage) {
	}

	@Override
	protected boolean handleBackNavigation() {
		Gdx.app.exit();
		return true;
	}	
}