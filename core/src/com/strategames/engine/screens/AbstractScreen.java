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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.strategames.engine.game.Game;
import com.strategames.engine.sounds.SoundEffect;

public abstract class AbstractScreen implements Screen, InputProcessor
{
	private Game game;
	private InputMultiplexer multiplexer;
	private static Skin skin;
	private static Stage stageActors;
	private static Stage stageUIActors;

	private static OrthographicCamera menuCamera;
	private static OrthographicCamera gameCamera;

	private static Vector2 menuSize;

	public AbstractScreen(Game game)
	{
		this.game = game;

		Gdx.input.setCatchBackKey(true);

		this.multiplexer = new InputMultiplexer();
		this.multiplexer.addProcessor(getStageUIActors());
		this.multiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(this.multiplexer);
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
		setupUI(getStageUIActors());
		setupActors(getStageActors());
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
	}

	@Override
	public void hide()
	{
		dispose();
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

	public static void setStageActors(Stage stageActors) {
		AbstractScreen.stageActors = stageActors;
	}

	public Stage getStageUIActors() {
		if( stageUIActors == null ) {
			OrthographicCamera camera = getMenuCamera();
			Vector2 size = getMenuSize();
			Viewport viewport = new FitViewport(size.x, size.y, camera);
			stageUIActors = new Stage(viewport);
		}
		return stageUIActors;
	}

	public static void setStageUIActors(Stage stageUIActors) {
		AbstractScreen.stageUIActors = stageUIActors;
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
