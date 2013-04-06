package com.strategames.catchdastars.screens;

import com.strategames.catchdastars.Game;


public class LevelScreen extends AbstractScreen
{	
	public LevelScreen(
			Game game,
			int level )
	{
		super(game);
	}

	@Override
	protected boolean isGameScreen()
	{
		return true;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		getGame().setupStage(getStage());
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		getGame().update(delta);
	}
}
