package com.strategames.catchdastars.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.LevelEditorPreferences;
import com.strategames.ui.ButtonsDialog;
import com.strategames.ui.Dialog;
import com.strategames.ui.Dialog.OnClickListener;
import com.strategames.ui.GameObjectConfigurationDialog;
import com.strategames.ui.GameObjectPickerDialog;
import com.strategames.ui.Grid;
import com.strategames.ui.LevelEditorOptionsDialog;
import com.strategames.ui.RectangleImage;
import com.strategames.ui.ToolsPickerDialog;

public class LevelEditorScreen extends AbstractScreen implements GestureListener, Dialog.OnClickListener {

	private Vector2 longPressPosition;
	private Vector2 dragDirection;
	private float previousZoomDistance;
	private Actor uiElementHit;
	private Game game;
	private boolean testGame;
	private LevelEditorPreferences preferences;
	private boolean snapToGrid;
	private Vector2 initialTouchPosition;
	
	private Actor actorTouched;
	
	private ArrayList<GameObject> selectedGameObjects;

	private RectangleImage rectangleImage;

	private Grid grid;

	private enum States {
		ZOOM, LONGPRESS, DRAG, NONE
	}

	private States state;

	private class Tap {
		private long tapTime1;
		private long tapTime2;
		private Actor actor;

		private int tapDelay = 200;

		public Tap() {
		}

		public void tap() {
			tapTime1 = tapTime2;
			tapTime2 = System.currentTimeMillis();
		}

		public boolean doubleTapped() {
			return ( tapTime2 - tapTime1 ) < tapDelay;
		}

		/**
		 * Set the actor to monitor for double tapping.
		 * <br/>
		 * Note that this resets the tap time if the actor being set is different from the one currently being monitored
		 * @param actor
		 */
		public void setActor(Actor actor) {
			if( actor == this.actor ) {
				return;
			}

			tapTime1 = -1 * (tapDelay + 1); // make sure doubleTapped is not true when setting the actor
			tapTime2 = 0;
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

		this.initialTouchPosition = new Vector2();
		this.longPressPosition = new Vector2();
		this.dragDirection = new Vector2();

		this.selectedGameObjects = new ArrayList<GameObject>();

		this.preferences = new LevelEditorPreferences();
		this.snapToGrid = this.preferences.snapToGridEnabled();

		Vector2 worldSize = game.getWorldSize();
		this.grid = new Grid();
		this.grid.calculateGridSize(worldSize.x, worldSize.y);

		this.rectangleImage = new RectangleImage();
		this.rectangleImage.setColor(1f, 0.25f, 0.25f, 0.5f);

		getMultiplexer().addProcessor(new GestureDetector(this));
	}

	@Override
	protected void setupUI(Stage stage) {
		displayGrid(this.preferences.displayGridEnabled());
		stage.addActor(this.rectangleImage);
	}

