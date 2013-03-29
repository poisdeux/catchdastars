package com.me.mygdxgame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuScreen extends AbstractScreen {
	public MenuScreen(
	        Game game )
	    {
	        super( game );
	    }

	    @Override
	    public void show()
	    {
	        super.show();

	        // retrieve the default table actor
	        Table table = super.getTable();
	        table.add( "Welcome to Tyrian for Android!" ).spaceBottom( 50 );
	        table.row();

	        // register the button "start game"
	        TextButton startGameButton = new TextButton( "Start game", getSkin() );
	        startGameButton.addListener(new EventListener() {
				
				@Override
				public boolean handle(Event event) {
					
					return false;
				}
			});
	        		
	        	
	        table.add( startGameButton ).size( 300, 60 ).uniform().spaceBottom( 10 );
	        table.row();

	        // register the button "options"
	        TextButton optionsButton = new TextButton( "Options", getSkin() );
	        optionsButton.addListener( new EventListener() {
				
				@Override
				public boolean handle(Event event) {
					
					return false;
				}
			});
	        table.add( optionsButton ).uniform().fill().spaceBottom( 10 );
	        table.row();

	        // register the button "high scores"
	        TextButton highScoresButton = new TextButton( "High Scores", getSkin() );
	        highScoresButton.addListener( new EventListener() {
				
				@Override
				public boolean handle(Event event) {
					
					return false;
				}
			});
	        table.add( highScoresButton ).uniform().fill();
	    }
	}
