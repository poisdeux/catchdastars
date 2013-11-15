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

		// retrieve the default table actor
		//		Table table = super.getTable();
		Gdx.app.log("MainMenuScreen", "setupUI: stage.getWidth="+stage.getWidth()+", stage.getHeight()="
				+stage.getHeight());
		Table table = new Table( getSkin() );
		table.setFillParent(true);
		table.add( "Welcome to Catch Da Stars!" ).spaceBottom( 50 );
		table.row();

		TextButton button = new TextButton( "Start game", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
//				AbstractScreen screen = new LevelScreen(game);
//				game.setScreen( new LoadingScreen(screen, game, 1) );
				game.setLevelNumber(1);
				game.setScreen(new LevelScreen(game));
			}
		}); 

		table.add( button ).uniform().fill().spaceBottom( 10 );
		table.row();

		button = new TextButton( "Options", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("MenuScreen", "touch done at (" +x+ ", " +y+ ")");
				//				super.touchUp( event, x, y, pointer, button );
				//				game.getSoundManager().play( TyrianSound.CLICK );
				//				game.setScreen( new OptionsScreen( game ) );
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
		// TODO Auto-generated method stub
		Gdx.app.log("MainMenuScreen", "setupActors: stage.getWidth="+stage.getWidth()+", stage.getHeight()="
				+stage.getHeight());
	}
	
	@Override
	protected boolean handleBackNavigation() {
		Gdx.app.exit();
		return true;
	}
}


