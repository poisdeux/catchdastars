package com.strategames.catchdastars.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.Game.GameLoadedListener;
import com.strategames.catchdastars.actors.Text;
import com.strategames.catchdastars.utils.Animations;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.LevelPausedDialog;


public class LevelScreen extends AbstractScreen implements InputProcessor, GameLoadedListener, OnClickListener
{	
	private Game game;
	private Image levelImage;
	private boolean gameLoaded;
	private Stage stageActors;
	private LevelPausedDialog levelPausedDialog;

	public LevelScreen(AbstractScreen previousScreen, Game game) {
		super(previousScreen, game);
		this.game = game;
		gameLoaded = false;
		game.loadLevelAsync(this);
		//		game.loadLevel();
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

		this.levelImage = new Text("Level " + this.game.getLevelNumber());
		this.levelImage.setScale(2f);

		float imageWidth = this.levelImage.getWidth();
		float imageHeight = this.levelImage.getHeight();

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
		this.stageActors = stage;
		this.game.pauseGame();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		this.game.update(delta, super.stageActors);
	}

	@Override
	protected boolean handleBackNavigation() {
		if( game.isPaused() ) { // prevent pausing game when game is complete or failed
			return true;
		}
		
		if( this.levelPausedDialog == null  ) {
			this.levelPausedDialog = new LevelPausedDialog(super.stageUIActors, getSkin());
			this.levelPausedDialog.setOnClickListener(this);
			this.levelPausedDialog.create();
		}
		if( ! this.levelPausedDialog.isVisible() ) {
			this.levelPausedDialog.show();
		}

		// Make sure key is also sent to game engine as well
		return false;
	}

	@Override
	public void onGameLoaded() {
		this.gameLoaded = true;
	}

	@Override
	public void onClick(Dialog dialog, int which) {
		dialog.hide();
		if( dialog instanceof LevelPausedDialog ) {
			switch( which ) {
			case LevelPausedDialog.BUTTON_QUIT_CLICKED:
				getGame().setScreen(getPreviousScreen());
				break;
			case LevelPausedDialog.BUTTON_RESUME_CLICKED:
				game.resumeGame();
				break;
			default:
			}
		}
	}
}
