package com.strategames.catchdastars.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobjects.Text;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.tweens.ActorAccessor;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelLoader.OnLevelLoadedListener;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.dialogs.LevelPausedDialog;
import com.strategames.ui.helpers.FilledRectangleImage;

public class LevelScreen extends AbstractScreen implements OnClickListener, OnLevelLoadedListener
{	
	private Image levelImage;
	private LevelPausedDialog levelPausedDialog;
	private Stage stageActors;
	private FilledRectangleImage filter;
	private Stage stage;
	private boolean imageStartAnimationFinished;
	private boolean levelLoaded;
	private boolean gameEnded;
	private Timeline levelStartAnimation;
	
	public LevelScreen(Game game) {
		super(game, null);
	}

	@Override
	protected void setupUI(Stage stage) {
		this.stage = stage;
		Game game = getGame();
		this.filter = new FilledRectangleImage(stage);
		this.filter.setWidth(stage.getWidth());
		this.filter.setHeight(stage.getHeight());
		this.filter.setColor(0f, 0f, 0f, 1f);
		stage.addActor(this.filter);

		this.levelImage = new Text("Level " + game.getLevelNumber());
		this.levelImage.setScale(2f);

		float imageWidth = this.levelImage.getWidth();
		float imageHeight = this.levelImage.getHeight();

		float x = (stage.getWidth() - imageWidth) / 2f;

		this.levelImage.setX(x);
		this.levelImage.setY(-imageHeight);

		stage.addActor(this.levelImage);

//		setupStartAnimation();
	}

	@Override
	protected void setupActors(Stage stage) {
		this.stageActors = stage;
		getGame().pauseGame();
		getGame().loadLevel(this);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		Game game = getGame();
		if( game.isRunning() ) {
			game.updateScreen(delta, this.stageActors);
		} else {
			if( !this.gameEnded ) {
				if( game.isComplete() ) {
					game.showLevelCompleteDialog();
					this.gameEnded = true;
				} else if( game.isFailed() ) {
					game.showLevelFailedDialog();
					this.gameEnded = true;
				}
			}
		}
	}

	@Override
	protected boolean handleBackNavigation() {
		if( ! getGame().isRunning() ) { // prevent pausing game when game is complete or failed
			return true;
		}

		if( this.levelPausedDialog == null  ) {
			this.levelPausedDialog = new LevelPausedDialog(getStageUIActors(), getSkin());
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
				getGame().stopScreen();
				break;
			case LevelPausedDialog.BUTTON_RESUME_CLICKED:
				getGame().resumeGame();
				break;
			}
		} else if( dialog instanceof ErrorDialog ) {
			switch( which ) {
			case ErrorDialog.BUTTON_CLOSE:
				getGame().stopScreen();
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

		if( getGame().setup(this) ) {
			this.levelLoaded = true;
			startScreenCloseAnimation();
		} else {
			ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Error loading level", getSkin());
			dialog.setOnClickListener(this);
			dialog.create();
			dialog.show();
		}
	}

	private void startScreenCloseAnimation() {
		if( imageStartAnimationFinished && levelLoaded ) {
			this.levelStartAnimation.resume();
		}
	}

//	private void setupStartAnimation() {
//		float x = this.levelImage.getX();
//		float y = (stage.getHeight() + this.levelImage.getHeight()) / 2f;
//		this.levelImage.addAction(sequence(moveTo(x, y, 1f, Interpolation.circleOut),
//				new Action() {
//
//			@Override
//			public boolean act(float delta) {
//				imageStartAnimationFinished = true;
//				startScreenCloseAnimation();
//				return true;
//			}
//
//		}));
//	}

//	private void setupEndAnimation() {
//
//		this.filter.addAction(sequence(fadeOut(1f), 
//				new Action() {
//
//			@Override
//			public boolean act(float delta) {
//				levelImage.addAction(sequence(moveTo(levelImage.getX(), stage.getHeight() + levelImage.getHeight(), 0.5f, Interpolation.circleIn),
//						new Action() {
//
//					@Override
//					public boolean act(float delta) {
//						levelImage.remove();
//						filter.remove();
//						getGame().startGame();
//						MusicPlayer.getInstance().playNext();
//						return true;
//					}
//				}));
//				return true;
//			}
//		}));
//
//
//	}
	
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
				getGame().startGame();
			}
		});
		levelStartAnimation.push(slideoutTween);
		
		return this.levelStartAnimation;
	}

	@Override
	protected Timeline hideAnimation() {
		return null;
	}
}
