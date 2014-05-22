package com.strategames.catchdastars.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;

public class MainMenuScreen extends AbstractScreen {

	public MainMenuScreen(Game game )
	{
		super(game);
	}


	@Override
	protected void setupUI(Stage stage) {
		final Game game = getGame();

		Table table = new Table( getSkin() );
		table.setFillParent(true);
		table.add( "Welcome to Catch Da Stars!" ).spaceBottom( 50 );
		table.row();

		TextButton button = new TextButton( "Start game", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				game.setLevelNumber(1);
				game.setScreen(new LevelScreen(game));
			}
		}); 

		table.add( button ).uniform().fill().spaceBottom( 10 );
		table.row();

		button = new TextButton( "Options", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
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
				getGame().setScreen(new LevelEditorMenuScreen(getGame()));
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


