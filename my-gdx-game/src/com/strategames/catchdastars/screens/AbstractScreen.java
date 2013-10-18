package com.strategames.catchdastars.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strategames.catchdastars.Game;
import com.strategames.ui.Dialog;

/**
 * TODO create a screen manager to reduce the amount of new objects created when a new screen must be shown
 * @author mbrekhof
 *
 */
public abstract class AbstractScreen implements Screen, InputProcessor
{
	private Game game;
	private InputMultiplexer multiplexer;
	protected BitmapFont font;
	protected SpriteBatch batch;
	private Skin skin;
	protected final Stage stageActors;
	protected final Stage stageUIActors;
	
	protected float screenHeight;
	protected float screenWidth;
	
	private Camera camera;
	
	public AbstractScreen(Game game)
	{
		this.game = game;

		this.stageActors = new Stage();
		this.stageUIActors = new Stage();

		Gdx.input.setCatchBackKey(true);

		this.multiplexer = new InputMultiplexer();
		this.multiplexer.addProcessor(this.stageUIActors);
		this.multiplexer.addProcessor(this);
	}
	
	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public Camera getCamera() {
		return camera;
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

	protected boolean isGameScreen()
	{
		return false;
	}

	// Screen implementation

	@Override
	public void show()
	{
		Gdx.input.setInputProcessor(this.multiplexer);
		setupUI(this.stageUIActors);
		setupActors(this.stageActors);
	}

	public float getScreenWidth() {
		return screenWidth;
	}
	
	public float getScreenHeight() {
		return screenHeight;
	}
	
	@Override
	public void render(
			float delta )
	{	
		this.camera.update();
		
		Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		
//		if( this.game.getGameState() == game.GAME_STATE_RUNNING ) {
			this.stageActors.act(delta);
//		}
			
		this.stageActors.draw();
		
		this.stageUIActors.act(delta);
		this.stageUIActors.draw();
	}

	@Override
	public void hide()
	{
		// dispose the resources by default
		dispose();
	}

	@Override
	public void resize(int width, int height) {
		float aspectRatio = (float) height / (float) width;
		this.screenWidth = 800f;
		this.screenHeight = this.screenWidth * aspectRatio;
		this.camera = new OrthographicCamera(this.screenWidth, this.screenHeight);
		this.camera.position.x = this.screenWidth/2f;
		this.camera.position.y = this.screenHeight/2f;
		
		this.stageActors.setCamera(this.camera);
		this.stageUIActors.setCamera(this.camera);
		
		this.stageUIActors.setViewport(this.screenWidth, this.screenHeight, true);
		this.stageActors.setViewport(this.screenWidth, this.screenHeight, true);
//		this.stageActors.setViewport(Game.convertWorldToBox(this.screenWidth) , 
//				Game.convertWorldToBox(this.screenHeight), true);
	}

	@Override
	public void dispose()
	{
		this.stageActors.dispose();
		this.stageUIActors.dispose();
		if( font != null ) font.dispose();
		if( batch != null ) batch.dispose();
		if( skin != null ) skin.dispose();
	}
	
	public Stage getStageActors() {
		return this.stageActors;
	}

	public Stage getStageUIElements() {
		return this.stageUIActors;
	}

	public void showDialog(Dialog dialog) {
		dialog.show(this.stageUIActors);
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
//		Gdx.app.log("AbstractScreen", "mouseMoved");
		return false;
	}
	
	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	/**
	 * Implement to override default back navigation handling
	 * by Game class
	 * @return true if handled, false otherwise
	 */
	protected boolean handleBackNavigation() {
		return false;
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
