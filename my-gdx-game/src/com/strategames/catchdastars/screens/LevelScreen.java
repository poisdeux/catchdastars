package com.strategames.catchdastars.screens;

import com.strategames.catchdastars.Game;


public class LevelScreen extends AbstractScreen
{
	private Game game;
	
	public LevelScreen(
			Game game,
			int level )
	{
		this.game = game;
	}

	@Override
	protected boolean isGameScreen()
	{
		return true;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		this.game.setupStage(getStage());
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		this.game.update(delta);
	}
}
