package com.strategames.catchdastars.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.Game;


public class LevelScreen extends AbstractScreen implements InputProcessor
{	
	private Game game;
	
	public LevelScreen(Game game ) {
		super(game);
		this.game = game;
		game.startGame();
	}

	@Override
	public void hide() {
		super.hide();
		
//		this.game.disposeLevel();
	}
	
	@Override
	protected boolean isGameScreen()
	{
		return true;
	}

	@Override
	protected void setupUI(Stage stage) {
	}
	
	@Override
	protected void setupActors(Stage stage) {
		this.game.setupStage(stage);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		this.game.update(delta, super.stageActors);
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if( ( keycode == Keys.BACK ) ||
				( keycode == Keys.ESCAPE ) ) {
			getGame().pauseGame();
			return true;
		}
		return false;
	}
}
