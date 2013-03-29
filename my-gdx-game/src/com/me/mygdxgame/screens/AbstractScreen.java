package com.me.mygdxgame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class AbstractScreen implements Screen
{
	protected final Game game;
	protected final Stage stage;

	protected BitmapFont font;
	protected SpriteBatch batch;
	protected Skin skin;

	private Table table;
	
	public AbstractScreen(
			Game game )
	{
		this.game = game;
		this.stage = new Stage( 0, 0, true );
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
			skin = new Skin( Gdx.files.internal( "uiskin.json" ) );
		}
		return skin;
	}

	protected Table getTable()
	{
		if( table == null ) {
			table = new Table( getSkin() );
			table.setFillParent( true );
			stage.addActor( table );
		}
		return table;
	}

	// Screen implementation

	@Override
	public void show()
	{
		Gdx.app.log( "AbstractScreen", "Showing screen: " + getName() );

		// set the input processor
		Gdx.input.setInputProcessor( stage );
	}

	@Override
	public void resize(
			int width,
			int height )
	{
		Gdx.app.log( "AbstractScreen", "Resizing screen: " + getName() + " to: " + width + " x " + height );

		// resize and clear the stage
		stage.setViewport( width, height, true );
		stage.clear();
	}

	@Override
	public void render(
			float delta )
	{
		// (1) process the game logic

		// update the actors
		stage.act( delta );

		// (2) draw the result

		// clear the screen with the given RGB color (black)
		Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

		// draw the actors
		stage.draw();
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
	public void dispose()
	{
		Gdx.app.log( "AbstractScreen", "Disposing screen: " + getName() );
		stage.dispose();
		if( font != null ) font.dispose();
		if( batch != null ) batch.dispose();
		if( skin != null ) skin.dispose();
	}
}
