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

package com.strategames.engine.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;
import com.strategames.engine.scenes.scene2d.ui.MenuButton;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.tweens.ActorAccessor;
import com.strategames.ui.dialogs.ButtonsDialog;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

/**
 * TODO dialogs do not take exclusive focus which means that user can still select
 * buttons. This may result in application crashing as the dialog may still be in 
 * the stage. We should either make sure the dialog takes complete focus or stages
 * are properly cleared when switching screens.
 *
 */
public abstract class AbstractScreen implements Screen, InputProcessor
{
	private GameEngine gameEngine;
	private InputMultiplexer multiplexer;
	private static Skin skin;
	private Stage stageActors;
	private Stage stageUIActors;

	private static OrthographicCamera menuCamera;
	private static OrthographicCamera gameCamera;

	private static Vector2 menuSize = new Vector2(510, 810);

	private TweenManager tweenManager;

	private Label title;
	private ButtonsDialog mainMenu;
	private MenuButton menuButton;

	private Array<Vector2> originalPositions;

	public AbstractScreen() {
		this(null);
	}

	/**
	 * 
	 * @param engine
	 */
	public AbstractScreen(GameEngine engine)
	{
		this.gameEngine = engine;

		this.tweenManager = new TweenManager();

		Gdx.input.setCatchBackKey(true);
	}

