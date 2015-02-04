package com.strategames.catchdastars.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.strategames.engine.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.game.CatchDaStars;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.screens.AbstractScreen;

public class MainMenuScreen extends AbstractScreen {

	public MainMenuScreen(GameEngine game) {
		super(game);
        setTitle(new Label("Welcome to Catch Da Stars!", getSkin()));
	}

	@Override
	protected void setupUI(Stage stage) {
		float x = stage.getWidth() / 2f;
		float y = 600f;

		TextButton button = new TextButton( "Play", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
                //If original game is completed show SelectGameScreen
                //otherwise show GameMenuScreen for original game
				((CatchDaStars) getGameEngine()).showSelectGameScreen();
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

        //Check if original game was completed before showing editor
		button = new TextButton( "Game editor", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				((CatchDaStars) getGameEngine()).showGameEditorScreen();
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