package com.strategames.engine.screens;


import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.engine.game.Game;
import com.strategames.engine.tweens.TextButtonAccessor;

public class MainMenuScreen extends AbstractScreen {

	private TweenManager manager;

	public MainMenuScreen(Game game) {
		super(game);
	}

	@Override
	protected void setupUI(Stage stage) {
		final Game game = getGame();
		
		this.manager = new TweenManager();

		// We can now create as many interpolations as we need !
		
		Table table = new Table( getSkin() );
		table.setFillParent(true);
		table.add( "Welcome to Catch Da Stars!" ).spaceBottom( 50 );
		table.row();

		TextButton button = new TextButton( "New game", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				game.setTotalScore(0);
				game.startLevel(1);
			}
		}); 

		Tween.from(button, TextButtonAccessor.POSITION_Y, 1.0f)
	    .target(0)
	    .start(manager);
		table.add( button ).uniform().fill().spaceBottom( 10 );
		table.row();

		button = new TextButton( "Settings", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().showSettings();
			}
		});
		Tween.from(button, TextButtonAccessor.POSITION_Y, 1.0f)
	    .target(0)
	    .start(manager);
		table.add( button ).uniform().fill().spaceBottom( 10 );
		table.row();

		button = new TextButton( "High Scores", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
			}
		} );
		Tween.from(button, TextButtonAccessor.POSITION_Y, 1.0f)
	    .target(0)
	    .start(manager);
		table.add( button ).uniform().fill().spaceBottom( 10 );
		table.row();
		
		button = new TextButton( "Game editor", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().showLevelEditorMenu();
			}
		} );
		Tween.from(button, TextButtonAccessor.POSITION_Y, 1.0f)
	    .target(0)
	    .start(manager);
		table.add( button ).uniform().fill().spaceBottom( 10 );

		stage.addActor(table);
//		Gdx.input.setInputProcessor( stage );
	}
	
	@Override
	public void render(float delta) {
		this.manager.update(delta);
		super.render(delta);
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


