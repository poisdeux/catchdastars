package com.strategames.catchdastars.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.ui.GameObjectPickerDialog;
import com.strategames.ui.GameObjectPickerDialog.SelectListener;

public class LevelEditorScreen extends AbstractScreen implements GestureListener, SelectListener, InputProcessor {

	private Vector2 touchPosition;
	private Actor actorHit;
	
	public LevelEditorScreen(Game game) {
		super(game);
		
		this.touchPosition = new Vector2();
		this.actorHit = null;
	}

	@Override
	public void show() {
		getGame().setupStage(getStageActors());
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(new GestureDetector(this));
		multiplexer.addProcessor(this);
		multiplexer.addProcessor(getStageActors());
		multiplexer.addProcessor(getStageUIElements());
		Gdx.input.setInputProcessor(multiplexer);
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Stage stage = getStageActors();
		Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));
		this.actorHit = stage.hit(stageCoords.x, stageCoords.y, false);
		if( actorHit != null ) {
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if( this.actorHit != null ) {
			Gdx.app.log("LevelEditorScreen", "touchDragged:");
			Vector2 stageCoords = getStageActors().screenToStageCoordinates(new Vector2(screenX, screenY));
			GameObject gameObject = (GameObject) this.actorHit;
			gameObject.moveTo(stageCoords.x, stageCoords.y);
			return true;
		}
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
		getStageUIElements().addActor(dialog);
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
		Gdx.app.log("LevelEditorScreen", "onSelectListener");
		Stage stage = getStageActors();
		GameObject copy = object.createCopy();
		Vector2 stageCoords = stage.screenToStageCoordinates(this.touchPosition);
		copy.setPosition(stageCoords.x, stageCoords.y);
		getGame().addGameObject(copy);
		stage.addActor(copy);
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
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
