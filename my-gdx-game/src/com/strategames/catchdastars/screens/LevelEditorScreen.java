package com.strategames.catchdastars.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.ui.GameObjectPickerDialog;
import com.strategames.ui.GameObjectPickerDialog.SelectListener;

public class LevelEditorScreen extends AbstractScreen implements GestureListener, SelectListener {

	private Vector2 touchPosition;
	private float displayHeight;
	
	public LevelEditorScreen(Game game) {
		super(game);
		
		this.touchPosition = new Vector2();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		getGame().setupStage(getStage());
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(new GestureDetector(this));
		multiplexer.addProcessor(getStage());
		Gdx.input.setInputProcessor(multiplexer);
		
		this.displayHeight = Gdx.app.getGraphics().getHeight();
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		this.touchPosition.set(x, y);
		GameObjectPickerDialog dialog = new GameObjectPickerDialog(getGame(), getSkin(), this);
		getStage().addActor(dialog);
		return true;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void onSelectListener(GameObject object) {
		object.setPosition(this.touchPosition.x, this.displayHeight - this.touchPosition.y);
		getGame().addGameObject(object.createCopy());
	}

}
