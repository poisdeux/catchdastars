package com.strategames.engine.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;

public class MainMenuScreen extends AbstractScreen {


	public MainMenuScreen(Game game) {
		super(game);
	}

	@Override
	protected void setupUI(Stage stage) {
		final Game game = getGame();

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

		table.add( button ).uniform().fill().spaceBottom( 10 );
		table.row();

		button = new TextButton( "Settings", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().showSettings();
			}
		});
		table.add( button ).uniform().fill().spaceBottom( 10 );
		table.row();

		button = new TextButton( "High Scores", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
			}
		} );
		table.add( button ).uniform().fill().spaceBottom( 10 );
		table.row();
		
		button = new TextButton( "Game editor", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().showLevelEditorMenu();
			}
		} );
		table.add( button ).uniform().fill().spaceBottom( 10 );

		stage.addActor(table);
//		Gdx.input.setInputProcessor( stage );
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