	/**
	 * 
	 * @param game
	 * @param stageUIActors stage to use for UI elements
	 * @param stageActors stage to use for game objects
	 */
	public AbstractScreen(GameEngine game, Stage stageUIActors, Stage stageActors) {
		this(game);
		this.stageUIActors = stageUIActors;
		this.stageActors = stageActors;
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public void setMenuSize(Vector2 menuSize) {
		AbstractScreen.menuSize = menuSize;
	}

	public Vector2 getMenuSize() {
		return menuSize;
	}

    /**
     *
     * @return title of this screen
     */
	public Label getTitle() {
		return title;
	}

    /**
     * Set the title of the screen
     * @param title label holding the title
     */
    public void setTitle(Label title) {
        this.title = title;
    }

    public OrthographicCamera getGameCamera() {
		if( gameCamera == null ) {
			Vector2 size = this.gameEngine.getViewSize();
			gameCamera = new OrthographicCamera(size.x, size.y);
			gameCamera.position.set(size.x/2f, size.y/2f, 0f);
		}
		return gameCamera;
	}

	public static void setGameCamera(OrthographicCamera gameCamera) {
		AbstractScreen.gameCamera = gameCamera;
	}

	public OrthographicCamera getMenuCamera() {
		if( menuCamera == null ) {
			Vector2 size = getMenuSize();
			menuCamera = new OrthographicCamera(size.x, size.y);
			menuCamera.position.set(size.x/2f, size.y/2f, 0f);
		}
		return menuCamera;
	}

	public static void setMenuCamera(OrthographicCamera menuCamera) {
		AbstractScreen.menuCamera = menuCamera;
	}

	public InputMultiplexer getMultiplexer() {
		if( this.multiplexer == null ) {
			this.multiplexer = new InputMultiplexer();
		}
		return this.multiplexer;
	}

	protected GameEngine getGameEngine() {
		return this.gameEngine;
	}

	protected String getName()
	{
		return ((Object) this).getClass().getSimpleName();
	}

	public Skin getSkin()
	{
		if( skin == null ) {
			skin = new Skin( Gdx.files.internal( "skin/uiskin.json" ) );
		}
		return skin;
	}

	/**
	 * {@inheritDoc show}
	 * <br/>
	 * Calls {@link #setupUI(Stage)}, {@link #setupMenu()}, and {@link #setupActors(Stage)} in that order.
	 * <br/>
	 * {@link #setupUI(Stage)} and {@link #setupMenu()} are ONLY called if UI stage has not already been setup.
	 * In other words if {@link #getStageUIActors()} returns null
	 * <br/>
	 * {@link #setupActors(Stage)} is ONLY called if {@link #getStageActors()} returns null
	 */
	@Override
	public void show()
	{
		if( this.stageUIActors == null ) {
			getMultiplexer().addProcessor(getStageUIActors());
			setupUI(getStageUIActors());
			setupMenu();
		}

		if( this.stageActors == null ) {
			setupActors(getStageActors());
		}

		if( this.title != null ) {
			Vector2 sizeMenu = getMenuSize();
			this.title.setPosition((sizeMenu.x / 2f) - (this.title.getWidth() / 2f), 700f);
			getStageUIActors().addActor(this.title);
		}

		getMultiplexer().addProcessor(this);
		Gdx.input.setInputProcessor(getMultiplexer());

		this.tweenManager.killAll();
		Timeline timeline = showAnimation();
		if( timeline != null ) {
			timeline.start(this.tweenManager);
		}
	}

	@Override
	public void render( float delta )
	{	
		Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		stageActors.act(delta);
		stageActors.draw();

		stageUIActors.act(delta);
		stageUIActors.draw();

		this.tweenManager.update(delta);
	}

	@Override
	public void hide()
	{
		if( this.mainMenu != null ) {
			this.mainMenu.remove();
			this.mainMenu.setVisible(false);
		}
		
		this.tweenManager.killAll();
		Timeline timeline = hideAnimation();
		if( timeline != null ) {
			timeline.setCallbackTriggers(TweenCallback.ANY);
			timeline.setCallback(new TweenCallback() {

				@Override
				public void onEvent(int arg0, BaseTween<?> arg1) {
					if( arg0 == TweenCallback.COMPLETE ){
						getGameEngine().notifyScreenHidden();
					}
				}
			});

			timeline.start(this.tweenManager);
		} else {
			getGameEngine().notifyScreenHidden();
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void dispose()
	{
		if( stageActors != null ) stageActors.clear();
		if( stageUIActors != null ) stageUIActors.clear();
		SoundEffect.releaseAll();
	}

	public void startTimeline(Timeline timeline) {
		timeline.start(this.tweenManager);
	}

	/**
	 * Clears the stage for actors
	 */
	public void resetStageActors() {
		if( this.stageActors != null ) {
			this.stageActors.clear();
			this.stageActors = null;
		}
	}

	public Stage getStageActors() {
		if( this.stageActors == null ) {
			Vector3 sizeWorld = this.gameEngine.getWorldSize();
			this.stageActors = new Stage(new FitViewport(sizeWorld.x, sizeWorld.y, getGameCamera()));
		}
		return this.stageActors;
	}

	public Stage getStageUIActors() {
		if( this.stageUIActors == null ) {
			Vector2 sizeMenu = getMenuSize();
			this.stageUIActors = new Stage(new FitViewport(sizeMenu.x, sizeMenu.y, getMenuCamera()));
		}
		return this.stageUIActors;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //Hide main menu if visible but not clicked
		if( ( this.mainMenu != null ) && ( this.mainMenu.isVisible() ) ) {
            if ( this.mainMenu != this.mainMenu.hit(screenX, screenY, true) ) {
                hideMainMenu();
            }
        }

        return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if((keycode == Keys.BACK) 
				|| (keycode == Keys.ESCAPE)) {
			if ( handleBackNavigation() ) {
				return true;
			}
		}
		return this.gameEngine.handleKeyEvent(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	/**
	 * Adds an item to the menu.
	 * <br/>
	 * Only call this method within {@link #setupUI(Stage)}
	 * @param text of the menu item to be added
	 */
	public void addMenuItem(final String text) {
		if( this.mainMenu == null ) {
			this.mainMenu = new ButtonsDialog(getStageUIActors(), getSkin(), ButtonsDialog.ORIENTATION.VERTICAL);
		}

		this.mainMenu.add(text, new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onMenuItemSelected(text);
			}
		});
	}

//	public ButtonsDialog getMainMenu() {
//		return mainMenu;
//	}

	public void hideMainMenu() {
		this.mainMenu.hide();
	}
	
	/**
	 * Override to handle menu item selections
	 * <br/>
	 * Menu is not automatically closed when a selection has been made.
	 * Use {@link #hideMainMenu()} to hide the main menu manually.
	 * @param text of menu item as created using {@link #addMenuItem(String)}
	 */
	protected void onMenuItemSelected(String text) {

	}

	@Override
	public String toString() {
		String message = ", stageActors="+stageActors+
				", stageUIActors="+stageUIActors+
				"\nmenuCamera="+menuCamera+
				", gameCamera="+gameCamera+
				"\nmenuSize="+menuSize;
		return super.toString() + message;
	}

	/**
	 * Implement to override default back navigation handling
	 * by Game class
	 * @return true if handled, false to pass key to game class as well
	 */
	protected boolean handleBackNavigation() {
		return false;
	}

	/**
	 * Called to create the animation when screen is displayed
	 * @return Timeline timeline that contains the animation(s) or null to disable animation
	 */
	protected Timeline showAnimation() {
		Timeline timelineParallel = Timeline.createParallel();
		Timeline timelineSequence = Timeline.createSequence();
		Stage stage = getStageUIActors();

		if(this.title != null) {
			this.title.setY(stage.getHeight() + this.title.getHeight());
			timelineParallel.push(Tween.to(this.title, ActorAccessor.POSITION_Y, 0.4f)
					.target(700));
		}

		if(this.menuButton != null) {
			this.menuButton.setY(stage.getHeight() + this.menuButton.getHeight());
			timelineParallel.push(Tween.to(this.menuButton, ActorAccessor.POSITION_Y, 0.4f)
					.target(700));
		}

		Array<Actor> actors = stage.getActors();

		/**
		 * Hiding will move actors out of screen. If we want to re-show
		 * the screen we need to know where they were originally.
		 */
		if( this.originalPositions == null ) {
			this.originalPositions = new Array<Vector2>(actors.size);
			for(int i = 0; i < actors.size; i++) {
				Actor actor = actors.get(i);
				this.originalPositions.add(new Vector2(actor.getX(), actor.getY()));
			}
		}

		for(int i = 0; i < actors.size; i++) {
			Actor actor = actors.get(i);
			if( ( actor != this.title ) && ( actor != this.menuButton ) ) {
				Vector2 pos = this.originalPositions.get(i);
				actor.setY(-60f);
				timelineSequence.push(Tween.to(actor, ActorAccessor.POSITION_Y, 0.1f)
						.target(pos.y));
			}
		}

		timelineParallel.push(timelineSequence);
		return timelineParallel;
	}

	/**
	 * Called to create the animation when screen will be hidden
	 * @return Timeline timeline that contains the animation(s) or null to disable animation
	 */
	protected Timeline hideAnimation() {
		Stage stage = getStageUIActors();
		Timeline timeline = Timeline.createParallel();

		if(this.title != null) {
			timeline.push(Tween.to(this.title, ActorAccessor.POSITION_Y, 0.4f)
					.target(stage.getHeight() + this.title.getHeight()));
		}

		if(this.menuButton != null) {
			timeline.push(Tween.to(this.menuButton, ActorAccessor.POSITION_Y, 0.4f)
					.target(stage.getHeight() + this.menuButton.getHeight()));
		}

		Array<Actor> actors = getStageUIActors().getActors();

		for(int i = 0; i < actors.size; i++) {
			Actor actor = actors.get(i);
			if( ( actor != this.title ) && ( actor != this.menuButton ) ) {
				timeline.push(Tween.to(actor, ActorAccessor.POSITION_Y, 0.1f)
						.target(-actor.getHeight()));
			}
		}

		return timeline;
	}

	/**
	 *
	 */

	/**
	 * Called to create the stage for the UI elements.
	 * <br>
	 * The actors in this stage will only be drawn and
	 * not updated using Box2D
	 * @param stage that should hold the UI actors.
	 */
	abstract protected void setupUI(Stage stage);

	/**
	 * Called to create the stage for the game object elements.
	 * Note that this will only be called the first time the screen is shown.
	 * If you want {@link #setupActors(Stage)} to be called when {@link #show()} is called
	 * make sure you call {@link #resetStageActors()} before showing the screen.
	 * <br>
	 * The actors in this stage will be drawn and
	 * updated using Box2D
	 * @param stage that should hold the game actors
	 */
	abstract protected void setupActors(Stage stage);

	private void setupMenu() {
		if( this.mainMenu != null ) {

			if( this.menuButton == null ) {
				this.menuButton = new MenuButton();
				this.menuButton.setPosition(450f, 700f);
				this.menuButton.setListener( new ActorListener() {

					@Override
					public void onTap(Actor actor) {
						if( mainMenu.isVisible() ) {
							mainMenu.hide();
						} else {
							mainMenu.show();
						}
					}

					@Override
					public void onLongPress(Actor actor) {	}
					
				});
				getStageUIActors().addActor(this.menuButton);
			}

			this.mainMenu.create();
			this.mainMenu.setPosition(this.menuButton.getX() - this.mainMenu.getWidth(), 
					this.menuButton.getY() - ( this.mainMenu.getHeight() - this.menuButton.getHeight() ) );
		}
	}
}
