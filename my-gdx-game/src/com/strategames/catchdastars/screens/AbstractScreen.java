package com.strategames.catchdastars.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strategames.catchdastars.Game;

public abstract class AbstractScreen implements Screen
{
	private Game game;
	protected BitmapFont font;
	protected SpriteBatch batch;
	protected Skin skin;
	private final Stage stageActors;
	private final Stage stageUIElements;
	
	public AbstractScreen(Game game)
	{
		this.game = game;
		
		this.stageActors = new Stage();
		this.stageUIElements = new Stage();
		
		Gdx.input.setCatchBackKey(false);
	}

	protected Game getGame() {
		return this.game;
	}
	
	protected String getName()
	{
		return getClass().getSimpleName();
	}

	public BitmapFont getFont()
	{
		if( font == null ) {
			font = new BitmapFont();
		}
		return font;
	}

	public SpriteBatch getBatch()
	{
		if( batch == null ) {
			batch = new SpriteBatch();
		}
		return batch;
	}

	protected Skin getSkin()
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
		Gdx.app.log( "AbstractScreen", "Showing screen: " + getName() );
		
		setupUI(this.stageUIElements);
		setupActors(this.stageActors);
	}

	@Override
	public void render(
			float delta )
	{		
		Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		// draw the actors
		this.stageActors.act();
		this.stageActors.draw();
		this.stageUIElements.draw();
	}

	@Override
	public void hide()
	{
		Gdx.app.log( "AbstractScreen", "Hiding screen: " + getName() );

		// dispose the resources by default
		dispose();
	}

	@Override
	public void pause()
	{
		Gdx.app.log( "AbstractScreen", "Pausing screen: " + getName() );
	}

	@Override
	public void resume()
	{
		Gdx.app.log( "AbstractScreen", "Resuming screen: " + getName() );
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log( "AbstractScreen", "Resizing screen: " + getName() );
		this.stageActors.setViewport( width, height, true );
		this.stageUIElements.setViewport( width, height, true );
	}
	
	@Override
	public void dispose()
	{
		Gdx.app.log( "AbstractScreen", "Disposing screen: " + getName() );
		this.stageActors.dispose();
		this.stageUIElements.dispose();
		if( font != null ) font.dispose();
		if( batch != null ) batch.dispose();
		if( skin != null ) skin.dispose();
	}
	
	public Stage getStageActors() {
		return this.stageActors;
	}
	
	public Stage getStageUIElements() {
		return this.stageUIElements;
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
