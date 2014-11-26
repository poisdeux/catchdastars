package com.strategames.catchdastars.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.strategames.engine.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.CatchDaStars;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.utils.Game;

public class MainMenuScreen extends AbstractScreen {

	public MainMenuScreen(GameEngine game) {
		super(game, "Welcome to Catch Da Stars!");
	}

	@Override
	protected void setupUI(Stage stage) {
		final GameEngine gameEngine = getGameEngine();
		
		float x = stage.getWidth() / 2f;
		float y = 600f;
		
		TextButton button = new TextButton( "New game", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				Game game = new Game();
				game.setCurrentLevelPosition(new int[] {0,0});
				gameEngine.setGame(game);
				gameEngine.resetGame();
			}
		});
		button.setPosition(x - (button.getWidth() / 2f), y);
		stage.addActor(button);
		y -= button.getHeight() + 20f;
		
		button = new TextButton( "Settings", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				((CatchDaStars) getGameEngine()).showSettings();
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
				((CatchDaStars) getGameEngine()).showSelectGameScreen();
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