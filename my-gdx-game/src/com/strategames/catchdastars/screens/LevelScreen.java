package com.strategames.catchdastars.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.Game;


public class LevelScreen extends AbstractScreen implements InputProcessor
{	
	private Game game;
	
	public LevelScreen(
			Game game )
	{
		super(game);
		this.game = game;
		Gdx.input.setInputProcessor(this);
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

	@Override
	public boolean keyDown(int keycode) {
		if( keycode == Keys.ESCAPE ) {
			getGame().setScreen(new MenuScreen(getGame()));
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
