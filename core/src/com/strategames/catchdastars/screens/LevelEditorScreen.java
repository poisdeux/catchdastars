package com.strategames.catchdastars.screens;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.actors.Wall;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.LevelEditorPreferences;
import com.strategames.catchdastars.utils.LevelLoader.OnLevelLoadedListener;
import com.strategames.catchdastars.utils.LevelWriter;
import com.strategames.catchdastars.utils.ScreenBorder;
import com.strategames.interfaces.ButtonListener;
import com.strategames.ui.Grid;
import com.strategames.ui.dialogs.ButtonsDialog;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.GameObjectConfigurationDialog;
import com.strategames.ui.dialogs.LevelEditorOptionsDialog;
import com.strategames.ui.dialogs.ToolsPickerDialog;
import com.strategames.ui.widgets.MenuButton;
import com.strategames.ui.widgets.RectangleImage;

public class LevelEditorScreen extends AbstractScreen implements OnLevelLoadedListener, ButtonListener, GestureListener, Dialog.OnClickListener {

	private ButtonsDialog mainMenu;
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

	private Vector3 worldSize;

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

		this.game = game;

		this.testGame = false;

		this.initialTouchPosition = new Vector2();
		this.dragDirection = new Vector2();

		this.selectedGameObjects = new ArrayList<GameObject>();

		this.worldSize = this.game.getWorldSize(); 

		this.preferences = new LevelEditorPreferences();
		this.snapToGrid = this.preferences.snapToGridEnabled();

		this.grid = new Grid(this.worldSize.x, this.worldSize.y);

		this.rectangleImage = new RectangleImage();
		this.rectangleImage.setColor(1f, 0.25f, 0.25f, 0.5f);

		getMultiplexer().addProcessor(new GestureDetector(this));

