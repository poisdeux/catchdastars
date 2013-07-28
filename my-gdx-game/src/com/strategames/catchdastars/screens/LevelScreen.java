package com.strategames.catchdastars.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.Text;


public class LevelScreen extends AbstractScreen implements InputProcessor
{	
	private Game game;
	private Image levelImage;

	public LevelScreen(Game game ) {
		super(game);
		this.game = game;
	}

	@Override
	public void hide() {
		super.hide();

		//		this.game.disposeLevel();
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
				delay(0.5f), 
				moveTo(x, stage.getHeight() + imageHeight, 0.5f, Interpolation.circleIn),
				new Action() {
					@Override
					public boolean act(float arg0) {
						getGame().startGame();
						levelImage.remove();
						return true;
					}
				}
		));
		stage.addActor(this.levelImage);
	}

	@Override
	protected void setupActors(Stage stage) {
		this.game.setupStage(stage);
		this.game.pauseGame();
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
			getGame().pauseGame();
			return true;
		}
		return false;
	}
}
