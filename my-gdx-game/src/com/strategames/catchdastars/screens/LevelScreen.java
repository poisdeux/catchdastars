package com.strategames.catchdastars.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.Game;


public class LevelScreen extends AbstractScreen implements InputProcessor
{	
	private Game game;
	private InputMultiplexer multiplexer;
	
	public LevelScreen(Screen screen, Game game )
	{
		super(screen, game);
		this.game = game;
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
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
		this.multiplexer.addProcessor(stage);
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

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
}
