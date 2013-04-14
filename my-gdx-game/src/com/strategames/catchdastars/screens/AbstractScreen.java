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
		this.stageUIElements = new Stage(0, 0, true);
		this.stageActors = new Stage(0, 0, true);
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
	}

	@Override
	public void render(
			float delta )
	{		
		Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		// draw the actors
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
}