	@Override
	protected void setupActors(Stage stage) {
		this.game.setupStage(stage);
		getMultiplexer().addProcessor(stage);

		Array<Actor> actors = stage.getActors();
		for( Actor actor : actors ) {
			GameObject object = (GameObject) actor;
			object.initializeConfigurationItems();
			deselectGameObject(object);
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
		Gdx.app.log("LevelEditorScreen", "touchDown float: (x,y)="+x+","+y+")");

		if( this.testGame ) { //do not handle event in game mode
			return false;
		}

		this.dragDirection.x = x;
		this.dragDirection.y = y;
		this.initialTouchPosition.x = x;
		this.initialTouchPosition.y = y;
		this.rectangleImage.setPosition(x, y);
		this.rectangleImage.setWidth(0f);
		this.rectangleImage.setHeight(0f);

		this.previousZoomDistance = 0f; // reset zoom distance
		this.state = States.NONE;

		Vector2 touchPosition = new Vector2(x, y);

		Stage stage = getStageUIElements();
		stage.screenToStageCoordinates(touchPosition);
		this.uiElementHit = stage.hit(touchPosition.x, touchPosition.y, false);
		touchPosition.set(x, y);   //reset vector as we use different metrics for actor stage
		stage = getStageActors();
		stage.screenToStageCoordinates(touchPosition);
		Actor actor = stage.hit(touchPosition.x, touchPosition.y, false);
		
		tap.setActor(actor);
		this.actorTouched = actor;
		
		if( actor != null ) { // actor selected
//			Vector2 testConversion = new Vector2(touchPosition);
//			stage.stageToScreenCoordinates(testConversion);
//			testConversion.y = Gdx.app.getGraphics().getHeight() - testConversion.y;
//			Gdx.app.log("LevelEditorScreen", "touchDown: this.actorTouched(x,y)=("
//			+this.actorTouched.getX()+","+this.actorTouched.getY()+"), touchPosition="+touchPosition+
//			"\ntestConversion="+testConversion);
			
			selectGameObject((GameObject) actor);
		} else if( this.uiElementHit == null ) { // empty space selected
			for( GameObject gameObject : this.selectedGameObjects ) {
				deselectGameObject(gameObject);
			}
			this.selectedGameObjects.clear();
		}

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if( this.rectangleImage.getWidth() > 0 ) { // check if we did a selection
			Rectangle rectangle = new Rectangle(this.rectangleImage.getX(),
					this.rectangleImage.getY(), 
					this.rectangleImage.getWidth(),
					this.rectangleImage.getHeight());
			ArrayList<Actor> selection = getActors(rectangle);
			for( Actor actor : selection ) {
				selectGameObject((GameObject) actor);
			}
			this.rectangleImage.setWidth(0f);
			this.rectangleImage.setHeight(0f);
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if( ( this.state == States.NONE ) || 
				( this.state == States.DRAG ) ) {

			this.state = States.DRAG;

			if( this.uiElementHit != null ) {
				return false;
			}

			if( this.selectedGameObjects.size() > 0 ) {
				Vector2 moveDirection = new Vector2(screenX, screenY);
				Vector2 stageCoords = new Vector2(this.actorTouched.getX(), this.actorTouched.getY());
				getStageActors().stageToScreenCoordinates(stageCoords);
				stageCoords.y = super.screenHeight - stageCoords.y;
				moveDirection.sub(stageCoords);
				Gdx.app.log("LevelEditorScreen", "touchDragged: after moveDirection="+moveDirection+
						", stageCoords="+stageCoords);
//				this.dragDirection.sub(screenX, screenY);
				
				for( GameObject gameObject : this.selectedGameObjects ) {
					moveActor(gameObject, moveDirection);
				}
			} else {
				float width = screenX - this.initialTouchPosition.x;
				float height = this.initialTouchPosition.y - screenY;
				this.rectangleImage.setWidth(width);
				this.rectangleImage.setHeight(height);
			}

			return true;

		} else {

			return false;

		}
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

		this.tap.tap();

		GameObject gameObject = (GameObject) this.tap.getActor();

		if( ( gameObject != null ) && ( this.uiElementHit == null ) ){
			if( tap.doubleTapped() ) {
				showGameObjectCongfigurationDialog(gameObject);
				return true;
			} 

		}

		return false;
	}

	@Override
	public boolean longPress(final float x, final float y) {
		/**
		 * Used to show generic configuration window
		 */
		if( this.testGame ) {
			return false;
		}

		if( this.state == States.NONE ) {
			this.state = States.LONGPRESS;

			this.longPressPosition.x = x;
			this.longPressPosition.y = y;

			if( this.uiElementHit != null ) return false;

			showMainMenu();

		}
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
		if( this.testGame ) { //do not handle event in game mode
			return false;
		}

		if( ( this.state == States.ZOOM) || 
				( this.state == States.NONE ) ) {
			this.state = States.ZOOM;
			if( this.previousZoomDistance == 0 ) {
				this.previousZoomDistance = distance;
			}

			if( this.selectedGameObjects.size() == 0 ) return false;

			if( distance > this.previousZoomDistance ) {
				for( GameObject gameObject : this.selectedGameObjects ) {
					gameObject.increaseSize();
				}
			} else if( distance < this.previousZoomDistance ) {
				for( GameObject gameObject : this.selectedGameObjects ) {
					gameObject.decreaseSize();
				}
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

	public void addGameObject(GameObject object) {
		Stage stage = getStageActors();
		GameObject copy = object.createCopy();
		Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(this.longPressPosition));
		copy.setPosition(stageCoords.x, stageCoords.y);
		copy.initializeConfigurationItems();
		getGame().addGameObject(copy);
		stage.addActor(copy);
		deselectGameObject(copy);
	}

	@Override
	public void onClick(Dialog dialog, int which) {
		if( dialog instanceof LevelEditorOptionsDialog ) {
			switch( which ) {
			case LevelEditorOptionsDialog.CHECKBOX_DISPLAYGRID:
				displayGrid(this.preferences.displayGridEnabled());
				break;
			case LevelEditorOptionsDialog.CHECKBOX_SNAPTOGRID:
				this.snapToGrid = this.preferences.snapToGridEnabled();
				break;
			}
		} else if( dialog instanceof GameObjectPickerDialog ) {
			switch( which ) {
			case GameObjectPickerDialog.BUTTON_GAMEOBJECTSELECTED:
				addGameObject(((GameObjectPickerDialog) dialog).getSelectedGameObject());
				break;
			}
		}
	}

	private void selectGameObject(GameObject gameObject) {
		if( gameObject == null) return;

		gameObject.setColor(1f, 1f, 1f, 1.0f);
		if( ! this.selectedGameObjects.contains(gameObject) ) {
			this.selectedGameObjects.add(gameObject);
		}
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
			if( this.selectedGameObjects.contains(actor) ) continue;
			Rectangle rectangleActor = new Rectangle(actor.getX(), actor.getY(), 
					actor.getWidth(), actor.getHeight());
			if( rectangle.overlaps(rectangleActor) ) return actor;
		}
		return null;
	}

	private ArrayList<Actor> getActors(Rectangle rectangle) {
		ArrayList<Actor> actorsInRectangle = new ArrayList<Actor>();

		Array<Actor> actors = getStageActors().getActors();
		for(Actor actor : actors) {
			if( this.selectedGameObjects.contains(actor) ) continue;
			Rectangle rectangleActor = new Rectangle(actor.getX(), actor.getY(), 
					actor.getWidth(), actor.getHeight());
			if( rectangle.overlaps(rectangleActor) ) {
				actorsInRectangle.add(actor);
			}
		}
		return actorsInRectangle;
	}

	private void moveActor(Actor actor, Vector2 v) {
		Stage stage = getStageActors();
		
		GameObject gameObject = (GameObject) actor;

		Vector2 newPos = new Vector2(gameObject.getX(), gameObject.getY());
		stage.stageToScreenCoordinates(newPos);
		newPos.y = super.screenHeight - newPos.y;
		newPos.add(v);
		
//		Gdx.app.log("LevelEditorScreen", "moveActor: gameObject="+gameObject.getName()+", newPos="+newPos);
		
		if( this.snapToGrid ) {
			this.grid.map(newPos);
		}		
//		Gdx.app.log("LevelEditorScreen", "moveActor: gameObject="+gameObject.getName()+", snapToGrid v="+v);
		stage.screenToStageCoordinates(newPos);
		
		Rectangle rectangle = gameObject.getBoundingRectangle();
		float curX = rectangle.x;
		float curY = rectangle.y;

		//Make rectangle a bit smaller inside object to allow objects to
		//be placed adjacent to each other. This is especially a problem
		//when using snap to grid.
		rectangle.width -= 0.02f;
		rectangle.height -= 0.02f;

		// position object at new X coordinate adding half the amount we
		// subtracted from the width
		rectangle.x = newPos.x + 0.01f;   
		if( getActor(rectangle) != null ) { // check to see if new X coordinate does not overlap
			rectangle.x = curX;
		} else {
			rectangle.x = newPos.x;
		}

		// position object at new Y coordinate adding half the amount we
		// subtracted from the height
		rectangle.y = newPos.y + 0.01f;
		if( getActor(rectangle) != null ) { // check to see if new Y coordinate does not overlap
			rectangle.y = curY;
		} else {
			rectangle.y = newPos.y;
		}

		gameObject.moveTo(rectangle.x, rectangle.y);
	}

	private void displayGrid(boolean display) {
		if( display ) {
			getStageUIElements().addActor(this.grid);
		} else {
			this.grid.remove();
		}
	}

	private void showMainMenu() {
		ButtonsDialog dialog = new ButtonsDialog(getGame(), getSkin(), this);

		dialog.add("Add game object", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				showGameObjectPickerDialog();
			}
		});

		dialog.add("Tools", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				showToolsDialog();
			}
		});

