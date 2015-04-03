package com.strategames.catchdastars.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.catchdastars.dialogs.LevelCompleteDialog;
import com.strategames.catchdastars.gameobjects.BalloonBlue;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.types.Text;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.storage.LevelLoader;
import com.strategames.engine.storage.LevelLoader.OnLevelLoadedListener;
import com.strategames.engine.tweens.ActorAccessor;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Score;
import com.strategames.engine.utils.Textures;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.dialogs.LevelFailedDialog;
import com.strategames.ui.dialogs.LevelPausedDialog;
import com.strategames.ui.helpers.FilledRectangleImage;
import com.strategames.ui.helpers.Screen;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

public class LevelScreen extends AbstractScreen implements OnClickListener, OnLevelLoadedListener
{	
	private Text levelImage;

	private Stage stageActors;
	private FilledRectangleImage filter;
	private Stage stage;
	private boolean imageStartAnimationFinished;
	private boolean levelLoaded;
	private Timeline levelStartAnimation;
	
	public LevelScreen(GameEngine game) {
		super(game);
	}

	@Override
	protected void setupUI(Stage stage) {
		this.stage = stage;
		
		Vector2 start = new Vector2();
		Vector2 end = new Vector2();
		Screen.getFullScreenCoordinates(stage, start, end);
		
		this.filter = new FilledRectangleImage(this.stage);
		this.filter.setPosition(start.x, start.y);
		this.filter.setWidth(end.x);
		this.filter.setHeight(end.y);
		this.filter.setColor(0f, 0f, 0f, 1f);
		stage.addActor(this.filter);
		
		this.levelImage = new Text();
		this.levelImage.setText("Get ready!");

		float imageWidth = this.levelImage.getWidth();
		float imageHeight = this.levelImage.getHeight();

		float x = (stage.getWidth() - imageWidth) / 2f;

		this.levelImage.setX(x);
		this.levelImage.setY(-imageHeight);

		stage.addActor(this.levelImage);
	}

	@Override
	protected void setupActors(Stage stage) {
		this.stageActors = stage;
	}

	@Override
	public void show() {
		super.show();
		getGameEngine().pauseGame();
		Game game = getGameEngine().getGame();
        Level level = LevelLoader.loadCompleted(game, game.getCurrentLevelPosition());
        if( level == null ) {
            level = LevelLoader.loadOriginal(game, game.getCurrentLevelPosition());
        }

		onLevelLoaded(level);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		getGameEngine().updateScreen(delta, this.stageActors);
	}

	@Override
	protected boolean handleBackNavigation() {
        if( Dialog.getAmountOfDialogsVisible() > 0 ) {
            return true;
        }

		LevelPausedDialog levelPausedDialog = new LevelPausedDialog(getStageUIActors(), getSkin());
		levelPausedDialog.setOnClickListener(this);
		levelPausedDialog.create();
        levelPausedDialog.show();

		// Make sure key is also sent to game engine as well
		return false;
	}

	@Override
	public void onClick(Dialog dialog, int which) {
		dialog.hide();
		if( dialog instanceof LevelPausedDialog ) {
			switch( which ) {
			case LevelPausedDialog.BUTTON_QUIT_CLICKED:
				getGameEngine().stopScreen();
				break;
			case LevelPausedDialog.BUTTON_RESUME_CLICKED:
				getGameEngine().resumeGame();
				break;
			case LevelPausedDialog.BUTTON_RETRY_CLICKED:
				resetStageActors();
				getGameEngine().resetLevel();
				break;
			}
		} else if( dialog instanceof ErrorDialog ) {
			switch( which ) {
			case ErrorDialog.BUTTON_CLOSE:
				getGameEngine().showMainMenu();
				break;
			}
		} else if( dialog instanceof LevelFailedDialog ) {
            switch( which ) {
                case LevelFailedDialog.BUTTON_QUIT_CLICKED:
                    getGameEngine().stopScreen();
                    break;
                case LevelFailedDialog.BUTTON_RETRY_CLICKED:
                    getGameEngine().resetLevel();
                    break;
            }
        } else if( dialog instanceof LevelCompleteDialog ) {
            switch( which ) {
                case LevelCompleteDialog.BUTTON_QUIT_CLICKED:
                    getGameEngine().stopScreen();
                    break;
                case LevelCompleteDialog.BUTTON_NEXT_CLICKED:
                    getGameEngine().startNextLevel();;
                    break;
            }
        }
	}

	@Override
	public void onLevelLoaded(final Level level) {
		if( level == null ) {
			ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Error loading level", getSkin());
			dialog.setOnClickListener(this);
			dialog.create();
			dialog.show();
			return;
		}
		
		
		GameEngine engine = getGameEngine();
		Game game = engine.getGame();
		game.setLevel(level);
		
		if( getGameEngine().setup(getStageActors()) ) {
			this.levelLoaded = true;
			startScreenCloseAnimation();
		} else {
			ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Error setting up level", getSkin());
			dialog.setOnClickListener(this);
			dialog.create();
			dialog.show();
		}
	}

    public void showLevelCompleteDialog(Score score) {
        LevelCompleteDialog levelCompleteDialog = new LevelCompleteDialog(getStageUIActors(), getSkin(), score);
        levelCompleteDialog.setOnClickListener(this);
        levelCompleteDialog.create();
        levelCompleteDialog.show();
    }

    public void showLevelFailedDialog() {
        LevelFailedDialog dialog = new LevelFailedDialog(getStageUIActors(), getSkin());
        dialog.setOnClickListener(this);
        dialog.create();
        dialog.show();
    }

	private void startScreenCloseAnimation() {
		if( imageStartAnimationFinished && levelLoaded ) {
			this.levelStartAnimation.resume();
		}
	}
	
	@Override
	protected Timeline showAnimation() {
		this.levelStartAnimation = Timeline.createSequence();
		
		float y = (stage.getHeight() + this.levelImage.getHeight()) / 2f;
		Tween slideinTween = Tween.to(this.levelImage, ActorAccessor.POSITION_Y, 1f).ease(TweenEquations.easeInOutQuint).target(y);
		slideinTween.setCallbackTriggers(TweenCallback.COMPLETE);
		slideinTween.setCallback(new TweenCallback() {
			
			@Override
			public void onEvent(int arg0, BaseTween<?> arg1) {
				levelStartAnimation.pause();
				imageStartAnimationFinished = true;
				startScreenCloseAnimation();
			}
		});
		this.levelStartAnimation.push(slideinTween);
		
		this.levelStartAnimation.push(Tween.to(this.filter, ActorAccessor.ALPHA, 0.5f).target(0f));
		
		y = stage.getHeight() + levelImage.getHeight();
		Tween slideoutTween = Tween.to(this.levelImage, ActorAccessor.POSITION_Y, 0.5f).ease(TweenEquations.easeInQuint).target(y);
		slideoutTween.setCallbackTriggers(TweenCallback.COMPLETE);
		slideoutTween.setCallback(new TweenCallback() {
			
			@Override
			public void onEvent(int arg0, BaseTween<?> arg1) {
				levelImage.remove();
				filter.remove();
				MusicPlayer.getInstance().playNext();
				getGameEngine().startGame();			}
		});
		levelStartAnimation.push(slideoutTween);
		
		return this.levelStartAnimation;
	}

	@Override
	protected Timeline hideAnimation() {
		return null;
	}

}