		setCamera();
	}

	@Override
	protected void setupUI(Stage stage) {
		stage.addActor(this.rectangleImage);
		displayGrid(this.preferences.displayGridEnabled());
	}

	@Override
	protected void setupActors(Stage stage) {
		getMultiplexer().addProcessor(stage);

		Array<Actor> actors = stage.getActors();
		if( actors.size == 0 ) {
			ScreenBorder.create(this.game);
		}

		for( Actor actor : actors ) {
			GameObject object = (GameObject) actor;
			object.initializeConfigurationItems();
			deselectGameObject(object);
		}

		//This is added to the actor stage as we use
		//game objects in the menu
		setupMenu(stage);

		this.game.pauseGame();
	}

	@Override
	public void show() {
		super.show();

		this.game.loadLevel(this);
	}

	@Override
	protected boolean handleBackNavigation() {
		saveLevel();
		return false;
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if( testGame ) {
			this.game.update(delta, super.stageActors);
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

		super.stageUIActors.screenToStageCoordinates(touchPosition);
		this.uiElementHit = super.stageUIActors.hit(touchPosition.x, touchPosition.y, false);
		touchPosition.set(x, y);   //reset vector as we use different metrics for actor stage
		super.stageActors.screenToStageCoordinates(touchPosition);
		Actor actor = super.stageActors.hit(touchPosition.x, touchPosition.y, false);
		//		Gdx.app.log("LevelEditorScreen", "touchDown touchPosition="+touchPosition);

		this.tap.setActor(actor);
		this.actorTouched = actor;

		if( actor != null ) { // actor selected
			deselectAllGameObjects();
			selectGameObject((GameObject) this.actorTouched);
		} else if( this.uiElementHit == null ) { // empty space selected
			deselectAllGameObjects();
		}

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if( this.actorTouched != null ) {
			GameObject gameObject = (GameObject) this.actorTouched;

			//If menu item create new menu item at initial position
			if( gameObject.isMenuItem() ) {
				Vector2 v = gameObject.getInitialPosition();
				if( inGameArea(gameObject) ) {
					gameObject.setMenuItem(false);
					gameObject.setSaveToFile(true);
					addGameObjectToMenu(this.stageActors, gameObject, v.x, v.y);
				} else {
					//return menu item to its original position
					gameObject.moveTo(v.x, v.y);
				}
			} else {
				if( ! inGameArea(gameObject) ) {
					gameObject.remove();
				}
			}
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

			if( this.actorTouched != null ) {
				Vector2 newPos = new Vector2(screenX, screenY);
				super.stageActors.screenToStageCoordinates(newPos);
				moveActor(this.actorTouched, newPos);
			}

			return true;

		} else {

			return false;

		}
	}
	
	@Override
	public boolean tap(final float x, final float y, int count, int button) {
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
	public boolean longPress(float x, float y) {
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

	/**
	 * Creates a copy of object and adds the copy to the game
	 * @param object
	 * @param xStage
	 * @param yStage
	 * @return game object added to the game
	 */
	public GameObject addGameObject(GameObject object, float xStage, float yStage) {
		GameObject copy = object.createCopy();
		copy.setMenuItem(false);
		copy.setInitialPosition(new Vector2(xStage, yStage));
		copy.moveTo(xStage, yStage);
		copy.initializeConfigurationItems();
		getGame().addGameObject(copy);
		//		super.stageActors.addActor(copy);
		return copy;
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
		} else if (dialog instanceof GameObjectConfigurationDialog ) {
			switch( which ) {
			case GameObjectConfigurationDialog.BUTTON_COPY_CLICKED:
				GameObject copy = ((GameObjectConfigurationDialog) dialog).getGameObject().createCopy();
				Vector2 stageCoords = stageActors.screenToStageCoordinates(new Vector2(copy.getX(), copy.getY()));
				copy.setPosition(stageCoords.x, stageCoords.y);
				getGame().addGameObject(copy);
				stageActors.addActor(copy);
				deselectGameObject(copy);
				break;
			case GameObjectConfigurationDialog.BUTTON_DELETE_CLICKED:
				((GameObjectConfigurationDialog) dialog).getGameObject().remove();
				dialog.remove();
				break;
			}
		}
	}

	/**
	 * Positions camera to make room for menu
	 */
	private void setCamera() {		
		Stage stage = getStageActors();
		OrthographicCamera camera = (OrthographicCamera) stage.getCamera();
		camera.position.set(worldSize.x/2f, worldSize.y/2f, 0f);

		boolean screenOK = false;
		while( ! screenOK ) {
			camera.zoom += 0.02; 
			camera.update();
			Vector3 screenSize = new Vector3(0f, Gdx.graphics.getHeight(), 0f);
			camera.unproject(screenSize);

			Vector2 maxObjectSize = getMaxObjectSize();
			//Add screenborder Wall as this is placed halfway the actual screenborder
			maxObjectSize.x += 0.6*Wall.WIDTH;
			maxObjectSize.y += 0.6*Wall.HEIGHT;

			//			Gdx.app.log("LevelEditorScreen", "After camera.zoom="+camera.zoom+", screenSize="+screenSize+
			//					", maxObjectSize="+maxObjectSize);

			/**
			 * We always set menu at the right as on Android the action bar will 
			 * trigger when trying to pick a game object
			 */
			if(Math.abs(screenSize.x) > maxObjectSize.x ) {
				screenOK = true;
			} else if ( camera.zoom > 3 ) {
				screenOK = true;
				//Print error
			}
		}
	}

	private Vector2 getMaxObjectSize() {
		ArrayList<GameObject> gameObjects = this.game.getAvailableGameObjects();

		Vector2 maxObjectSize = new Vector2(0, 0);

		for( GameObject object : gameObjects ) {
			if( object.getWidth() > maxObjectSize.x ) {
				maxObjectSize.x = object.getWidth();
			}

			if( object.getHeight() > maxObjectSize.y ) {
				maxObjectSize.y = object.getHeight();
			}
		}

		return maxObjectSize;
	}

	private void selectGameObject(GameObject gameObject) {
		if( gameObject == null) return;

		gameObject.setColor(1f, 1f, 1f, 1.0f);
		this.selectedGameObjects.add(gameObject);
	}

	private void deselectGameObject(GameObject gameObject) {
		if( gameObject == null) return;

		this.selectedGameObjects.remove(gameObject);

		gameObject.setColor(0.7f, 0.7f, 0.7f, 1.0f);
	}

	private void deselectAllGameObjects() {
		Iterator<GameObject> itr = this.selectedGameObjects.iterator();
		while(itr.hasNext()) {
			GameObject object = (GameObject) itr.next();
			itr.remove();
			deselectGameObject(object);
		}
		this.selectedGameObjects.clear();
	}

	private void saveLevel() {
		Game game = getGame();
		Level level = game.getLevel();
		level.setGameObjects(game.getGameObjects());
		LevelWriter.save(level);
	}

	private Actor getActor(Rectangle rectangle) {
		Array<Actor> actors = super.stageActors.getActors();
		for(Actor actor : actors) {
			if( this.selectedGameObjects.contains(actor) ) continue;
			Rectangle rectangleActor = new Rectangle(actor.getX(), actor.getY(), 
					actor.getWidth(), actor.getHeight());
			if( rectangle.overlaps(rectangleActor) ) return actor;
		}
		return null;
	}

	/**
	 * Moves actor to position v.
	 * @param actor
	 * @param v new position of actor. Note: v will be changed, 
	 * so make a copy before calling this method if you wish to keep
	 * its value
	 */
	private void moveActor(Actor actor, Vector2 v) {
		GameObject gameObject = (GameObject) actor;

		if( this.snapToGrid ) {
			this.grid.map(v);
		}		

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
		rectangle.x = v.x + 0.01f;   
		if( getActor(rectangle) != null ) { // check to see if new X coordinate does not overlap
			rectangle.x = curX;
		} else {
			rectangle.x = v.x;
		}

		// position object at new Y coordinate adding half the amount we
		// subtracted from the height
		rectangle.y = v.y + 0.01f;
		if( getActor(rectangle) != null ) { // check to see if new Y coordinate does not overlap
			rectangle.y = curY;
		} else {
			rectangle.y = v.y;
		}

		gameObject.moveTo(rectangle.x, rectangle.y);
	}

	private void displayGrid(boolean display) {
		if( display ) {
			super.stageActors.addActor(this.grid);
		} else {
			this.grid.remove();
		}
	}

	private void createMainMenu() {
		this.mainMenu = new ButtonsDialog(stageUIActors, "Main menu", getSkin(), ButtonsDialog.ORIENTATION.VERTICAL);

		this.mainMenu.add("Tools", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ToolsPickerDialog dialog = new ToolsPickerDialog(stageUIActors, getGame(), getSkin());
				dialog.create();
				dialog.setPosition(mainMenu.getX(), mainMenu.getY());
				mainMenu.hide();
			}
		});

		this.mainMenu.add("Options", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				LevelEditorOptionsDialog dialog = new LevelEditorOptionsDialog(stageUIActors, getSkin(), preferences, LevelEditorScreen.this);
				dialog.create();
				dialog.setPosition(mainMenu.getX(), mainMenu.getY());
				mainMenu.hide();
			}
		});

		this.mainMenu.add("Play level", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				saveLevel();
				game.startLevel(game.getLevel());
				mainMenu.hide();
			}
		});

		this.mainMenu.setPositiveButton("Save", new OnClickListener() {

			@Override
			public void onClick(Dialog dialog, int which) {
				saveLevel();
				mainMenu.hide();
			}
		});
		this.mainMenu.setNegativeButton("Quit", new OnClickListener() {

			@Override
			public void onClick(Dialog dialog, int which) {
				saveLevel();
				mainMenu.hide();
				getGame().stopScreen();
			}
		});

		this.mainMenu.create();
	}

	private void showGameObjectCongfigurationDialog(GameObject gameObject) {
		GameObjectConfigurationDialog dialog = new GameObjectConfigurationDialog(stageUIActors, gameObject, getSkin());
		dialog.setOnClickListener(this);
		dialog.create();
		dialog.show();
	}

	private void setupMenu(Stage stage) {
		ArrayList<GameObject> gameObjects = this.game.getAvailableGameObjects();

		Vector3 worldSize = this.game.getWorldSize();

		/**
		 * We always set menu at the right as on Android the action bar will 
		 * trigger when trying to pick a game object
		 */
		float delta = stage.getHeight() / ( gameObjects.size() + 1 );
		float x = (float) (worldSize.x + 0.6*Wall.WIDTH);
		float y = worldSize.y - Wall.HEIGHT;

		Vector3 screenCoords =  stage.getCamera().project(new Vector3(x, Wall.HEIGHT, 0f));
		Vector3 stageUICoords = stageUIActors.getCamera().unproject(screenCoords);
		//Add menu button
		MenuButton menuButton = new MenuButton();
		menuButton.setListener(this);
		menuButton.setPosition(stageUICoords.x, stageUICoords.y);
		stageUIActors.addActor(menuButton);

		y-=delta;

		for(GameObject object : gameObjects ) {
			addGameObjectToMenu(stage, object, x, y);
			y -= delta;
		}
	}

	/**
	 * Creates a copy of object and adds copy as menu item at position x,y
	 * @param stage
	 * @param object
	 * @param x
	 * @param y
	 */
	private void addGameObjectToMenu(Stage stage, GameObject object, float x, float y) {
		GameObject gameObject = object.createCopy();
		gameObject.setSaveToFile(false);
		gameObject.setMenuItem(true);
		deselectGameObject(gameObject);
		gameObject.moveTo(x, y);
		gameObject.setInitialPosition(new Vector2(x, y));
		stage.addActor(gameObject);
	}

	@Override
	public void onTap(Button button) {
		if( button instanceof MenuButton ) {
			if( this.mainMenu == null ) {
				createMainMenu();
				this.mainMenu.setPosition(button.getX() - this.mainMenu.getWidth(), 
						button.getY() - ( this.mainMenu.getHeight() - button.getHeight() ) );
			}

			if( this.mainMenu.isVisible() ) {
				this.mainMenu.hide();
			} else {
				this.mainMenu.show();
			}
		}
	}

	@Override
	public void onLongPress(Button button) {

	}

	private boolean inGameArea(GameObject gameObject) {
		float x = gameObject.getX();
		float y = gameObject.getY();
		if( ( x < 0 ) || ( x > worldSize.x ) || 
				( y < 0 ) || ( y > worldSize.y ) ) {
			return false;
		}
		return true;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public void onLevelLoaded(Level level) {
		this.game.initialize();
	}
}


