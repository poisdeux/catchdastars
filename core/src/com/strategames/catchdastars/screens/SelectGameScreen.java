package com.strategames.catchdastars.screens;

import com.strategames.engine.game.GameEngine;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;

public class SelectGameScreen extends AbstractScreen {

	public SelectGameScreen(GameEngine game) {
		super(game, "Select a game");
	}
	
	@Override
	protected void setupUI(Stage stage) {
		//Gameloader to load all games
		//Show list of games as vertically scrollable buttonlist
	}

	@Override
	protected void setupActors(Stage stage) {
		// TODO Auto-generated method stub
		
	}

}
