package com.strategames.catchdastars.screens;

import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.GameFile;


public class LevelScreen extends AbstractScreen
{

	//	private final Profile profile;
	//	private final Level level;
	private Game game;
	
	public LevelScreen(
			Game game,
			int level )
	{
		this.game = game;
	}

	@Override
	protected boolean isGameScreen()
	{
		return true;
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		super.resize(width, height);
		
		this.game.setupStage(getStage());
		
		GameFile gameFile = new GameFile();
		gameFile.load(1);
//		gameFile.save(getStage(), 1);
		
		// add a fade-in effect to the whole stage
		//		stage.getRoot().getColor().a = 0f;
		//		stage.getRoot().addAction( Actions.fadeIn( 0.5f ) );
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);

		this.game.update(delta);
		
//		this.debugRenderer.render(world, camera.combined);	
	}

	@Override
	public void dispose() {
//		batch.dispose();
	}
}
