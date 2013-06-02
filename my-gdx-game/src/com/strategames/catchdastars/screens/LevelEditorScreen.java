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
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.utils.Level;
import com.strategames.interfaces.DialogInterface;
import com.strategames.ui.Dialog;
import com.strategames.ui.GameObjectConfigurationDialog;
import com.strategames.ui.GameObjectPickerDialog;

public class LevelEditorScreen extends AbstractScreen implements GestureListener, DialogInterface, InputProcessor {

	private Vector2 longPressPosition;
	private Vector2 touchPositionObjectDelta;
	private Actor actorHit;
	private float previousZoomDistance;
	private InputMultiplexer multiplexer;
	private Actor uiElementHit;
	private Game game;
	private boolean testGame;

	private enum States {
		ZOOM, LONGPRESS, DRAG, NONE
	}

	private States state;

	private class Tap {
		private long tapTime1;
		private long tapTime2;
		private Actor actor;

		public Tap() {
		}

		public void tap() {
			tapTime1 = tapTime2;
			tapTime2 = System.currentTimeMillis();
		}

		public boolean doubleTapped() {
			return ( tapTime2 - tapTime1 ) < 200;
		}

		public void setActor(Actor actor) {
			this.actor = actor;
		}

		public Actor getActor() {
			return actor;
		}

		@Override
		public String toString() {
			return "tapTime1="+tapTime1+", tapTime2="+tapTime2+", actor="+actor;
		}
	}

	private Tap tap = new Tap();

