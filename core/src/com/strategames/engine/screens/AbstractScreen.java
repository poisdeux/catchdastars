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
import com.badlogic.gdx.utils.viewport.Viewport;
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

	private Vector2 centerPosition;

	public AbstractScreen(Game game, String title)
	{
		this.game = game;

		Gdx.input.setCatchBackKey(true);

		setupUI(getStageUIActors());
		setupActors(getStageActors());

		this.tweenManager = new TweenManager();

		this.title = new Label(title, getSkin());
		getStageUIActors().addActor(this.title);
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

	public OrthographicCamera getGameCamera() {
		if( gameCamera == null ) {
			Vector3 size = this.game.getWorldSize();
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
		Gdx.app.log("AbstractScreen", getName()+": show: called");
		this.multiplexer = new InputMultiplexer();
		this.multiplexer.addProcessor(getStageUIActors());
		this.multiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(this.multiplexer);

		this.tweenManager.killAll();
		Timeline timeline = createShowAnimation();
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
		Gdx.app.log("AbstractScreen", getName()+": hide: called");
		this.multiplexer.clear();

		this.tweenManager.killAll();
		Timeline timeline = createHideAnimation();
		if( timeline != null ) {
			Gdx.app.log("AbstractScreen", getName()+": setting up hide animation");
			timeline.setCallbackTriggers(TweenCallback.ANY);
			timeline.setCallback(new TweenCallback() {

				@Override
				public void onEvent(int arg0, BaseTween<?> arg1) {
					Gdx.app.log("AbstractScreen", getName()+": onEvent: "+arg0 );
					if( arg0 == TweenCallback.COMPLETE ){
						Gdx.app.log("AbstractScreen", getName()+": animation complete");
						getGame().notifyScreenHidden();
						//						dispose();
					}
				}
			});

			timeline.start(this.tweenManager);
		} else {
			getGame().notifyScreenHidden();
			dispose();
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
		if( stageActors == null ) {
			OrthographicCamera camera = getGameCamera();
			Vector3 size = this.game.getWorldSize();
			Viewport viewport = new FitViewport(size.x, size.y, camera);
			stageActors = new Stage(viewport);
		}
		return stageActors;
	}

	public Stage getStageUIActors() {
		if( stageUIActors == null ) {
			OrthographicCamera camera = getMenuCamera();
			Vector2 size = getMenuSize();
			Viewport viewport = new FitViewport(size.x, size.y, camera);
			stageUIActors = new Stage(viewport);
			this.centerPosition = new Vector2(size.x / 2f, size.y / 2f);
		}
		return stageUIActors;
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

	public void addUIElement(Actor actor) {
		getStageUIActors().addActor(actor);
	}

	protected Timeline createShowAnimation() {
		Timeline timelineParallel = Timeline.createParallel();
		Timeline timelineSequence = Timeline.createSequence();
		Stage stage = getStageUIActors();

		float x = (centerPosition.x) - (title.getWidth() / 2f);
		title.setPosition(x, stage.getHeight() + title.getHeight());
		Gdx.app.log("AbstractScreen", "createShowAnimation: title: x,y="+title.getX()+","+title.getY());
		timelineParallel.push(Tween.to(title, ActorAccessor.POSITION_Y, 0.8f)
				.target(700));

		float y = 600f;
		Array<Actor> actors = stage.getActors();
		for(int i = 0; i < actors.size; i++) {
			Actor actor = actors.get(i);
			if( actor != title ) {
				timelineSequence.push(createActorShowAnimation(actor, y));
				y -= actor.getHeight() + 20f;
			}
		}

		timelineParallel.push(timelineSequence);
		return timelineParallel;
	}

	protected Timeline createHideAnimation() {
		Stage stage = getStageUIActors();
		Timeline timeline = Timeline.createParallel();

		timeline.push(Tween.to(title, ActorAccessor.POSITION_Y, 0.8f)
				.target(stage.getHeight() + title.getHeight()));

		Array<Actor> actors = getStageUIActors().getActors();
		float y = -81f;
		for(int i = 1; i < actors.size; i++) {
			Actor actor = actors.get(i);
			timeline.push(createActorHideAnimation(actor, y));
			y -= actor.getHeight() + 20f;
		}

		return timeline;
	}

	private Tween createActorShowAnimation(Actor actor, float endYposition) {
		float x = (centerPosition.x) - (actor.getWidth() / 2f);
		actor.setPosition(x, -actor.getHeight());
		return Tween.to(actor, ActorAccessor.POSITION_Y, 0.2f)
				.target(endYposition);
	}

	private Tween createActorHideAnimation(Actor actor, float endYposition) {
		return Tween.to(actor, ActorAccessor.POSITION_Y, 0.2f)
				.target(endYposition);
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

	//	/**
	//	 * Called to create the animation when screen is displayed
	//	 * @return Timeline that contains the animation(s)
	//	 */
	//	abstract protected Timeline createShowAnimation();
	//	
	//	/**
	//	 * Called to create the animation when screen will be hidden
	//	 * @return Timeline that contains the animation(s)
	//	 */
	//	abstract protected Timeline createHideAnimation();
}
