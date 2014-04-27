package com.strategames.catchdastars.screens;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
import com.strategames.catchdastars.utils.ScreenBorder;
import com.strategames.interfaces.ButtonListener;
import com.strategames.ui.Grid;
import com.strategames.ui.dialogs.ButtonsDialog;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.GameObjectConfigurationDialog;
import com.strategames.ui.dialogs.GameObjectPickerDialog;
import com.strategames.ui.dialogs.LevelEditorOptionsDialog;
import com.strategames.ui.dialogs.ToolsPickerDialog;
import com.strategames.ui.widgets.MenuButton;
import com.strategames.ui.widgets.RectangleImage;

public class LevelEditorScreen extends AbstractScreen implements ButtonListener, GestureListener, Dialog.OnClickListener {

	private enum MenuPosition { TOP, BOTTOM, LEFT, RIGHT };
	private MenuPosition menuPosition;
	private ButtonsDialog mainMenu;
	private Vector2 longPressPosition;
	private Vector2 dragDirection;
	private float previousZoomDistance;
	private Game game;
	private boolean testGame;
	private LevelEditorPreferences preferences;
	private boolean snapToGrid;
	private Vector2 initialTouchPosition;

	private Actor actorTouched;

	private ArrayList<GameObject> selectedGameObjects;

	private RectangleImage rectangleImage;

	private Grid grid;

