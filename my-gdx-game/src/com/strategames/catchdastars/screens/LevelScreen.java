package com.strategames.catchdastars.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.Game.GameLoadedListener;
import com.strategames.catchdastars.actors.Text;
import com.strategames.catchdastars.utils.Animations;
import com.strategames.ui.LevelPauseDialog;


public class LevelScreen extends AbstractScreen implements InputProcessor, GameLoadedListener
{	
	private Game game;
	private Image levelImage;
	private boolean gameLoaded;
	private Stage stageActors;
	
	public LevelScreen(Game game ) {
		super(game);
		this.game = game;
		gameLoaded = false;
		game.loadLevelAsync(this);
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	protected boolean isGameScreen()
	{
		return true;
	}

	@Override
	protected void setupUI(Stage stage) {
		float scale = 2f;
		
		this.levelImage = new Text("Level " + this.game.getLevelNumber());
		this.levelImage.setScale(scale);
		
		float imageWidth = this.levelImage.getWidth() * scale;
		float imageHeight = this.levelImage.getHeight() * scale;
		
		float x = (stage.getWidth() - imageWidth) / 2f;
		float y = (stage.getHeight() + imageHeight) / 2f;

		this.levelImage.setX(x);
		this.levelImage.setY(-imageHeight);
		this.levelImage.addAction(sequence( moveTo(x, y, 0.5f, Interpolation.circleOut),
				new Action() {
					
					@Override
					public boolean act(float delta) {
						while( ! gameLoaded ) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
								return false;
							}
						}
						game.setupStage(stageActors);
						Animations.fadeIn(stageActors, 0.5f, Interpolation.circleIn);
						return true;
					}
				}, 
				fadeOut(0.5f, Interpolation.circleIn),
//				moveTo(x, stage.getHeight() + imageHeight, 0.5f, Interpolation.circleIn),
				new Action() {
					@Override
					public boolean act(float arg0) {
						Gdx.app.log("LevelScreen", "getGame().getGameState()="+getGame().getGameState());
						if( ! getGame().isPaused() ) { 
							getGame().startGame();
						}
						levelImage.remove();
						return true;
					}
				}
		));
		stage.addActor(this.levelImage);
	}

	@Override
	protected void setupActors(Stage stage) {
		this.stageActors = stage;
		this.game.stopGame();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		this.game.update(delta, super.stageActors);
	}

	@Override
	public boolean keyUp(int keycode) {
		if( ( keycode == Keys.BACK ) ||
				( keycode == Keys.ESCAPE ) ) {
			Game game = getGame();
			game.pauseGame();
			LevelPauseDialog dialog = new LevelPauseDialog(game, getSkin());
			dialog.show(getStageUIElements());
			return true;
		}
		return false;
	}

	@Override
	public void onGameLoaded() {
		this.gameLoaded = true;
	}
}
