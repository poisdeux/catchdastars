package com.strategames.catchdastars.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;

public class MenuScreen extends AbstractScreen {
	private Game game;
	
	public MenuScreen(
			Game game )
	{
		this.game = game;
	}


	@Override
	public void show()
	{
		// retrieve the default table actor
//		Table table = super.getTable();
		Table table = new Table( getSkin() );
		table.setFillParent(true);
		table.add( "Welcome to Catch Da Stars!" ).spaceBottom( 50 );
		table.row();

		// register the button "start game"
		TextButton startGameButton = new TextButton( "Start game", getSkin() );
		startGameButton.addListener( new ClickListener() {
			
			public void clicked(InputEvent event, float x, float y) {
			        Gdx.app.log("MenuScreen", "touch done at (" +x+ ", " +y+ ")");
//				game.getSoundManager().play( TyrianSound.CLICK );
				game.setScreen( new LevelScreen( game, 1 ) );
			}
		}); 
				
		table.add( startGameButton ).size( 300, 60 ).uniform().spaceBottom( 10 );
		table.row();

		// register the button "options"
		TextButton optionsButton = new TextButton( "Options", getSkin() );
		optionsButton.addListener( new ClickListener() {
			
			public void clicked(InputEvent event, float x, float y) {
		        Gdx.app.log("MenuScreen", "touch done at (" +x+ ", " +y+ ")");
//				super.touchUp( event, x, y, pointer, button );
//				game.getSoundManager().play( TyrianSound.CLICK );
//				game.setScreen( new OptionsScreen( game ) );
		     }
		  });
		table.add( optionsButton ).uniform().fill().spaceBottom( 10 );
		table.row();

		// register the button "high scores"
		TextButton highScoresButton = new TextButton( "High Scores", getSkin() );
		highScoresButton.addListener( new ClickListener() {
			
			public void clicked(InputEvent event, float x, float y) {
	        	Gdx.app.log("MenuScreen", "touch done at (" +x+ ", " +y+ ")");
//				super.touchUp( event, x, y, pointer, button );
//				game.getSoundManager().play( TyrianSound.CLICK );
//				game.setScreen( new HighScoresScreen( game ) );
	        }
	    } );
		
		
		table.add( highScoresButton ).uniform().fill();
		
		Stage stage = getStage();
		stage.addActor(table);
		Gdx.input.setInputProcessor( stage );
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
}


