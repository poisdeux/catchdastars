package com.strategames.catchdastars.screens;

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
	public void show() {
		getGame().setupStage(getStageActors());
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		getGame().update(delta);
	}
}
