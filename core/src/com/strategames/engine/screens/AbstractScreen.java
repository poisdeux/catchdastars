package com.strategames.engine.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.strategames.engine.game.Game;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.tweens.ActorAccessor;

public abstract class AbstractScreen implements Screen, InputProcessor
{
	private Game game;
	private InputMultiplexer multiplexer;
	private static Skin skin;
	private Stage stageActors;
	private Stage stageUIActors;

	private static OrthographicCamera menuCamera;
	private static OrthographicCamera gameCamera;

	private static Vector2 menuSize;

	private TweenManager tweenManager;

	private Label title;

	private Array<Vector2> originalPositions;
	
	public AbstractScreen() {
		this(null, null);
	}
	
	/**
	 * 
	 * @param game 
	 * @param title set to null to not display the screen title
	 */
	public AbstractScreen(Game game, String title)
	{
		this.game = game;

		this.tweenManager = new TweenManager();

		Gdx.input.setCatchBackKey(true);

		if( title != null ) {
			this.title = new Label(title, getSkin());
		}
	}
	
	/**
	 * 
	 * @param game
	 * @param title set to null to not display the screen title
	 * @param stageUIActors stage to use for UI elements
	 * @param stageActors stage to use for game objects
	 */
	public AbstractScreen(Game game, String title, Stage stageUIActors, Stage stageActors) {
		this(game, title);
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
		if( menuSize == null ) {
			setMenuSize(new Vector2(510, 810));
		}
		return menuSize;
	}

	public Label getTitle() {
		return title;
	}

	public OrthographicCamera getGameCamera() {
		if( gameCamera == null ) {
			Vector2 size = this.game.getViewSize();
			gameCamera = new OrthographicCamera(size.x, size.y);
			gameCamera.position.set(size.x/2f, size.y/2f, 0f);
		}
		return gameCamera;
	}

	public OrthographicCamera getMenuCamera() {
		if( menuCamera == null ) {
			Vector2 size = getMenuSize();
			menuCamera = new OrthographicCamera(size.x, size.y);
			menuCamera.position.set(size.x/2f, size.y/2f, 0f);
		}
		return menuCamera;
	}

	public InputMultiplexer getMultiplexer() {
		if( this.multiplexer == null ) {
			this.multiplexer = new InputMultiplexer();
			this.multiplexer.addProcessor(getStageUIActors());
			this.multiplexer.addProcessor(this);
		}
		return this.multiplexer;
	}

	protected Game getGame() {
		return this.game;
	}

	protected String getName()
	{
		return getClass().getSimpleName();
	}

	public Skin getSkin()
	{
		if( skin == null ) {
			skin = new Skin( Gdx.files.internal( "skin/uiskin.json" ) );
		}
		return skin;
	}

	@Override
	public void show()
	{	
		if( this.stageUIActors == null ) {
			setupUI(getStageUIActors());
		}
	
		if( this.stageActors == null ) {
			setupActors(getStageActors());
		}
		
		if( this.title != null ) {
			Vector2 sizeMenu = getMenuSize();
			this.title.setPosition((sizeMenu.x / 2f) - (this.title.getWidth() / 2f), 700f);
			getStageUIActors().addActor(this.title);
		}

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
		this.tweenManager.killAll();
		Timeline timeline = hideAnimation();
		if( timeline != null ) {
			timeline.setCallbackTriggers(TweenCallback.ANY);
			timeline.setCallback(new TweenCallback() {

				@Override
				public void onEvent(int arg0, BaseTween<?> arg1) {
					if( arg0 == TweenCallback.COMPLETE ){
						getGame().notifyScreenHidden();
					}
				}
			});

			timeline.start(this.tweenManager);
		} else {
			getGame().notifyScreenHidden();
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

	public Stage getStageActors() {
		if( this.stageActors == null ) {
			Vector3 sizeWorld = this.game.getWorldSize();
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
		return this.game.handleKeyEvent(keycode);
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
	 * Implement to override default back navigation handling
	 * by Game class
	 * @return true if handled, false to pass key to game class as well
	 */
	protected boolean handleBackNavigation() {
		return false;
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
	 * Called to create the animation when screen is displayed
	 * @return Timeline timeline that contains the animation(s) or null to disable animation
	 */
	protected Timeline showAnimation() {
		Timeline timelineParallel = Timeline.createParallel();
		Timeline timelineSequence = Timeline.createSequence();
		Stage stage = getStageUIActors();

		if(title != null) {
			title.setY(stage.getHeight() + title.getHeight());
			timelineParallel.push(Tween.to(title, ActorAccessor.POSITION_Y, 0.4f)
					.target(700));
		}

		Array<Actor> actors = stage.getActors();
		if( this.originalPositions == null ) {
			this.originalPositions = new Array<Vector2>(actors.size);
			for(int i = 0; i < actors.size; i++) {
				Actor actor = actors.get(i);
				this.originalPositions.add(new Vector2(actor.getX(), actor.getY()));
			}
		}
		for(int i = 0; i < actors.size; i++) {
			Actor actor = actors.get(i);
			if( actor != title ) {
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

		if(title != null) {
			timeline.push(Tween.to(title, ActorAccessor.POSITION_Y, 0.4f)
					.target(stage.getHeight() + title.getHeight()));
		}

		Array<Actor> actors = getStageUIActors().getActors();

		for(int i = 0; i < actors.size; i++) {
			Actor actor = actors.get(i);
			if( actor != title ) {
				timeline.push(Tween.to(actor, ActorAccessor.POSITION_Y, 0.1f)
						.target(-actor.getHeight()));
			}
		}

		return timeline;
	}

	/**
	 * Called to create the stage for the UI elements.
	 * <br>
	 * The actors in this stage will only be drawn and
	 * not updated using Box2D
	 * @return stage that should hold the UI elements
	 */
	abstract protected void setupUI(Stage stage);

	/**
	 * Called to create the stage for the game object elements.
	 * <br>
	 * The actors in this stage will be drawn and
	 * updated using Box2D
	 * @param stage that should hold the game actors
	 */
	abstract protected void setupActors(Stage stage);
}