	private Vector2 worldSize;

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
		//		Gdx.app.log("LevelEditorScreen", "LevelEditorScreen");

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
		setupMenu(stage);
	}

	@Override
	protected void setupActors(Stage stage) {
		this.game.setupStage(stage);
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

		displayGrid(this.preferences.displayGridEnabled());

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
			this.game.update(delta, super.stageActors);
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Gdx.app.log("LevelEditorScreen", "touchDown int: (x,y)="+screenX+","+screenY+")");
		Vector2 touchPosition = new Vector2(screenX, screenY);

		super.stageUIActors.screenToStageCoordinates(touchPosition);
		this.initialTouchPosition.x = touchPosition.x;
		this.initialTouchPosition.y = touchPosition.y;
		Actor menuTouched = super.stageUIActors.hit(touchPosition.x, touchPosition.y, false);
		
		this.dragDirection.x = screenX;
		this.dragDirection.y = screenY;
		this.initialTouchPosition.x = screenX;
		this.initialTouchPosition.y = screenY;
		this.rectangleImage.setPosition(screenX, screenY);
		this.rectangleImage.setWidth(0f);
		this.rectangleImage.setHeight(0f);

		this.previousZoomDistance = 0f; // reset zoom distance
		this.state = States.NONE;

		touchPosition = new Vector2(screenX, screenY);
		super.stageActors.screenToStageCoordinates(touchPosition);
		Actor actor = super.stageActors.hit(touchPosition.x, touchPosition.y, false);

		tap.setActor(actor);
		this.actorTouched = actor;

		if( this.actorTouched != null ) { // actor selected
			Gdx.app.log("LevelEditorScreen", "touchDown int: actor touched");
			deselectAllGameObjects();
			selectGameObject((GameObject) this.actorTouched);
		} else if( menuTouched == null ){ // empty space selected
			Gdx.app.log("LevelEditorScreen", "touchDown int: empty space touched");
			deselectAllGameObjects();
		} else {
			Gdx.app.log("LevelEditorScreen", "touchDown int: menu touched");
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Gdx.app.log("LevelEditorScreen", "touchDown float: (x,y)="+x+","+y+")");

		if( this.testGame ) { //do not handle event in game mode
			return false;
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
		} else if( this.actorTouched != null ) {
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
		Gdx.app.log("LevelEditorScreen", "touchDragged int: (x,y)="+screenX+","+screenY+")");
		if( ( this.state == States.NONE ) || 
				( this.state == States.DRAG ) ) {

			this.state = States.DRAG;

			if( ( this.actorTouched != null ) &&  ( this.selectedGameObjects.size() > 0 ) ) {
				Vector2 moveDirection = new Vector2(screenX, screenY);
				Vector2 actorCoords = new Vector2(this.actorTouched.getX(), this.actorTouched.getY());
				super.stageActors.screenToStageCoordinates(moveDirection);
				moveDirection.sub(actorCoords);

				//				Gdx.app.log("LevelEditorScreen", "touchDragged: after moveDirection="+moveDirection+
				//						", stageCoords="+stageCoords);

				for( GameObject gameObject : this.selectedGameObjects ) {
					moveGameObject(gameObject, moveDirection);
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

		if( gameObject != null ){
			if( tap.doubleTapped() ) {
				showGameObjectCongfigurationDialog(gameObject);
				return true;
			} 

		}

		return false;
	}

	@Override
	public boolean longPress(final float x, final float y) {
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
		copy.setInitialPosition(new Vector2(xStage, yStage));
		copy.moveTo(xStage, yStage);
		copy.initializeConfigurationItems();
		getGame().addGameObject(copy);
		//		super.stageActors.addActor(copy);
		return copy;
	}

	@Override
	public void onClick(Dialog dialog, int which) {
		Gdx.app.log("LevelEditorScreen", "onClick");
		if( dialog instanceof LevelEditorOptionsDialog ) {
			switch( which ) {
			case LevelEditorOptionsDialog.CHECKBOX_DISPLAYGRID:
				displayGrid(this.preferences.displayGridEnabled());
				break;
			case LevelEditorOptionsDialog.CHECKBOX_SNAPTOGRID:
				this.snapToGrid = this.preferences.snapToGridEnabled();
				break;
			}
		} else if ( dialog instanceof GameObjectPickerDialog ) {
			Gdx.app.log("LevelEditorScreen", "onClick GameObjectPickerDialog");
			GameObject gameObject = ((GameObjectPickerDialog) dialog).getSelectedGameObject();
			deselectAllGameObjects();
			selectGameObject(gameObject);
			Vector2 touchPosition = new Vector2(this.initialTouchPosition.x, this.initialTouchPosition.y);
			Gdx.app.log("LevelEditorScreen", "onClick: touchPosition="+touchPosition);
			super.stageActors.screenToStageCoordinates(touchPosition);
			addGameObject(gameObject, touchPosition.x, touchPosition.y);
			this.actorTouched = gameObject;
		}
	}

	private void setCamera() {		
		Stage stage = getStageActors();

		boolean screenOK = false;
		while( ! screenOK ) {
			OrthographicCamera camera = getGameCamera();
			camera.zoom += 0.02; 
			camera.update();

			Vector2 maxObjectSize = getMaxObjectSize();

			//Add screenborder Wall as this is placed halfway the actual screenborder
			maxObjectSize.x += 0.5*Wall.WIDTH;
			maxObjectSize.y += 0.5*Wall.HEIGHT;

			Vector2 screenSize = new Vector2(Gdx.graphics.getWidth(), 0f);
			stage.screenToStageCoordinates(screenSize);
			screenSize.sub(worldSize); //screenSize will hold the difference

			//			Gdx.app.log("LevelEditorScreen", "After screenSize="+screenSize+", worldSize="+worldSize+", screenSize - worldSize = "+ screenSize.sub(worldSize));

			if(Math.abs(screenSize.x) > maxObjectSize.x ) {
				this.menuPosition = MenuPosition.RIGHT;
				screenOK = true;
			} else if( Math.abs(screenSize.y) > maxObjectSize.y ) {
				this.menuPosition = MenuPosition.TOP;
				screenOK = true;
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
		level.save();
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

	private ArrayList<Actor> getActors(Rectangle rectangle) {
		ArrayList<Actor> actorsInRectangle = new ArrayList<Actor>();

		Array<Actor> actors = super.stageActors.getActors();
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

	private void moveGameObject(GameObject gameObject, Vector2 v) {
		Vector2 newPos = new Vector2(gameObject.getX(), gameObject.getY());
		newPos.add(v);

		if( this.snapToGrid ) {
			this.grid.map(newPos);
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
			super.stageActors.addActor(this.grid);
		} else {
			this.grid.remove();
		}
	}

	private void setupMainMenu() {
		this.mainMenu = new ButtonsDialog("Main menu", getSkin(), ButtonsDialog.ORIENTATION.VERTICAL);

		this.mainMenu.add("Tools", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				showToolsDialog();
				mainMenu.hide();
			}
		});

		this.mainMenu.add("Options", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				showOptionsDialog();
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
				getGame().setScreen(new LevelEditorMenuScreen(getGame()));
			}
		});

		this.mainMenu.create();
	}

	private void showToolsDialog() {
		ToolsPickerDialog dialog = new ToolsPickerDialog(getGame(), getSkin());
		dialog.create();
		dialog.show(getStageUIElements());
	}

	private void showOptionsDialog() {
		LevelEditorOptionsDialog dialog = new LevelEditorOptionsDialog(getSkin(), this.preferences, this);
		dialog.create();
		dialog.show(getStageUIElements());
	}

	private void showGameObjectCongfigurationDialog(GameObject gameObject) {
		GameObjectConfigurationDialog dialog = new GameObjectConfigurationDialog(gameObject, getSkin());
		dialog.addButton("Copy " + gameObject.getName(), new OnClickListener() {

			@Override
			public void onClick(Dialog dialog, int which) {
				GameObject copy = ((GameObjectConfigurationDialog) dialog).getGameObject().createCopy();
				Vector2 stageCoords = stageActors.screenToStageCoordinates(new Vector2(copy.getX(), copy.getY()));
				copy.setPosition(stageCoords.x, stageCoords.y);
				getGame().addGameObject(copy);
				stageActors.addActor(copy);
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
		dialog.create();
		dialog.show(getStageUIElements());
	}

	private void setupMenu(Stage stage) {
		MenuButton menuButton = new MenuButton();
		menuButton.setListener(this);
		super.stageUIActors.addActor(menuButton);

		ArrayList<GameObject> gameObjects = this.game.getAvailableGameObjects();

		Vector2 worldSize = this.game.getWorldSize();

		//		this.menuPosition = MenuPosition.LEFT;
		if( this.menuPosition == MenuPosition.TOP ) {
			float delta = stage.getWidth() / ( gameObjects.size() + 1 );
			float x = 0.1f;
			float y = (float) (worldSize.y + 0.6*Wall.HEIGHT);

			/**
			 * MenuButton's pivot is positioned at (0,0)=(left, bottom)
			 */
			Vector2 stageCoords = stage.stageToScreenCoordinates(new Vector2(x,worldSize.y - y));
			super.stageUIActors.screenToStageCoordinates(stageCoords);
			menuButton.setPosition(stageCoords.x, stageCoords.y);
			x+=delta;

			for(GameObject object : gameObjects ) {
				addGameObjectToMenu(stage, object, x, y);
				x += delta;
			}
		} else {
			float delta = stage.getHeight() / ( gameObjects.size() + 1 );
			float x = (float) (worldSize.x + 0.6*Wall.WIDTH);
			float y = worldSize.y - Wall.HEIGHT;

			//Add menu button
			float stageActorMenuButtonWidth = Game.convertScreenToWorld(menuButton.getWidth());
			float stageActorMenuButtonHeight = Game.convertScreenToWorld(menuButton.getHeight());
			Vector2 stageUICoords = new Vector2(Game.convertWorldToScreen(x - stageActorMenuButtonWidth), 
					Game.convertWorldToScreen(y - stageActorMenuButtonHeight));
			menuButton.setPosition(stageUICoords.x, stageUICoords.y);
			
			y-=delta;

			for(GameObject object : gameObjects ) {
				addGameObjectToMenu(stage, object, x, y);
				y -= delta;
			}
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
				setupMainMenu();
				if( this.menuPosition == MenuPosition.TOP ) {
					this.mainMenu.setPosition(button.getX(), 
							button.getY() - this.mainMenu.getHeight());
				} else {
					this.mainMenu.setPosition(button.getX() - this.mainMenu.getWidth(), 
							button.getY() - ( this.mainMenu.getHeight() - button.getHeight() ) );
				}
			}

			if( this.mainMenu.isVisible() ) {
				this.mainMenu.hide();
			} else {
				this.mainMenu.show(getStageUIElements());
			}
		}
	}

	@Override
	public void onLongPress(Button button) {
		// TODO Auto-generated method stub

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
}


