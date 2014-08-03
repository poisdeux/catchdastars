package com.strategames.engine.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobjects.GameObject;
import com.strategames.engine.gameobjects.Text;
import com.strategames.engine.utils.Animations;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelLoader.OnLevelLoadedListener;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.dialogs.LevelPausedDialog;


public class LevelScreen extends AbstractScreen implements InputProcessor, OnClickListener, OnLevelLoadedListener
{	
	private Game game;
	private Image levelImage;
	private LevelPausedDialog levelPausedDialog;

	public LevelScreen(Game game) {
		super(game);
		this.game = game;
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
		this.levelImage.addAction(moveTo(x, y, 0.5f, Interpolation.circleOut));
		stage.addActor(this.levelImage);

		this.game.loadLevel(this);
	}

	@Override
	protected void setupActors(Stage stage) {
		this.game.pauseGame();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		this.game.update(delta, super.stageActors);
	}

	@Override
	protected boolean handleBackNavigation() {
		if( ! this.game.isRunning() ) { // prevent pausing game when game is complete or failed
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
	public void onClick(Dialog dialog, int which) {
		dialog.hide();
		if( dialog instanceof LevelPausedDialog ) {
			switch( which ) {
			case LevelPausedDialog.BUTTON_QUIT_CLICKED:
				this.game.stopScreen();
				break;
			case LevelPausedDialog.BUTTON_RESUME_CLICKED:
				this.game.resumeGame();
				break;
			}
		} else if( dialog instanceof ErrorDialog ) {
			switch( which ) {
			case ErrorDialog.BUTTON_CLOSE:
				this.game.stopScreen();
				break;
			}
		}
	}

	@Override
	public void onLevelLoaded(final Level level) {
		if( level == null ) {
			ErrorDialog dialog = new ErrorDialog(getStageUIElements(), "Error loading level", getSkin());
			dialog.setOnClickListener(this);
			dialog.create();
			dialog.show();
			return;
		}

		game.initialize();

		this.levelImage.addAction(sequence(new Action() {

			@Override
			public boolean act(float delta) {
				Animations.fadeIn(stageActors, 0.5f, Interpolation.circleIn);
				return true;
			}
		}, 
		fadeOut(0.5f),
		new Action() {

			@Override
			public boolean act(float delta) {
				levelImage.remove();
				ArrayList<GameObject> gameObjects = level.getGameObjects();
				Stage stage = getStageActors();
				
				for( GameObject gameObject : gameObjects ) {
					stage.addActor(gameObject);
				}
				game.startGame();
				MusicPlayer.getInstance().playNext();
				return true;
			}
		}));
	}
}
