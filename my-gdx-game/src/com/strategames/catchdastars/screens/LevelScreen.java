package com.strategames.catchdastars.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.Game;


public class LevelScreen extends AbstractScreen
{	
	public LevelScreen(
			Game game )
	{
		super(game);
	}

	@Override
	protected boolean isGameScreen()
	{
		return true;
	}

	@Override
	protected void setupUI(Stage stage) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void setupActors(Stage stage) {
		getGame().setupStage(stage);
	}
}