		dialog.add("Options", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				showOptionsDialog();
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

	private void showGameObjectPickerDialog() {
		GameObjectPickerDialog dialog = new GameObjectPickerDialog(getGame(), getSkin(), this);
		dialog.setPositiveButton("Close", this);
		dialog.setNegativeButton("Quit", this);
		dialog.show(getStageUIElements());
	}

	private void showToolsDialog() {
		ToolsPickerDialog tDialog = new ToolsPickerDialog(getGame(), getSkin());
		tDialog.show(getStageUIElements());
	}

	private void showOptionsDialog() {
		LevelEditorOptionsDialog tDialog = new LevelEditorOptionsDialog(getSkin(), this.preferences, this);
		tDialog.show(getStageUIElements());
	}

	private void showGameObjectCongfigurationDialog(GameObject gameObject) {
		GameObjectConfigurationDialog dialog = new GameObjectConfigurationDialog(gameObject, getSkin());
		dialog.addButton("Copy " + gameObject.getName(), new OnClickListener() {

			@Override
			public void onClick(Dialog dialog, int which) {
				Stage stage = getStageActors();
				GameObject copy = ((GameObjectConfigurationDialog) dialog).getGameObject().createCopy();
				Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(copy.getX(), copy.getY()));
				copy.setPosition(stageCoords.x, stageCoords.y);
				getGame().addGameObject(copy);
				stage.addActor(copy);
				deselectGameObject(copy);
			}
		});
		dialog.addButton("Delete " + gameObject.getName(), new OnClickListener() {

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
	}
}


