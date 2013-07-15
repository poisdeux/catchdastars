package com.strategames.catchdastars.screens;

import com.badlogic.gdx.Gdx;
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
import com.strategames.ui.ToolsPickerDialog;

public class LevelEditorScreen extends AbstractScreen implements GestureListener, DialogInterface {

	private Vector2 longPressPosition;
	private Vector2 dragOffset;
	private Actor actorHit;
	private float previousZoomDistance;
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
		Gdx.app.log("LevelEditorScreen", "LevelEditorScreen");
		
		this.game = game;

		this.testGame = false;

		this.longPressPosition = new Vector2();
		this.dragOffset = new Vector2();
		
		this.actorHit = null;
		
		getMultiplexer().addProcessor(new GestureDetector(this));
	}

	@Override
	protected void setupUI(Stage stage) {
	}

	@Override
	protected void setupActors(Stage stage) {
		this.game.setupStage(stage);
		getMultiplexer().addProcessor(stage);
		
		Array<Actor> actors = stage.getActors();
		for( Actor actor : actors ) {
			deselectGameObject((GameObject) actor);
		}
	}

	@Override
	protected boolean handleBackNavigation() {
		saveLevel();
		getGame().setScreen(new LevelEditorMenuScreen(getGame()));
		return true;
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
		Gdx.app.log("LevelEditorScreen", "touchDown float");
		
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
			Vector2 v = new Vector2(this.actorHit.getX(), this.actorHit.getY());
			stage.stageToScreenCoordinates(v);
			this.dragOffset.x = x - v.x;
			this.dragOffset.y = v.y - (Gdx.graphics.getHeight() - y); //Touch event coordinates are y-down coordinated
		} else if( this.uiElementHit == null ) { // empty space selected
			deselectGameObject((GameObject) this.actorHit);
			this.actorHit = null;
		}
		
		return true;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if( ( this.state == States.DRAG ) || 
				( this.state == States.NONE ) ) {
			this.state = States.DRAG;

			if( ( this.actorHit != null ) && ( this.uiElementHit == null ) ){
				Vector2 v = new Vector2(screenX - this.dragOffset.x, screenY - this.dragOffset.y);
				getStageActors().screenToStageCoordinates(v);
				moveActor(this.actorHit, v.x, v.y);
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
		if( this.testGame ) { //return to edit mode
			this.testGame = false;
			getGame().reset();
		}

		tap.tap();

		if( ( this.actorHit != null ) && ( this.uiElementHit == null ) ){
			if( tap.doubleTapped() && ( this.actorHit == tap.getActor() ) ) {
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
		if( this.testGame || (this.actorHit != null) ) {
			return false;
		}

		this.longPressPosition.x = x;
		this.longPressPosition.y = y;

		if( this.state == States.NONE ) {
			this.state = States.LONGPRESS;

			if( this.uiElementHit != null ) return false;

			if( ( this.actorHit == null ) ) {
				GameObjectPickerDialog dialog = new GameObjectPickerDialog(getGame(), getSkin(), this);
				dialog.setNeutralButton("Tools", new OnClickListener() {

					@Override
					public void onClick(Dialog dialog, int which) {
						ToolsPickerDialog tDialog = new ToolsPickerDialog(game, skin);
						tDialog.show(getStageUIElements());
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
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
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
		return true;
	}

	@Override
	public void onObjectSelectListener(GameObject object) {
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
		Level level = game.getLevel();
		level.setGameObjects(game.getGameObjects());
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
	
	private void moveActor(Actor actor, float x, float y) {
		GameObject gameObject = (GameObject) actor;
		
		Rectangle rectangle = gameObject.getBoundingRectangle();
		float curX = rectangle.x;
		rectangle.x = x;   // position object at new X coordinate
		if( getActor(rectangle) != null ) { // check to see if new X coordinate does not overlap
			rectangle.x = curX;
		}

		float curY = rectangle.y;
		rectangle.y = y;   // position object at new Y coordinate
		if( getActor(rectangle) != null ) { // check to see if new Y coordinate does not overlap
			rectangle.y = curY;
		}

		gameObject.moveTo(rectangle.x, rectangle.y);
	}
}