	public LevelEditorScreen(Game game) {
		super(game);

		this.game = game;

		this.testGame = false;

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
		this.game.setupStage(stage);
		this.multiplexer.addProcessor(stage);

		Array<Actor> actors = stage.getActors();
		for( Actor actor : actors ) {
			deselectGameObject((GameObject) actor);
		}
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if( testGame ) {
			this.game.update(delta, stageActors);
		}
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Gdx.app.log("LevelEditorScreen", "touchDown: x="+x+", y="+y);
		if( this.testGame ) { //do not handle event in game mode
			return false;
		}

		this.previousZoomDistance = 0f; // reset zoom distance
		this.state = States.NONE;

		Vector2 touchPosition = new Vector2(x, y);

		Stage stage = getStageUIElements();
		stage.screenToStageCoordinates(touchPosition);
		this.uiElementHit = stage.hit(touchPosition.x, touchPosition.y, false);

		touchPosition.set(x, y);   //reset vector
		stage = getStageActors();
		stage.screenToStageCoordinates(touchPosition);
		Actor actor = stage.hit(touchPosition.x, touchPosition.y, false);



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
		if( ( this.state == States.DRAG ) || 
				( this.state == States.NONE ) ) {
			this.state = States.DRAG;

			if( ( this.actorHit != null ) && ( this.uiElementHit == null ) ){
				Vector2 stageCoords = getStageActors().screenToStageCoordinates(new Vector2(screenX, screenY));

				GameObject gameObject = (GameObject) this.actorHit;
				Vector2 v = gameObject.getBody().getWorldCenter();
				float left = gameObject.getX() - v.x;
				float bottom = gameObject.getY() - v.y;

				//Check if actor does not overlap other actor when moving horizontally
				Rectangle rectangle = new Rectangle(stageCoords.x + left, this.actorHit.getY(),
						this.actorHit.getWidth(), this.actorHit.getHeight());
				if( getActor(rectangle) != null ) { // we cannot move horizontally
					stageCoords.x = v.x;
				}

				//Check if actor does not overlap other actor when moving vertically
				rectangle = new Rectangle(this.actorHit.getX(), stageCoords.y + bottom,
						this.actorHit.getWidth(), this.actorHit.getHeight());
				if( getActor(rectangle) != null ) { // we cannot move vertically
					stageCoords.y = v.y;
				}

				gameObject.moveTo(stageCoords.x, stageCoords.y);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean tap(final float x, final float y, int count, int button) {
		/**
		 * Used to show gameobject configuration window when double tapped
		 */
		Gdx.app.log("LevelEditorScreen", "tap");
		if( this.testGame ) { //return to edit mode
			this.testGame = false;
			getGame().reset();
		}

		tap.tap();

		if( ( this.actorHit != null ) && ( this.uiElementHit == null ) ){
			if( tap.doubleTapped() && ( this.actorHit == tap.getActor() ) ) {
				Gdx.app.log("LevelEditorScreen", "tap: this.actorTapped="+this.tap);

				GameObjectConfigurationDialog dialog = new GameObjectConfigurationDialog((GameObject) this.actorHit, getSkin());
				dialog.addButton("Copy " + this.actorHit.getName(), new OnClickListener() {

					@Override
					public void onClick(Dialog dialog, int which) {
						Stage stage = getStageActors();
						GameObject copy = ((GameObjectConfigurationDialog) dialog).getGameObject().createCopy();
						Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(x, y));
						//		Vector2 stageCoordsMapped = Grid.map(stageCoords);
						copy.setPosition(stageCoords.x, stageCoords.y);
						getGame().addGameObject(copy);
						stage.addActor(copy);
						deselectGameObject(copy);
					}
				});
				dialog.addButton("Delete " + this.actorHit.getName(), new OnClickListener() {

					@Override
					public void onClick(Dialog dialog, int which) {
						((GameObjectConfigurationDialog) dialog).getGameObject().remove();
						dialog.remove();
					}
				});

				dialog.setPositiveButton("Close", new OnClickListener() {

					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.remove();
					}
				});
				dialog.show(getStageUIElements());
				
				return true;
			} 
			
		}
		
		tap.setActor(this.actorHit);
		
		return false;
	}

	@Override
	public boolean longPress(final float x, final float y) {
		/**
		 * Used to show generic configuration window
		 */
		Gdx.app.log("LevelEditorScreen", "longPress: x="+x+", y="+y);
		if( this.testGame ) { //do not handle event in game mode
			return false;
		}

		this.longPressPosition.x = x;
		this.longPressPosition.y = y;

		if( this.state == States.NONE ) {
			this.state = States.LONGPRESS;

			if( this.uiElementHit != null ) return false;

			if( ( this.actorHit == null ) ) {
				GameObjectPickerDialog dialog = new GameObjectPickerDialog(getGame(), getSkin(), this);
				dialog.setNeutralButton("Test", new OnClickListener() {

					@Override
					public void onClick(Dialog dialog, int which) {
						multiplexer.removeProcessor(getStageActors());
						testGame = true;
						saveLevel();
						dialog.remove();
					}
				});
				dialog.setPositiveButton("Close", new OnClickListener() {

					@Override
					public void onClick(Dialog dialog, int which) {
						saveLevel();
						dialog.remove();
					}
				});
				dialog.setNegativeButton("Quit", new OnClickListener() {

					@Override
					public void onClick(Dialog dialog, int which) {
						saveLevel();
						getGame().setScreen(new LevelEditorMenuScreen(getGame()));
					}
				});
				dialog.show(getStageUIElements());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		//		Gdx.app.log("LevelEditorScreen", "fling");
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		//		Gdx.app.log("LevelEditorScreen", "pan");
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		//		Gdx.app.log("LevelEditorScreen", "zoom");
		if( this.testGame ) { //do not handle event in game mode
			return false;
		}

		if( ( this.state == States.ZOOM) || 
				( this.state == States.NONE ) ) {
			this.state = States.ZOOM;
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
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		//		Gdx.app.log("LevelEditorScreen", "pinch");
		return true;
	}

	@Override
	public boolean keyDown(int keycode) {
		if((keycode == Keys.BACK) 
				|| (keycode == Keys.ESCAPE)) {
			if( ! this.testGame ) { //do not save in game mode
				saveLevel();
			}
			getGame().setScreen(new LevelEditorMenuScreen(getGame()));
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
		//		Gdx.app.log("LevelEditorScreen", "touchDown");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//		Gdx.app.log("LevelEditorScreen", "touchUp");
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
		//		Gdx.app.log("LevelEditorScreen", "onSelectListener");
		Stage stage = getStageActors();
		GameObject copy = object.createCopy();
		Vector2 stageCoords = stage.screenToStageCoordinates(this.longPressPosition);
		//		Vector2 stageCoordsMapped = Grid.map(stageCoords);
		copy.setPosition(stageCoords.x, stageCoords.y);
		Gdx.app.log("LevelEditorScreen", "onObjectSelectListener: stageCoords.x="+stageCoords.x+", stageCoords.y"+stageCoords.y);
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
		Level level = game.getLevel();

		ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
		for( Actor actor : getStageActors().getActors() ) {
			gameObjects.add((GameObject) actor);
		}
		level.setGameObjects(gameObjects);
		level.save();
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
