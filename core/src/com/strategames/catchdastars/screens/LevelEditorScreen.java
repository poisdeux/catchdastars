package com.strategames.catchdastars.screens;

import java.util.ArrayList;
import java.util.Iterator;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.dialogs.ChangeWorldSizeDialog;
import com.strategames.catchdastars.dialogs.LevelEditorOptionsDialog;
import com.strategames.catchdastars.dialogs.ToolsPickerDialog;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Balloon;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.gameobject.types.Wall;
import com.strategames.engine.gameobject.types.WallVertical;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelEditorPreferences;
import com.strategames.engine.utils.LevelLoader;
import com.strategames.engine.utils.LevelLoader.OnLevelLoadedListener;
import com.strategames.engine.utils.LevelWriter;
import com.strategames.engine.utils.ScreenshotFactory;
import com.strategames.ui.dialogs.ButtonsDialog;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.dialogs.GameObjectConfigurationDialog;
import com.strategames.ui.helpers.Grid;
import com.strategames.ui.interfaces.ActorListener;
import com.strategames.ui.widgets.MenuButton;

public class LevelEditorScreen extends AbstractScreen 
implements OnLevelLoadedListener, ActorListener, GestureListener, Dialog.OnClickListener {

	private ButtonsDialog mainMenu;
	private Vector2 dragDirection;
	private float previousZoomDistance;
	private Actor uiElementHit;
	private Vector2 initialTouchPosition;

	private Actor actorTouched;

	private ArrayList<GameObject> selectedGameObjects;

	private Grid grid;
	private boolean snapToGrid;

	private Vector3 worldSize;

	private float cameraZoomInitial;
	private OrthographicCamera camera;

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
		super(game, null);

		this.initialTouchPosition = new Vector2();
		this.dragDirection = new Vector2();

		this.selectedGameObjects = new ArrayList<GameObject>();

		this.worldSize = game.getWorldSize(); 

		this.grid = new Grid(this.worldSize.x, this.worldSize.y);
	}

	@Override
	protected void setupUI(Stage stage) {
		getMultiplexer().addProcessor(new GestureDetector(this));
		this.snapToGrid = LevelEditorPreferences.snapToGridEnabled();
	}

	@Override
	protected void setupActors(Stage stage) {
		getMultiplexer().addProcessor(stage);
		this.camera = (OrthographicCamera) stage.getCamera();
		zoomCamera(this.camera);

		getGame().pauseGame();

		displayGrid(LevelEditorPreferences.displayGridEnabled());
		
		getGame().loadLevel(this);
	}

	@Override
	public void show() {
		resetStageActors();
		super.show();
	}

	@Override
	public void hide() {
		getGameCamera().zoom = this.cameraZoomInitial;
		super.hide();
	}

	@Override
	protected boolean handleBackNavigation() {
		if(saveLevel()) {
			return false;
		}
		
		//notify user saving failed
		ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Failed saving screenshot", getSkin());
		dialog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
				getGame().stopScreen();
			}
		});
		dialog.create();
		dialog.show();
				
		return true;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		//		Gdx.app.log("LevelEditorScreen", "touchDown float: (x,y)="+x+","+y+")");

		this.dragDirection.x = x;
		this.dragDirection.y = y;
		this.initialTouchPosition.x = x;
		this.initialTouchPosition.y = y;

		this.previousZoomDistance = 0f; // reset zoom distance
		this.state = States.NONE;

		Vector2 touchPosition = new Vector2(x, y);

		Stage stageUIActors = getStageUIActors();
		Stage stageActors = getStageActors();

		stageUIActors.screenToStageCoordinates(touchPosition);
		this.uiElementHit = stageUIActors.hit(touchPosition.x, touchPosition.y, false);
		touchPosition.set(x, y);   //reset vector as we use different metrics for actor stage
		stageActors.screenToStageCoordinates(touchPosition);
		Actor actor = stageActors.hit(touchPosition.x, touchPosition.y, false);
		//		Gdx.app.log("LevelEditorScreen", "touchDown touchPosition="+touchPosition);

		if( ( actor instanceof Wall ) && ( ((Wall) actor).isBorder() ) ) {
			return true;
		}

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
		if( this.actorTouched == null ) {
			return false;
		}

		GameObject gameObject = (GameObject) this.actorTouched;

		//If gameObject came from the menu create a new menu item
		if( gameObject.isMenuItem() ) {
			Vector2 v = gameObject.getInitialPosition();
			gameObject.setMenuItem(false);
			gameObject.setSaveToFile(true);
			getGame().getLevel().addGameObject(gameObject);
			addGameObjectToMenu(getStageActors(), gameObject, v.x, v.y);
		} 

		//Make sure Door is positioned on a Wall
		if( gameObject instanceof Door ) {
			//check if Door is at a Wall
			Door door = (Door) gameObject;
			Array<Actor> actors = getStageActors().getActorsOverlapping(door);
			Wall wall = null;
			for(int i = 0; i < actors.size; i++) {
				Actor actor = actors.get(i);
				if( actor instanceof Wall ) {
					if(! ((Wall) actor).isMenuItem() ) {
						wall = (Wall) actors.get(i);
					}
				}
			}
			if( wall == null ) {
				getGame().getLevel().removeDoor(door);
				gameObject.remove();
			} else {
				placeDoor(door, wall);
			}
		} else if( ! inGameArea(gameObject) ) {
			getGame().getLevel().removeGameObject(gameObject);
			gameObject.remove();
		}
		return true;
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
				getStageActors().screenToStageCoordinates(newPos);
				moveActor(getStageActors(), this.actorTouched, newPos);
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

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	/**
	 * Creates a copy of object and adds the copy to the game
	 * @param object
	 * @param xStage
	 * @param yStage
	 * @return game object added to the game
	 */
	public GameObject addGameObject(GameObject object, float xStage, float yStage) {
		GameObject copy = object.copy();
		copy.setMenuItem(false);
		copy.setInitialPosition(new Vector2(xStage, yStage));
		copy.moveTo(xStage, yStage);
		copy.initializeConfigurationItems();
		copy.setGame(getGame());
		copy.setupImage();
		copy.setupBody();
		getGame().getLevel().addGameObject(copy);
		return copy;
	}

	@Override
	public void onClick(Dialog dialog, int which) {
		if( dialog instanceof LevelEditorOptionsDialog ) {
			switch( which ) {
			case LevelEditorOptionsDialog.CHECKBOX_DISPLAYGRID:
				displayGrid(LevelEditorPreferences.displayGridEnabled());
				break;
			case LevelEditorOptionsDialog.CHECKBOX_SNAPTOGRID:
				this.snapToGrid = LevelEditorPreferences.snapToGridEnabled();
				break;
			}
		} else if (dialog instanceof GameObjectConfigurationDialog ) {
			switch( which ) {
			case GameObjectConfigurationDialog.BUTTON_COPY_CLICKED:
				GameObject original = ((GameObjectConfigurationDialog) dialog).getGameObject();
				GameObject copy = copyGameObject(original);
				((GameObjectConfigurationDialog) dialog).setGameObject(copy);
				break;
			case GameObjectConfigurationDialog.BUTTON_DELETE_CLICKED:
				GameObject gameObject = ((GameObjectConfigurationDialog) dialog).getGameObject();
				gameObject.remove();
				getGame().getLevel().removeGameObject(gameObject);
				dialog.remove();
				break;
			}
		} else if( dialog instanceof ChangeWorldSizeDialog ) {
			switch( which ) {
			case ChangeWorldSizeDialog.VALUE_CHANGED:
				ChangeWorldSizeDialog wDialog = (ChangeWorldSizeDialog) dialog;
				resizeWorld(wDialog.getHorizontalAmount(), wDialog.getVertialAmount());
				break;
			case ChangeWorldSizeDialog.BUTTON_CLOSE:
				dialog.remove();
				break;
			}
		}
	}

	@Override
	public void onLevelLoaded(Level level) {
		if( level == null ) {
			ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Error loading level", getSkin());
			dialog.setOnClickListener(this);
			dialog.create();
			dialog.show();
			return;
		}

		Array<Level> adjLevels = getAdjacentLevels(level);
		Array<Balloon> balloons = new Array<Balloon>();
		for(Level l : adjLevels) {
			balloons.addAll(getBalloons(l));
		}
		
		Game game = getGame();
		Stage stage = getStageActors();
		Array<GameObject> gameObjects = level.getGameObjects();
		if( (gameObjects != null) ) {
			for( GameObject gameObject : gameObjects ) {
				gameObject.initializeConfigurationItems();
				deselectGameObject(gameObject);
				game.addGameObject(gameObject, stage);
				
				/**
				 * If gameobject is a balloon remove a
				 * corresponding colored balloon from balloons
				 * any balloons left in balloons array should
				 * be added. Any additional balloons in current
				 * level should be set to new: setNew(true)
				 */
			}
		}

		Array<Door> doors = level.getDoors();
		if( (doors != null) ) {
			for( Door door : doors ) {
				door.initializeConfigurationItems();
				deselectGameObject(door);
				game.addGameObject(door, stage);
			}
		}
		
		//We setup menu last to make sure menu items are drawn on top
		setupMenu(getStageActors());
	}

	private Array<Level> getAdjacentLevels(Level level) {
		Array<Level> levels = new Array<Level>();
		int[] pos = level.getPosition();
		pos[0]+=1;
		levels.add(LevelLoader.loadLocalSync(pos));
		pos[0]-=2;
		levels.add(LevelLoader.loadLocalSync(pos));
		pos[0]+=1;
		pos[1]+=1;
		levels.add(LevelLoader.loadLocalSync(pos));
		pos[1]-=2;
		levels.add(LevelLoader.loadLocalSync(pos));
		return levels;
	}
	
	private Array<Balloon> getBalloons(Level level) {
		Array<Balloon> balloons = new Array<Balloon>();
		Array<GameObject> objects = level.getGameObjects();
		for(GameObject object : objects) {
			if( object instanceof Balloon ) {
				balloons.add((Balloon) object);
			}
		}
		return balloons;
	}
	
	@Override
	protected Timeline showAnimation() {
		return null;
	}

	@Override
	protected Timeline hideAnimation() {
		return null;
	}

	private void placeDoor(Door door, Wall wall) {
		Vector3 worldSize = getGame().getWorldSize();
		
		if( wall instanceof WallVertical ) {
			float wallX = wall.getX();
			door.moveTo(wallX, door.getY());
			
			if( wall.isBorder() ) {
				int[] currentLevelPosition = getGame().getLevelPosition();
				float middle = worldSize.x / 2f;
				if( wallX < middle ) { //If left border set next level to left
					door.setNextLevelPosition(currentLevelPosition[0] - 1, currentLevelPosition[1]);
				} else { // set next level to right
					door.setNextLevelPosition(currentLevelPosition[0] + 1, currentLevelPosition[1]);
				}
			}
		} else {
			float wallY = wall.getY();
			door.moveTo(door.getX(), wallY);
			
			if( wall.isBorder() ) {
				int[] currentLevelPosition = getGame().getLevelPosition();
				float middle = worldSize.x / 2f;
				if( wallY < middle ) { //If bottom border set next level to bottom
					door.setNextLevelPosition(currentLevelPosition[0], currentLevelPosition[1] - 1);
				} else { // set next level to top
					door.setNextLevelPosition(currentLevelPosition[0], currentLevelPosition[1] + 1);
				}
			}
		}
	}
	
	private GameObject copyGameObject(GameObject object) {
		GameObject copy = object.copy();
		float xDelta = 0;
		float yDelta = 0;
		float width = copy.getWidth();
		float height = copy.getHeight();
		if( width > height ) {
			yDelta = 0.06f; // empirically determined
		} else {
			xDelta = 0.06f; // empirically determined
		}

		copy.setPosition(copy.getX() + xDelta, copy.getY() + yDelta);
		copy.setGame(getGame());
		copy.setupImage();
		copy.setupBody();
		getGame().getLevel().addGameObject(copy);
		getStageActors().addActor(copy);
		deselectGameObject(object);
		selectGameObject(copy);
		return copy;
	}

	/**
	 * Positions camera to make room for menu
	 */
	private void zoomCamera(OrthographicCamera camera) {
		camera.position.set(camera.viewportWidth/2f, camera.viewportHeight/2f, 0f);
		this.cameraZoomInitial = camera.zoom;

		Vector2 maxObjectSize = getMaxObjectSize();
		//Add screenborder Wall as this is placed halfway the actual screenborder
		maxObjectSize.x += 0.6*Wall.WIDTH;
		maxObjectSize.y += 0.6*Wall.HEIGHT;

		boolean screenOK = false;
		while( ! screenOK ) {
			Vector3 screenSize = new Vector3(0f, 0f, 0f);
			camera.unproject(screenSize);

			/**
			 * We always set menu at the right as on Android the action bar will 
			 * trigger when trying to pick a game object
			 */
			if(Math.abs(screenSize.x) > maxObjectSize.x ) {
				screenOK = true;
			} else if ( camera.zoom > 3 ) {
				screenOK = true;
				//Print error
			} else {
				camera.zoom += 0.02; 
				camera.update();
			}
		}
	}

	private Vector2 getMaxObjectSize() {
		Array<GameObject> gameObjects = getGame().getAvailableGameObjects();

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

	private boolean saveLevel() {
		Level level = getGame().getLevel();
		if( level == null ) {
			return false;
		}
		LevelWriter.save(level);
		
		
		return ScreenshotFactory.saveScreenshot(getStageActors(), level);
	}

	/**
	 * Moves actor to position v.
	 * @param actor
	 * @param v new position of actor. Note: v will be changed, 
	 * so make a copy before calling this method if you wish to keep
	 * its value
	 */
	private void moveActor(Stage stage, Actor actor, Vector2 v) {
		GameObject gameObject = (GameObject) actor;

		if( this.snapToGrid ) {
			this.grid.map(v);
		}		

		if( ! ( actor instanceof Door ) ) {
			v = alignGameObject(stage, gameObject, v);
		}

		gameObject.moveTo(v.x, v.y);
	}

	private Vector2 alignGameObject(Stage stage, GameObject gameObject, Vector2 v) {
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
		if( stage.getActorsInRectangle(rectangle).size > 0 ) { // check to see if new X coordinate does not overlap
			rectangle.x = curX;
		} else {
			rectangle.x = v.x;
		}

		// position object at new Y coordinate adding half the amount we
		// subtracted from the height
		rectangle.y = v.y + 0.01f;
		if( stage.getActorsInRectangle(rectangle).size > 0 ) { // check to see if new Y coordinate does not overlap
			rectangle.y = curY;
		} else {
			rectangle.y = v.y;
		}
		return new Vector2(rectangle.x, rectangle.y);
	}

	private void displayGrid(boolean display) {
		if( display ) {
			getStageActors().addActor(this.grid);
		} else {
			this.grid.remove();
		}
	}

	private void createMainMenu() {
		final Stage stageUIActors = getStageUIActors();

		this.mainMenu = new ButtonsDialog(stageUIActors, getSkin(), ButtonsDialog.ORIENTATION.VERTICAL);

		this.mainMenu.add("Tools", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ToolsPickerDialog dialog = new ToolsPickerDialog(stageUIActors, getGame(), getSkin());
				dialog.create();
				dialog.setPosition(mainMenu.getX() - (dialog.getWidth()/2f), mainMenu.getY());
				dialog.setOnClickListener(LevelEditorScreen.this);
				dialog.show();
				mainMenu.hide();
			}
		});

		this.mainMenu.add("Options", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				LevelEditorOptionsDialog dialog = new LevelEditorOptionsDialog(stageUIActors, getSkin(), LevelEditorScreen.this);
				dialog.create();
				dialog.setPosition(mainMenu.getX() - (dialog.getWidth()/2f), mainMenu.getY());
				dialog.show();
				mainMenu.hide();
			}
		});

		this.mainMenu.add("Play level", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				saveLevel();
				Game game = getGame();
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
		GameObjectConfigurationDialog dialog = new GameObjectConfigurationDialog(getStageUIActors(), gameObject, getSkin());
		dialog.setOnClickListener(this);
		dialog.create();
		dialog.show();
	}

	private void setupMenu(Stage stage) {
		Game game = getGame();
		Array<GameObject> gameObjects = game.getAvailableGameObjects();

		Vector2 viewSize = game.getViewSize();

		/**
		 * We always set menu at the right as otherwise the action bar will 
		 * trigger on Android when trying to pick a game object
		 */
		float delta = stage.getHeight() / ( gameObjects.size + 1 );
		float x = (float) (viewSize.x + 0.6*Wall.WIDTH);
		float y = viewSize.y - Wall.HEIGHT;

		Stage stageUIActors = getStageUIActors();

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
		GameObject gameObject = object.copy();
		gameObject.setSaveToFile(false);
		gameObject.setMenuItem(true);
		deselectGameObject(gameObject);
		gameObject.moveTo(x, y);
		gameObject.setInitialPosition(new Vector2(x, y));
		gameObject.setupImage();
		stage.addActor(gameObject);
	}

	@Override
	public void onTap(Actor actor) {
		if( actor instanceof MenuButton ) {
			if( this.mainMenu == null ) {
				createMainMenu();
				this.mainMenu.setPosition(actor.getX() - this.mainMenu.getWidth(), 
						actor.getY() - ( this.mainMenu.getHeight() - actor.getHeight() ) );
			}

			if( this.mainMenu.isVisible() ) {
				this.mainMenu.hide();
			} else {
				this.mainMenu.show();
			}
		}
	}

	@Override
	public void onLongPress(Actor actor) {

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

	private void resizeWorld(int w, int h) {
		Game game = getGame();
		Vector2 viewSize = game.getViewSize();
		Vector3 worldSize = game.getWorldSize();
		game.setWorldSize(new Vector3(viewSize.x * w, viewSize.y * h, worldSize.z));
		OrthographicCamera camera = (OrthographicCamera) getStageActors().getCamera();
		camera.viewportWidth =  viewSize.x * w;
		camera.viewportHeight =  viewSize.y * h;
	}


}


