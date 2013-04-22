package com.strategames.catchdastars.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.Game;


public class LevelScreen extends AbstractScreen
{	
	private Game game;
	
	public LevelScreen(
			Game game )
	{
		super(game);
		this.game = game;
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
		this.game.setupStage(stage);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		this.game.update(delta);
	}
}
