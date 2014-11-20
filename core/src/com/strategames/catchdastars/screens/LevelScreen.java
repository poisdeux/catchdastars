package com.strategames.catchdastars.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobject.types.Text;
import com.strategames.engine.scenes.scene2d.Stage;
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
import com.strategames.ui.helpers.Screen;

public class LevelScreen extends AbstractScreen implements OnClickListener, OnLevelLoadedListener
{	
	private Text levelImage;
	private LevelPausedDialog levelPausedDialog;
	private Stage stageActors;
	private FilledRectangleImage filter;
	private Stage stage;
	private boolean imageStartAnimationFinished;
	private boolean levelLoaded;
	private Timeline levelStartAnimation;
	
	public LevelScreen(Game game) {
		super(game, null);
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
		this.levelImage.setScale(2f);

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
		getGame().pauseGame();
		getGame().loadLevel(this);
	}
	@Override
	public void render(float delta) {
		super.render(delta);
		getGame().updateScreen(delta, this.stageActors);
	}

	@Override
	protected boolean handleBackNavigation() {
		if( Dialog.getAmountOfDialogsVisible() > 0 ) {
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
			case LevelPausedDialog.BUTTON_RETRY_CLICKED:
				getGame().resetLevel();
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
		
		this.levelImage.setText(level.getName());
		
		if( getGame().setup(getStageActors()) ) {
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
				getGame().startGame();			}
		});
		levelStartAnimation.push(slideoutTween);
		
		return this.levelStartAnimation;
	}

	@Override
	protected Timeline hideAnimation() {
		return null;
	}
}
