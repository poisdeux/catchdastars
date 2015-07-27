package com.strategames.catchdastars.screens.editor;

import java.util.ArrayList;

import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
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
import com.strategames.catchdastars.gameobjects.BalloonBlue;
import com.strategames.catchdastars.gameobjects.BalloonRed;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Balloon;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.gameobject.types.Wall;
import com.strategames.engine.gameobject.types.WallVertical;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;
import com.strategames.engine.scenes.scene2d.ui.MenuButton;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.storage.GameWriter;
import com.strategames.engine.storage.LevelWriter;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelEditorPreferences;
import com.strategames.engine.utils.ScreenshotFactory;
import com.strategames.ui.dialogs.ButtonsDialog;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.dialogs.GameObjectConfigurationDialog;
import com.strategames.ui.helpers.Grid;

public class LevelEditorScreen extends AbstractScreen
        implements ActorListener, GestureListener, Dialog.OnClickListener {

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

    private Level level;

    private int blueBalloonsFromEntryLevels;
    private int redBalloonsFromEntryLevels;

    private Array<Door> entryLevelsDoors;

    private enum States {
        ZOOM, LONGPRESS, DRAG, NONE
    }

    private States state;

    public LevelEditorScreen(GameEngine gameEngine) {
        super(gameEngine);

        this.initialTouchPosition = new Vector2();
        this.dragDirection = new Vector2();

        this.selectedGameObjects = new ArrayList<GameObject>();

        this.worldSize = gameEngine.getWorldSize();

        this.grid = new Grid(this.worldSize.x, this.worldSize.y);

        Game game = gameEngine.getGame();
        this.level = game.getCurrentLevel();
    }

    @Override
    protected void setupUI(Stage stage) {
        this.snapToGrid = LevelEditorPreferences.snapToGridEnabled();
        GestureDetector d = new GestureDetector(this);
        d.setTapSquareSize(60f);
        getMultiplexer().addProcessor(d);
    }

    @Override
    protected void setupActors(Stage stage) {
        getMultiplexer().addProcessor(stage);
        zoomCamera((OrthographicCamera) stage.getCamera());

        getGameEngine().pauseGame();

        displayGrid(LevelEditorPreferences.displayGridEnabled());
        setupLevel();
    }

    @Override
    public void show() {
        /**
         * Testing level from the leveleditor will start LevelScreen which automatically
         * saves game progression. To prevent intervening with any real game progression
         * we set it to a high value.
         * TODO change int to a UUID?
         */
        getGameEngine().getGame().getGameMetaData().setSavedGameProgressNumber(1000);
        GameWriter.deleteInprogress(getGameEngine().getGame().getGameMetaData());

        resetStageActors();

        setupInfoFromEntryLevels();

        super.show();
    }

    @Override
    public void hide() {
        getGameCamera().zoom = this.cameraZoomInitial;
        super.hide();
    }

    @Override
    protected boolean handleBackNavigation() {
        if( ( this.mainMenu != null ) && ( this.mainMenu.isVisible() ) ) {
            this.mainMenu.hide();
            return true;
        }

        if( ! saveLevel() ) {
            //notify user saving failed
            ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Error saving level", getSkin());
            dialog.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(Dialog dialog, int which) {
                    dialog.remove();
                    getGameEngine().stopScreen();
                }
            });
            dialog.create();
            dialog.show();
        }

        return false;
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

        this.actorTouched = actor;

        if( actor != null ) { // actor selected
            //			deselectAllGameObjects();
            selectGameObject((GameObject) this.actorTouched);
        } else if( this.uiElementHit == null ) { // empty space selected
            //			deselectAllGameObjects();
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
            this.level.addGameObject(gameObject);
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
                this.level.removeGameObject(door);
                gameObject.remove();
            } else {
                placeDoor(door, wall);
            }
        } else if( ! inGameArea(gameObject) ) {
            this.level.removeGameObject(gameObject);
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
        if( ( this.actorTouched != null ) && ( this.uiElementHit == null ) ){
            if( count > 1 ) { //double tap
                showGameObjectCongfigurationDialog((GameObject) this.actorTouched);
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

    @Override
    public void onClick(Dialog dialog, int which) {
        if( dialog instanceof LevelEditorOptionsDialog ) {
            handleLevelEditorOptionsDialogOnClick(which);
        } else if (dialog instanceof GameObjectConfigurationDialog ) {
            handleGameObjectConfigurationDialogOnClick(dialog, which);
        } else if( dialog instanceof ChangeWorldSizeDialog ) {
            handleChangeWorldSizeDialogOnClick(dialog, which);
        }
    }

    public void setupLevel() {
        if( this.level == null ) {
            ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Error loading level", getSkin());
            dialog.setOnClickListener(this);
            dialog.create();
            dialog.show();
            return;
        }

        addBalloonsFromEntryLevels();

        GameEngine gameEngine = getGameEngine();
        Stage stage = getStageActors();
        Array<GameObject> gameObjects = this.level.getGameObjects();
        if( (gameObjects != null) ) {
            for( GameObject gameObject : gameObjects ) {
                gameObject.initializeConfigurationItems();
                //				deselectGameObject(gameObject);
                gameEngine.addGameObject(gameObject, stage);
                if( ! gameObject.isNew() ) {
                    gameObject.setColor(1f, 1f, 1f, 0.3f);
                }
            }
        }
        Array<Door> doors = this.level.getDoors();
        if( (doors != null) ) {
            for( Door door : doors ) {
                door.initializeConfigurationItems();
                //				deselectGameObject(door);
                gameEngine.addGameObject(door, stage);
            }
        }

        //We setup the menu last to make sure menu items are drawn on top
        setupMenu(getStageActors());
    }

    private void addBalloonsFromEntryLevels() {
        //Get and add entry level doors leading to this level
        int[] levelPos = this.level.getPosition();

        Array<Level> entryLevels = new Array<>();
        Game game = getGameEngine().getGame();
        Array<com.strategames.engine.math.Vector2> entryLevelsPos = this.level.getAccessibleBy();

        for(com.strategames.engine.math.Vector2 entryLevelPos : entryLevelsPos) {

            Level entryLevel = game.getLevel((int) entryLevelPos.x, (int) entryLevelPos.y);

            Array<Door> doors = entryLevel.getDoors();
            for(Door door : doors) {
                int[] pos = door.getAccessToPosition();
                if ((pos[0] == levelPos[0]) && (pos[0] == levelPos[0])) {
                    //determine amount of red and blue balloons from entryLevel
                    for (GameObject gameObject : entryLevel.getGameObjects()) {
                        if (gameObject instanceof Balloon) {
                            //add balloons from entryLevel at door position
                            addBalloonFromEntryLevel((Balloon) gameObject, door, entryLevel);
                        }
                    }
                }
            }
        }
    }

    private void addBalloonFromEntryLevel(Balloon balloon, Door door, Level entryLevel) {
        int[] levelPos = door.getAccessToPosition();
        int[] entryLevelPos = entryLevel.getPosition();

        Vector2 wordlSize = entryLevel.getWorldSize();

        /**
         * -----D------
         * |    b     |
         * |         bD
         * Db         |
         * |          |
         * |       b  |
         * --------D---
         *
         * |,- = wall
         * D = door
         * b = balloon
         */

        Gdx.app.log("LevelEditorScreen", "addBalloonFromEntryLevel: pos=("+door.getX()+","+door.getY()+
                "), levelPos=("+levelPos[0]+","+levelPos[1]+
                "), entryLevelPos=("+entryLevelPos[0]+","+entryLevelPos[0]+
                "), worldSize="+wordlSize);

        /**
         * Determine if entryLevel is left or right from current level
         * and if it is above or below current level
         *
         *    Vertical position:
         *    below: ySign = +1
         *    above: ySign = -1
         *    same:  ySign = 0
         *
         *    Horizontal position:
         *    left:  xSign = -1
         *    right: xSign = +1
         *    same:  xSign = 0
         */
        int ySign = levelPos[1] - entryLevelPos[1];
        int xSign = levelPos[0] - entryLevelPos[0];

        //Door's position from entryLevel should be mirrored
        //in current level
        float xDoor = xSign != 0 ? (worldSize.x - door.getX()) : door.getX();
        float yDoor = ySign != 0 ? (worldSize.y - door.getY()) : door.getY();

        float balloonHeight = ySign < 0 ? balloon.getHeight() : 0;
        float balloonWidth = xSign < 0 ? 0 : balloon.getWidth();

        //entrylevel right from current level then we should position
        //balloon at the rightside of the door. Otherwise on the leftside.
        float xPos = xDoor + (xSign * (balloonWidth + door.getHalfWidth()));

        //entrylevel on top of current level then we should position
        //balloon below the door. Otherwise on above the door.
        float yPos = yDoor + (ySign * (balloonHeight + door.getHalfHeight()));

        Gdx.app.log("LevelEditorScreen", "addBalloonFromEntryLevel: balloon="+xPos+", "+yPos+")");

        Balloon balloonCopy = (Balloon) balloon.copy();
        balloonCopy.moveTo(xPos, yPos);
        balloonCopy.setNew(false);
        this.level.addGameObject(balloonCopy);
    }

    @Override
    protected Timeline showAnimation() {
        return null;
    }

    /**
     * TODO Show progress dialog while creating screenshot
     * @return Timeline
     */
    @Override
    protected Timeline hideAnimation() {
        Pixmap pixmap = ScreenshotFactory.takeScreenshot(getStageActors(), this.level);
        if( pixmap != null ) {
            LevelWriter.saveScreenshot(this.level, pixmap);
        }
        return null;
    }

    private void placeDoor(Door door, Wall wall) {
        Vector3 worldSize = getGameEngine().getWorldSize();

        if( wall instanceof WallVertical ) {
            float wallX = wall.getX();
            door.moveTo(wallX, door.getY());

            if( wall.isBorder() ) {
                int[] currentLevelPosition = getGameEngine().getGame().getCurrentLevelPosition();
                float middle = worldSize.x / 2f;
                if( wallX < middle ) { //If left border set next level to left
                    door.setAccessTo(currentLevelPosition[0] - 1, currentLevelPosition[1]);
                } else { // set next level to right
                    door.setAccessTo(currentLevelPosition[0] + 1, currentLevelPosition[1]);
                }
            }
        } else {
            float wallY = wall.getY();
            door.moveTo(door.getX(), wallY);

            if( wall.isBorder() ) {
                int[] currentLevelPosition = getGameEngine().getGame().getCurrentLevelPosition();
                float middle = worldSize.x / 2f;
                if( wallY < middle ) { //If bottom border set next level to bottom
                    door.setAccessTo(currentLevelPosition[0], currentLevelPosition[1] - 1);
                } else { // set next level to top
                    door.setAccessTo(currentLevelPosition[0], currentLevelPosition[1] + 1);
                }
            }
        }
    }

    /**
     * Returns if the amount of red and blue balloons is greater than the
     * given amount
     * @param amountOfBlue -1 to not take blue balloons into account
     * @param amountOfRed -1 to not take red balloons into account
     * @return true if the amount of both blue and red balloons is greater than amountOfBlue and amountOfRed
     */
    private boolean amountOfBalloonsLargerThan(int amountOfBlue, int amountOfRed) {
        int nBlue = 0;
        int nRed = 0;
        Array<GameObject> gameObjects = this.level.getGameObjects();
        for( GameObject gameObject : gameObjects ) {
            if( gameObject instanceof BalloonBlue ) {
                nBlue++;
            } else if ( gameObject instanceof BalloonRed ) {
                nRed++;
            }
        }
        return ( nBlue > amountOfBlue ) && ( nRed > amountOfRed );
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
        copy.setGame(getGameEngine());
        copy.setupImage();
        copy.setupBody();
        this.level.addGameObject(copy);
        getStageActors().addActor(copy);
        //		deselectGameObject(object);
        selectGameObject(copy);
        return copy;
    }

    /**
     * Positions camera to make room for menu
     */
    private void zoomCamera(OrthographicCamera camera) {
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f);
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
        Array<GameObject> gameObjects = getGameEngine().getAvailableGameObjects();

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

        //		gameObject.setColor(1f, 1f, 1f, 1.0f);
        this.selectedGameObjects.add(gameObject);
    }

    private boolean saveLevel() {
        return LevelWriter.saveOriginal(this.level);
    }

    /**
     * Moves actor to position v.
     * @param stage holding the actor
     * @param actor that should be moved
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
                mainMenu.hide();
                ToolsPickerDialog dialog = new ToolsPickerDialog(stageUIActors, level, getSkin());
                dialog.create();
                dialog.setPosition(mainMenu.getX() - (dialog.getWidth() / 2f), mainMenu.getY());
                dialog.setOnClickListener(LevelEditorScreen.this);
                dialog.show();
            }
        });

        this.mainMenu.add("Options", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainMenu.hide();
                LevelEditorOptionsDialog dialog = new LevelEditorOptionsDialog(stageUIActors, getSkin(), LevelEditorScreen.this);
                dialog.create();
                dialog.setPosition(mainMenu.getX() - (dialog.getWidth() / 2f), mainMenu.getY());
                dialog.show();
            }
        });

        this.mainMenu.add("Play level", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainMenu.hide();
                saveLevel();
                GameEngine game = getGameEngine();
                game.setTestMode(true);
                game.startLevel(level);
            }
        });

        this.mainMenu.setPositiveButton("Save", new OnClickListener() {

            @Override
            public void onClick(Dialog dialog, int which) {
                mainMenu.hide();
                saveLevel();
            }
        });
        this.mainMenu.setNegativeButton("Quit", new OnClickListener() {

            @Override
            public void onClick(Dialog dialog, int which) {
                mainMenu.hide();
                saveLevel();
            }
        });

        this.mainMenu.create();
    }

    private void showGameObjectCongfigurationDialog(GameObject gameObject) {
        GameObjectConfigurationDialog dialog = new GameObjectConfigurationDialog(getStageUIActors(), gameObject, getSkin());
        dialog.setOnClickListener(this);
        if( gameObject instanceof Balloon ) { // make sure we cannot delete all balloons
            if( gameObject instanceof BalloonBlue ) {
                if( amountOfBalloonsLargerThan(1, -1) ) {
                    dialog.setNegativeButton("Delete");
                }
            } else if ( gameObject instanceof BalloonRed ) {
                if( amountOfBalloonsLargerThan(-1, 1) ) {
                    dialog.setNegativeButton("Delete");
                }
            }
        } else {
            dialog.setNegativeButton("Delete");
        }
        dialog.setNeutralButton("Copy");
        dialog.setPositiveButton("Close", this);
        dialog.create();
        dialog.show();
    }

    private void setupMenu(Stage stage) {
        GameEngine game = getGameEngine();
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
            if( object instanceof Balloon ) { //only add balloons to menu if no balloon available from entry level
                if( ( object instanceof BalloonBlue ) && ( this.blueBalloonsFromEntryLevels == 0 ) ) {
                    addGameObjectToMenu(stage, object, x, y);
                    y -= delta;
                } else if( ( object instanceof BalloonRed ) && ( this.redBalloonsFromEntryLevels == 0 ) ) {
                    addGameObjectToMenu(stage, object, x, y);
                    y -= delta;
                }
            } else {
                addGameObjectToMenu(stage, object, x, y);
                y -= delta;
            }
        }
    }

    /**
     * Creates a copy of object and adds copy as menu item at position x,y
     * @param stage in which the menu is placed
     * @param object that should be added to the menu
     * @param x horizontal position
     * @param y vertical position
     */
    private void addGameObjectToMenu(Stage stage, GameObject object, float x, float y) {
        GameObject gameObject = object.copy();
        gameObject.setSaveToFile(false);
        gameObject.setMenuItem(true);
        //		deselectGameObject(gameObject);
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
        return ! ( ( x < 0 ) || ( x > worldSize.x ) ||
                ( y < 0 ) || ( y > worldSize.y ) );
    }

    private void resizeWorld(int w, int h) {
        GameEngine game = getGameEngine();
        Vector2 viewSize = game.getViewSize();
        Vector3 worldSize = game.getWorldSize();
        game.setWorldSize(new Vector3(viewSize.x * w, viewSize.y * h, worldSize.z));
        OrthographicCamera camera = (OrthographicCamera) getStageActors().getCamera();
        camera.viewportWidth =  viewSize.x * w;
        camera.viewportHeight =  viewSize.y * h;
    }

    private void handleLevelEditorOptionsDialogOnClick(int which) {
        switch( which ) {
            case LevelEditorOptionsDialog.CHECKBOX_DISPLAYGRID:
                displayGrid(LevelEditorPreferences.displayGridEnabled());
                break;
            case LevelEditorOptionsDialog.CHECKBOX_SNAPTOGRID:
                this.snapToGrid = LevelEditorPreferences.snapToGridEnabled();
                break;
        }
    }

    private void handleGameObjectConfigurationDialogOnClick(Dialog dialog,int which) {
        GameObject gameObject = ((GameObjectConfigurationDialog) dialog).getGameObject();
        switch( which ) {
            case GameObjectConfigurationDialog.BUTTON_NEUTRAL:
                GameObject copy = copyGameObject(gameObject);
                ((GameObjectConfigurationDialog) dialog).setGameObject(copy);
                break;
            case GameObjectConfigurationDialog.BUTTON_NEGATIVE:
                gameObject.remove();
                this.level.removeGameObject(gameObject);
                dialog.remove();
                break;
            case GameObjectConfigurationDialog.BUTTON_CLOSE_CLICKED:
                dialog.remove();
                break;
        }

        if( ! gameObject.isNew() ) {
            gameObject.setColor(1f, 1f, 1f, 0.3f);
        } else {
            gameObject.setColor(1f, 1f, 1f, 1f);
        }
    }

    private void handleChangeWorldSizeDialogOnClick(Dialog dialog,int which) {
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

    private void setupInfoFromEntryLevels() {
        Game game = getGameEngine().getGame();

        this.entryLevelsDoors = new Array<>();

        //We need entryLevels to setup objects that are not new but should be added from
        //previous levels. For example balloons
        Array<com.strategames.engine.math.Vector2> entryLevels = this.level.getAccessibleBy();
        for(Vector2 pos : entryLevels) {
            parseEntryLevel(game.getLevel((int) pos.x, (int) pos.y));
        }
    }

    private void parseEntryLevel(Level level) {
        Array<GameObject> gameObjects = level.getGameObjects();
        for(GameObject gameObject : gameObjects ) {
            if( gameObject instanceof BalloonBlue ) {
                this.blueBalloonsFromEntryLevels++;
            } else if( gameObject instanceof BalloonRed ) {
                this.redBalloonsFromEntryLevels++;
            } else if( gameObject instanceof Door ) {
                this.entryLevelsDoors.add((Door) gameObject);
            }
        }
    }
}


