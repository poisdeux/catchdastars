/**
 * 
 * Copyright 2013 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.engine.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.screens.game.LevelScreen;
import com.strategames.catchdastars.screens.game.MainMenuScreen;
import com.strategames.catchdastars.screens.game.SplashScreen;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.interfaces.ExportImport;
import com.strategames.engine.interfaces.MusicSelector;
import com.strategames.engine.interfaces.OnMusicFilesReceivedListener;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.storage.GameMetaData;
import com.strategames.engine.storage.LevelLoader;
import com.strategames.engine.tweens.ActorAccessor;
import com.strategames.engine.tweens.GameObjectAccessor;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Textures;

import java.util.EmptyStackException;
import java.util.Stack;

import aurelienribon.tweenengine.Tween;

abstract public class GameEngine extends com.badlogic.gdx.Game implements ContactListener, OnMusicFilesReceivedListener {
    public enum GAME_STATE {
        NONE, RUNNING, PAUSED
    };
    private GAME_STATE gameState = GAME_STATE.NONE;

    private enum LEVEL_STATE {
        NONE, INPROGRESS, FAILED, COMPLETE
    };

    private LEVEL_STATE levelState = LEVEL_STATE.NONE;

    public static final float FRAMES_PER_SECOND = 1/60f;
    public static final float BOX2D_UPDATE_FREQUENCY = 1f/30f;
    private final int BOX2D_VELOCITY_ITERATIONS = 6;
    private final int BOX2D_POSITION_ITERATIONS = 3;
    private WorldThread worldThread;

    public static final float BOX_TO_WORLD = 100f;
    public static final float WORLD_TO_BOX = 1/BOX_TO_WORLD;

    public static final float GRAVITY = 9.81f;

    private Array<GameObject> gameObjectsForDeletion;
    private Array<GameObject> gameObjectsForAddition;
    private Array<GameObject> gameObjectsInGame;

    private AssetManager manager;

//	private int[] levelPosition = new int[2];

//    private int[] nextLevelPosition;

//    private GameMetaData gameMetaData;
    private Game game;

    private World world;

    private Vector3 worldSize = new Vector3(0f, 0f, 0f);
    private Vector2 viewSize = new Vector2(0f, 0f);

    private ExportImport exportimport;
    private MusicSelector musicSelector;

    private String title;

    private Stack<Screen> backStack;

    private Screen currentScreen;
    private Screen newScreen;

    private boolean testMode;

//    private Score score = new Score();

    //	private Stage stageActors;

    private FPSLogger fpsLogger;

    private boolean levelCompleteCalled = false;

    public GameEngine() {
        this.title = "No name game";
        this.manager = new AssetManager();
        this.gameObjectsForDeletion = new Array<GameObject>();
        this.gameObjectsForAddition = new Array<GameObject>();
        this.gameObjectsInGame = new Array<GameObject>();
        this.backStack = new Stack<Screen>();
        this.fpsLogger = new FPSLogger();
        this.worldThread = new WorldThread(this, BOX2D_UPDATE_FREQUENCY, BOX2D_VELOCITY_ITERATIONS, BOX2D_POSITION_ITERATIONS);
        registerTweens();
    }

    public GameEngine(String title) {
        this();
        this.title = title;
    }

    @Override
    public void create() {
        showSplashScreen();
    }

    @Override
    public void dispose () {
        if (currentScreen != null) currentScreen.hide();
        try {
            Textures.getInstance().dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public boolean isTestMode() {
        return testMode;
    }

    @Override
    public void pause () {
        if (currentScreen != null) currentScreen.pause();
    }

    @Override
    public void resume () {
        if (currentScreen != null) currentScreen.resume();
    }

    @Override
    public void render () {
        if (currentScreen != null) currentScreen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize (int width, int height) {
        if (currentScreen != null) currentScreen.resize(width, height);
    }


    @Override
    public void setScreen(Screen screen) {
        this.newScreen = screen;
        if( this.currentScreen != null ) {
            this.currentScreen.hide();
        }
        if( ! ( currentScreen instanceof AbstractScreen ) ) {
            showScreen(this.newScreen);
        }
    }

    @Override
    public Screen getScreen() {
        return this.currentScreen;
    }

    /**
     * Notify the Game manager that screen is now hidden
     */
    public void notifyScreenHidden() {
        if( this.newScreen != null ) {
            showScreen(this.newScreen);
        }
    }

    public void pauseGamePlay() {
        this.gameState = GAME_STATE.PAUSED;
        MusicPlayer.getInstance().pause();
        if( this.worldThread != null ) {
            this.worldThread.stopThread();
        }
    }

    public void resumeGamePlay() {
        this.gameState = GAME_STATE.RUNNING;
        MusicPlayer.getInstance().resume();
        startBox2DThread();
    }

    public void startGamePlay() {
        this.gameState = GAME_STATE.RUNNING;
        this.levelState = LEVEL_STATE.INPROGRESS;
        this.levelCompleteCalled = false;
        MusicPlayer.getInstance().resume();
        startBox2DThread();
    }

    /**
     * Resets the current level
     */
    public void resetLevel() {

    }

    /**
     * Resets the current level and starts the level screen
     */
    public void restartLevel() {
        this.levelState = LEVEL_STATE.NONE;
        resetLevel();
        showLevelScreen();
    }

    /**
     * Sets the current level at pos and starts the level screen
     * @param pos
     */
    public void startLevel(int[] pos) {
        this.game.setCurrentLevelPosition(pos);
        this.levelState = LEVEL_STATE.NONE;
        showLevelScreen();
    }

    public void startLevel(Level level) {
        startLevel(level.getPosition());
    }

    abstract public void startNextLevel();

    public void setLevelCompleted() {
        this.levelState = LEVEL_STATE.COMPLETE;
    }

    public void setLevelFailed() {
        this.levelState = LEVEL_STATE.FAILED;
    }

    public void setLevelInProgress() {
        this.levelState = LEVEL_STATE.INPROGRESS;
    }

    public boolean isLevelCompleted() {
        return this.levelState == LEVEL_STATE.COMPLETE;
    }

    public boolean isLevelFailed() {
        return this.levelState == LEVEL_STATE.FAILED;
    }

    public boolean isLevelInProgress() {
        return this.levelState == LEVEL_STATE.INPROGRESS;
    }


    public boolean isRunning() {
        return this.gameState == GAME_STATE.RUNNING;
    }

    public boolean isPaused() {
        return this.gameState == GAME_STATE.PAUSED;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setExporterImporter(ExportImport exportimport) {
        this.exportimport = exportimport;
    }

    public ExportImport getExporterImporter() {
        return this.exportimport;
    }

    public void setMusicSelector(MusicSelector musicSelector) {
        this.musicSelector = musicSelector;
    }

    public void selectMusicFiles() {
        if( this.musicSelector != null ) {
            this.musicSelector.selectMusic(this);
        }
    }

    public MusicSelector getMusicSelector() {
        return musicSelector;
    }

    public void addToBackstack(Screen screen) {
        this.backStack.add(screen);
    }

    public Screen popBackstack() {
        try {
            return this.backStack.pop();
        } catch (EmptyStackException e) {
            return null;
        }
    }

    public Screen peekBackStack() {
        if( this.backStack.size() > 0 ) {
            return this.backStack.peek();
        } else {
            return null;
        }
    }

    /**
     *
     * @return size of the world in meters
     */
    public Vector3 getWorldSize() {
        return worldSize;
    }

    /**
     * Note that world size is not the same as screen size
     * It is the size of the world as used by Box2D
     * @param worldSize in meters
     */
    public void setWorldSize(Vector3 worldSize) {
        this.worldSize = worldSize;
    }

    /**
     * This is the size of the world that should be displayed. This
     * should be equal or smaller then the size set using {@link #setWorldSize(Vector3)}
     * @return
     */
    public Vector2 getViewSize() {
        return viewSize;
    }

    /**
     * This is the size of the world that should be displayed. This
     * should be equal or smaller then the size set using {@link #setWorldSize(Vector3)}
     * @param viewSize
     */
    public void setViewSize(Vector2 viewSize) {
        this.viewSize = viewSize;
    }

    /**
     * Use this to convert screen pixel sizes to Box2D sizes
     * @param x size in screen pixels
     * @return size in Box2D
     */
    static public float convertScreenToWorld(float x) {
        return x * WORLD_TO_BOX;
    }

    /**
     * Use this to convert Box2D sizes to screen pixel sizes
     * @param x size in Box2D
     * @return size in pixels
     */
    static public float convertWorldToScreen(float x) {
        return x * BOX_TO_WORLD;
    }

    /**
     * Connects a game to the engine.
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
//        this.score.setCumulatedScore(game.getScore());
    }

    public Game getGame() {
        return this.game;
    }

    public void setWorld(World world) {
        this.world = world;
        this.world.setContactListener(this);
    }

    public World getWorld() {
        return world;
    }

    /**
     * Use this to add User interface elements that do not require collision detection nor physics
     * Example: score bar, buttons, background images/animations
     * @param actor
     */
    public void addUIElement(Actor actor) {
        AbstractScreen screen = (AbstractScreen) getScreen();
        screen.getStageUIActors().addActor(actor);
    }

    public AssetManager getManager() {
        return manager;
    }

    /**
     * Queues a game object for removal. Note that this happens asynchronously.
     * <BR/>
     * Note that objects will only be removed is {@link GameObject#setCanBeRemoved(boolean)} has been
     * called and set to true
     * @param object the GameObject to be removed
     */
    public void deleteGameObject(GameObject object) {
        this.gameObjectsForDeletion.add(object);
        this.worldThread.setGameObjectInactive(object);
    }

    public WorldThread getWorldThread() {
        return worldThread;
    }

    /**
     * Returns the game objects that have been added using {@link #deleteGameObject(GameObject)}
     * to be deleted
     * @return ArrayList<GameObject>
     */
    public Array<GameObject> getGameObjectsForDeletion() {
        return gameObjectsForDeletion;
    }

    public void addGameObject(GameObject object, Stage stage) {
        object.setGame(this);

        if( this.gameState == GAME_STATE.RUNNING ) {
            this.gameObjectsForAddition.add(object);
            this.worldThread.addGameObject(object);
        } else {
            object.loadSounds();
            object.setupImage();
            object.setupBody();
        }

//        synchronized (this.gameObjectsInGame) {
            this.gameObjectsInGame.add(object);
//        }

        object.setInGame(true);

        stage.addActor(object);
    }

    public Array<GameObject> getGameObjectsForAddition() {
        return gameObjectsForAddition;
    }

    public Array<GameObject> getGameObjectsInGame() {
        return gameObjectsInGame;
    }

    /**
     * Called by the World thread prior to calling {@link World#step(float, int, int)}
     */
    public void updateWorld() {

    }

    /**
     * Called in game during a render cycle
     * @param delta time in seconds since last cycle
     * @param stage stage that holds the game actors
     */
    public void updateScreen(float delta, Stage stage) {
        //		fpsLogger.log();
        if( this.gameState == GAME_STATE.RUNNING ) {
            //			fixedTimeStep(delta, stage);
            fixedTimeStepInterpolated(delta, stage);

            handleDeleteGameObjectsQueue();

            handleAddGameObjectsQueue();

            if( ( this.levelState == LEVEL_STATE.COMPLETE ) && ( levelCompleteCalled == false ) ){
                levelCompleteCalled = true;
                levelComplete();
            } else if( this.levelState == LEVEL_STATE.FAILED ) {
                levelFailed();
            }
        }
    }

    /**
     * Called when a key was pressed
     * @param keycode one of the constants in Input.Keys
     * @return whether the key was processed
     */
    public boolean handleKeyEvent(int keycode) {
        if((keycode == Keys.BACK)
                || (keycode == Keys.ESCAPE)) {
            if( this.gameState == GAME_STATE.RUNNING ) {
                pauseGamePlay();
            } else {
                stopScreen();
            }
            return true;
        }
        return false;
    }

    public void showMainMenu() {
        Screen s = new MainMenuScreen(this);
        setScreen( s );
        addToBackstack(s);
    }

    public void showSplashScreen() {
        setScreen(new SplashScreen(this));
    }


    /**
     * Hides the current screen and shows the previous screen
     * TODO implement disposing screens when popped from backstack. This must be done after hide animation has finished
     */
    public void stopScreen() {
        if( popBackstack() != null ) {
            setScreen(peekBackStack());
        }
    }

    @Override
    public void onMusicFilesReceived() {
        MusicPlayer player = MusicPlayer.getInstance();
        player.setLibrary(this.musicSelector.getLibrary());
    }

    /**
     * Loads the current set level. If the level has already been played the saved version
     * is loaded. Otherwise the original level is loaded.
     * @param listener
     */
    public void loadLevel(LevelLoader.OnLevelLoadedListener listener) {
        GameMetaData gameMetaData = this.game.getGameMetaData();
        Level level = LevelLoader.loadCompleted(gameMetaData, this.game.getCurrentLevelPosition());
        if( level == null ) {
            level = LevelLoader.loadOriginal(gameMetaData, this.game.getCurrentLevelPosition());
        }

        listener.onLevelLoaded(level);
    }

    /**
     * Called by AbstractScreen when level is loaded and ready to start the game
     * @param stage that should hold the game objects
     * @return true if setup was successful, false otherwise
     */
    abstract public boolean setup(Stage stage);

    /**
     * Called when game state is set to level complete. Use this to calculate the score
     * ,show a level complete animation, save game state, ...
     */
    abstract public void levelComplete();

    /**
     * Called when game state is set to level failed. Use this to show start animation or show
     * a dialog.
     */
    abstract public void levelFailed();

    private void handleDeleteGameObjectsQueue() {
        Array<GameObject> notDeletedGameObjects = new Array<GameObject>();
        for (GameObject object : this.gameObjectsForDeletion ) {
            if( object.canBeRemoved() ) {
                if( object.remove() ) {
                    object.clear();
                }

//                synchronized (this.gameObjectsInGame) {
                    this.gameObjectsInGame.removeValue(object, true);
//                }
            } else {
                notDeletedGameObjects.add(object);
            }
        }
        this.gameObjectsForDeletion = notDeletedGameObjects;
    }

    private void handleAddGameObjectsQueue() {
        for(GameObject object : this.gameObjectsForAddition) {
            object.setupImage();
        }
        this.gameObjectsForAddition.clear();
    }

    private void fixedTimeStep(float delta, Stage stage) {
        this.world.step(BOX2D_UPDATE_FREQUENCY, 6, 2);
        Array<Actor> actors = stage.getActors();
        int size = actors.size;

        for(int i = 0; i < size; i++) {
            GameObject gameObject = (GameObject) actors.get(i);

        }
    }

    private void fixedTimeStepInterpolated(float delta, Stage stage) {
        //		Gdx.app.log("Game", "fixedTimeStepInterpolated: delta="+delta);

        //		if( delta > 0.25f ) { //upper bound on framerate to prevent spiral of death
        //			delta = 0.25f;
        //		}
        //
        //		accumulator += delta;
        //
        //		while (accumulator >= BOX2D_UPDATE_FREQUENCY) {
        //			this.world.step(BOX2D_UPDATE_FREQUENCY, BOX2D_VELOCITY_ITERATIONS, BOX2D_POSITION_ITERATIONS);
        //			accumulator -= BOX2D_UPDATE_FREQUENCY;
        //		}

        //		interpolateGameObjectsCurrentPosition(accumulator/BOX2D_UPDATE_FREQUENCY, stage);
        if( delta > BOX2D_UPDATE_FREQUENCY ) {
            //Rendering running slower than world updates
            //Should we also use Toast to notify user?
            Gdx.app.log("Game", "Renderer took "+delta+" seconds while world is updated each "+BOX2D_UPDATE_FREQUENCY+" seconds");
        }
        interpolateGameObjectsCurrentPosition(delta/BOX2D_UPDATE_FREQUENCY, stage);
    }

    private void interpolateGameObjectsCurrentPosition(float alpha, Stage stage) {
        Array<Actor> actors = stage.getActors();
        int size = actors.size;

        for(int i = 0; i < size; i++) {
            ((GameObject) actors.get(i)).interpolate(alpha);
        }
    }

    private void showLevelScreen() {
        LevelScreen screen = new LevelScreen(this);
        setScreen( screen );

        //Make sure LevelScreen is only added once to the backstack to prevent
        //going back to a previous level if user quits level
        if( ! ( peekBackStack() instanceof LevelScreen ) ) {
            addToBackstack(screen);
        }
    }

    private void showScreen(Screen screen) {
        this.newScreen.show();
        this.newScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.currentScreen = this.newScreen;
    }

    private void registerTweens() {
        Tween.registerAccessor(Actor.class, new ActorAccessor());
        Tween.registerAccessor(GameObject.class, new GameObjectAccessor());
    }

    private void startBox2DThread() {
        if( this.worldThread != null ) {
            this.worldThread.stopThread();
        }
        this.worldThread = new WorldThread(this, BOX2D_UPDATE_FREQUENCY, BOX2D_VELOCITY_ITERATIONS, BOX2D_POSITION_ITERATIONS);
        this.worldThread.start();
    }

    /**
     * This should return one game object for each type used in the game.
     * @return
     */
    abstract public Array<GameObject> getAvailableGameObjects();
}
