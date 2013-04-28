package com.strategames.catchdastars.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.utils.Grid;
import com.strategames.catchdastars.utils.Level;
import com.strategames.interfaces.OnSelectListener;
import com.strategames.ui.GameObjectConfigurationDialog;
import com.strategames.ui.GameObjectPickerDialog;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;

public class LevelEditorScreen extends AbstractScreen implements GestureListener, OnSelectListener, InputProcessor {

	private Vector2 longPressPosition;
	private Vector2 touchPositionObjectDelta;
	private Actor actorHit;
	private float previousZoomDistance;
	private InputMultiplexer multiplexer;
	private Actor uiElementHit;

	public LevelEditorScreen(Game game) {
		super(game);

		this.longPressPosition = new Vector2();
		this.touchPositionObjectDelta = new Vector2();
		this.actorHit = null;

		Gdx.input.setCatchBackKey(true);

		this.multiplexer = new InputMultiplexer();
		this.multiplexer.addProcessor(new GestureDetector(this));
		this.multiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(this.multiplexer);
	}

	@Override
	protected void setupUI(Stage stage) {
		this.multiplexer.addProcessor(stage);
	}

	@Override
	protected void setupActors(Stage stage) {
		getGame().setupStage(stage);
		this.multiplexer.addProcessor(stage);

		Array<Actor> actors = stage.getActors();
		for( Actor actor : actors ) {
			deselectGameObject((GameObject) actor);
		}
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Gdx.app.log("LevelEditorScreen", "touchDown");
		this.previousZoomDistance = 0f; // reset zoom distance

		Stage stage = getStageUIElements();
		Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));

		this.uiElementHit = stage.hit(stageCoords.x, stageCoords.y, false);

		stage = getStageActors();
		Actor actor = stage.hit(stageCoords.x, stageCoords.y, false);
		if( actor != null ) { // actor selected
			deselectGameObject((GameObject) this.actorHit);
			selectGameObject((GameObject) actor);
			this.actorHit = actor;
		} else if( this.uiElementHit == null ) { // empty space selected
			deselectGameObject((GameObject) this.actorHit);
			this.actorHit = null;
		}

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Gdx.app.log("LevelEditorScreen", "touchDragged");
		if( ( this.actorHit != null ) && ( this.uiElementHit == null ) ){
			Vector2 stageCoords = getStageActors().screenToStageCoordinates(new Vector2(screenX, screenY));
			Gdx.app.log("LevelEditorScreen", "touchDragged: stageCoords: x="+stageCoords.x+
					", y="+stageCoords.y);
			//			Vector2 stageCoordsMapped = Grid.map(stageCoords.x , stageCoords.y);
			//			Gdx.app.log("LevelEditorScreen", "touchDragged: stageCoordsMapped: x="+stageCoordsMapped.x+
			//					", y="+stageCoordsMapped.y);

			
			//Check if actor does not overlap other actor when moving horizontally
			Rectangle rectangle = new Rectangle(stageCoords.x, this.actorHit.getY(),
					this.actorHit.getWidth(), this.actorHit.getHeight());
			if( getActor(rectangle) != null ) { // we cannot move horizontally
				stageCoords.x = this.actorHit.getX();
			}
			
			//Check if actor does not overlap other actor when moving vertically
			rectangle = new Rectangle(this.actorHit.getX(), stageCoords.y,
					this.actorHit.getWidth(), this.actorHit.getHeight());
			if( getActor(rectangle) != null ) { // we cannot move vertically
				stageCoords.y = this.actorHit.getY();
			}
			
			GameObject gameObject = (GameObject) this.actorHit;
			gameObject.moveTo(stageCoords.x, stageCoords.y);
			return true;
		}
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		Gdx.app.log("LevelEditorScreen", "tap");
		Stage stage = getStageActors();
		Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));

		if( ( this.actorHit != null ) && ( this.uiElementHit == null ) ){
			Gdx.app.log("LevelEditorScreen", "touchDown: hit " + actorHit.getName());
			this.touchPositionObjectDelta.x = this.actorHit.getX() - stageCoords.x;
			this.touchPositionObjectDelta.y = this.actorHit.getY() - stageCoords.y;
			return true;
		}
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		Gdx.app.log("LevelEditorScreen", "longPress");
		this.longPressPosition.set(x, y);

		if( this.actorHit != null ) {
			GameObjectConfigurationDialog dialog = new GameObjectConfigurationDialog((GameObject) this.actorHit, getSkin());
			dialog.show(getStageUIElements());
		} else {
			GameObjectPickerDialog dialog = new GameObjectPickerDialog(getGame(), getSkin(), this);
			dialog.addButton("Quit", this);
			dialog.show(getStageUIElements());
		}
		return true;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		Gdx.app.log("LevelEditorScreen", "fling");
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		Gdx.app.log("LevelEditorScreen", "pan");
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		Gdx.app.log("LevelEditorScreen", "zoom");
		if( this.previousZoomDistance == 0 ) {
			this.previousZoomDistance = distance;
		}

		if( this.actorHit == null ) return false;

		if( distance > this.previousZoomDistance ) {
			GameObject gameObject = (GameObject) this.actorHit;
			gameObject.increaseSize();
		} else if( distance < this.previousZoomDistance ) {
			GameObject gameObject = (GameObject) this.actorHit;
			gameObject.decreaseSize();
		}
		return true;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		Gdx.app.log("LevelEditorScreen", "pinch");
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		if((keycode == Keys.BACK) 
				|| (keycode == Keys.ESCAPE)) {
			saveLevel();
			getGame().setScreen(new LevelEditorMenuScreen(getGame()));
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
		Gdx.app.log("LevelEditorScreen", "touchDown");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Gdx.app.log("LevelEditorScreen", "touchUp");
		//Hold reference to previous actor in case user performs a pinch gesture
		//		this.previousActorHit = this.actorHit;
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

	@Override
	public void onObjectSelectListener(GameObject object) {
		Gdx.app.log("LevelEditorScreen", "onSelectListener");
		Stage stage = getStageActors();
		GameObject copy = object.createCopy();
		Vector2 stageCoords = stage.screenToStageCoordinates(this.longPressPosition);
		//		Vector2 stageCoordsMapped = Grid.map(stageCoords);
		copy.setPosition(stageCoords.x, stageCoords.y);
		getGame().addGameObject(copy);
		stage.addActor(copy);
		deselectGameObject(copy);
	}

	private void selectGameObject(GameObject gameObject) {
		if( gameObject == null) return;

		gameObject.setColor(1f, 1f, 1f, 1.0f);
	}

	private void deselectGameObject(GameObject gameObject) {
		if( gameObject == null) return;

		gameObject.setColor(0.7f, 0.7f, 0.7f, 1.0f);
	}

	private void saveLevel() {
		Game game = getGame();
		Level level = game.getCurrentLevel();

		ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
		for( Actor actor : getStageActors().getActors() ) {
			gameObjects.add((GameObject) actor);
		}
		level.setGameObjects(gameObjects);
		level.save();
	}

	@Override
	public void onPressedListener(Button button) {
		if( button.getName().contentEquals("Quit") ) {
			saveLevel();
			getGame().setScreen(new LevelEditorMenuScreen(getGame()));
		} else if( button.getName().contentEquals("Delete object") ) {
			this.actorHit.remove();
		}
	}

	private Actor getActor(Rectangle rectangle) {
		Array<Actor> actors = getStageActors().getActors();
		for(Actor actor : actors) {
			if( actor == this.actorHit ) continue;
			Rectangle rectangleActor = new Rectangle(actor.getX(), actor.getY(), 
					actor.getWidth(), actor.getHeight());
			if( rectangle.overlaps(rectangleActor) ) return actor;
		}
		return null;
	}
}
