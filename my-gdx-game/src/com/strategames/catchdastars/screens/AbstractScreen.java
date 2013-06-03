package com.strategames.catchdastars.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strategames.catchdastars.Game;
import com.strategames.ui.Dialog;

public abstract class AbstractScreen implements Screen
{
	private Game game;
	protected BitmapFont font;
	protected SpriteBatch batch;
	protected Skin skin;
	protected final Stage stageActors;
	protected final Stage stageUIActors;
	
	public AbstractScreen(Game game)
	{
		this.game = game;
		
		this.stageActors = new Stage();
		this.stageUIActors = new Stage();
		
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
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
		setupUI(this.stageUIActors);
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
		
		this.stageUIActors.act();
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
		this.stageActors.setViewport( width * Game.WORLD_TO_BOX, height * Game.WORLD_TO_BOX, true );
		this.stageUIActors.setViewport( width, height, true );
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
