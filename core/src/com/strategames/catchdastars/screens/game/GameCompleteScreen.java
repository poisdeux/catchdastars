package com.strategames.catchdastars.screens.game;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.dialogs.GameCompleteDialog;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Balloon;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.sounds.BalloonGrowSound;
import com.strategames.engine.sounds.BalloonPopSound;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.tweens.ActorAccessor;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.helpers.FilledRectangleImage;

public class GameCompleteScreen extends AbstractScreen implements TweenCallback {

	private SoundEffect balloonGrowSound = new BalloonGrowSound();
	private SoundEffect balloonPopSound = new BalloonPopSound();
	private FilledRectangleImage filter;
	
	public GameCompleteScreen(GameEngine game, Stage stageActors) {
		super(game, null, stageActors);
	}

	@Override
	protected void setupUI(Stage stage) {
		this.filter = new FilledRectangleImage(stage);
		this.filter.setColor(1f, 1f, 1f, 0f);
		this.filter.setWidth(stage.getWidth());
		this.filter.setHeight(stage.getHeight());
		stage.addActor(this.filter);
	}

	@Override
	protected void setupActors(Stage stage) {
		
	}
	
	@Override
	protected Timeline showAnimation() {
		final Timeline timeline = Timeline.createSequence();
		
		timeline.beginParallel();
		Stage stage = getStageActors();
		Array<Actor> balloons = new Array<Actor>();
		Array<Actor> actors = stage.getActors();
		for( int i = 0; i < actors.size; i++ ) {
			Actor actor = actors.get(i);
			if( actor instanceof Balloon ) {
				balloons.add(actor);
				actor.setOrigin(actor.getWidth() / 2f, actor.getHeight() / 2f);
				timeline.push(createBlowUpAnimation(stage, actor));
			}
		}
		timeline.end();
		
		Tween flashTween = Tween.from(this.filter, ActorAccessor.ALPHA, 1f)
				.target(1f)
				.ease(TweenEquations.easeInExpo)
				.setCallbackTriggers(TweenCallback.END)
				.setCallback(new TweenCallback() {
					
					@Override
					public void onEvent(int arg0, BaseTween<?> arg1) {
						if( arg0 == TweenCallback.END ) {
							GameCompleteDialog dialog = new GameCompleteDialog(getStageUIActors(), getSkin());
							dialog.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(Dialog dialog, int which) {
									getGameEngine().showMainMenu();
								}
							});
							dialog.create();
							dialog.show();
						}
					}
				});
		timeline.push(flashTween);
		
		//Make sure balloons are drawn last by removing
		//and re-adding balloons to stage
		for( int i = 0; i < balloons.size; i++ ) {
			Actor actor = balloons.get(i);
			actor.remove();
			stage.addActor(actor);
		}
		
		return timeline;
	}

	private Timeline createBlowUpAnimation(Stage stage, Actor actor) {
		Timeline timeline = Timeline.createSequence();
		Vector2 origPosition = new Vector2(actor.getX(), actor.getY());
		Vector2 center = new Vector2(stage.getWidth() / 2f, stage.getHeight() / 2f);
		float xDelta = center.x - actor.getX();
		float yDelta = center.y - actor.getY();
		
		int steps = 5;
		double inc = Math.pow(steps - 1, 2);
		
		Tween tweenGrow = Tween.to(actor, ActorAccessor.SCALE, 2f)
				.target(2f, 2f)
				.ease(TweenEquations.easeInOutQuad)
				.setCallbackTriggers(TweenCallback.START)
				.setCallback(this);
		Tween tweenMove = Tween.to(actor, ActorAccessor.POSITION_XY, 2f)
				.targetRelative(((float) (xDelta / inc)), ((float) (yDelta / inc)));
		timeline.beginParallel();
		timeline.push(tweenGrow);
		timeline.push(tweenMove);
		timeline.end();
		timeline.pushPause(0.5f);
		tweenGrow = Tween.to(actor, ActorAccessor.SCALE, 2f)
				.target(4f,4f)
				.ease(TweenEquations.easeInOutQuad)
				.setCallbackTriggers(TweenCallback.START)
				.setCallback(this);
		tweenMove = Tween.to(actor, ActorAccessor.POSITION_XY, 2f)
				.targetRelative((float) ((xDelta * 2.0 / inc)), (float) ((yDelta * 2.0 / inc)));
		timeline.beginParallel();
		timeline.push(tweenGrow);
		timeline.push(tweenMove);
		timeline.end();
		timeline.pushPause(0.5f);
		tweenGrow = Tween.to(actor, ActorAccessor.SCALE, 2f)
				.target(8f, 8f)
				.ease(TweenEquations.easeInOutQuad)
				.setCallbackTriggers(TweenCallback.START)
				.setCallback(this);
		tweenMove = Tween.to(actor, ActorAccessor.POSITION_XY, 2f)
				.targetRelative((float) ((xDelta * 4.0 / inc)), (float) ((yDelta * 4.0 / inc)));
		timeline.beginParallel();
		timeline.push(tweenGrow);
		timeline.push(tweenMove);
		timeline.end();
		timeline.pushPause(0.5f);
		tweenGrow = Tween.to(actor, ActorAccessor.SCALE, 2f)
				.target(16f, 16f)
				.ease(TweenEquations.easeInOutQuad)
				.setCallbackTriggers(TweenCallback.START)
				.setCallback(this);
		tweenMove = Tween.to(actor, ActorAccessor.POSITION_XY, 2f)
				.targetRelative((float) ((xDelta * 8.0 / inc)), (float) ((yDelta * 8.0 / inc)));
		timeline.beginParallel();
		timeline.push(tweenGrow);
		timeline.push(tweenMove);
		timeline.end();
		timeline.pushPause(0.5f);
		tweenGrow = Tween.to(actor, ActorAccessor.SCALE, 2f)
				.target(32f, 32f)
				.ease(TweenEquations.easeInOutQuad)
				.setCallbackTriggers(TweenCallback.START)
				.setCallback(this);
		tweenMove = Tween.to(actor, ActorAccessor.POSITION_XY, 2f)
				.target(origPosition.x + xDelta, origPosition.y + yDelta);
		timeline.beginParallel();
		timeline.push(tweenGrow);
		timeline.push(tweenMove);
		timeline.end();
		timeline.pushPause(0.5f);
		tweenGrow = Tween.to(actor, ActorAccessor.SCALE, 1f)
				.target(48f, 48f)
				.ease(TweenEquations.easeInOutQuad)
				.setCallbackTriggers(TweenCallback.START | TweenCallback.END)
				.setCallback(this);
		timeline.push(tweenGrow);
		return timeline;
	}

	@Override
	public void onEvent(int arg0, BaseTween<?> arg1) {
		if( arg0 == TweenCallback.START ) {
			this.balloonGrowSound.play();
		} else if( arg0 == TweenCallback.END ) {
			Stage stage = getStageActors();
			Array<Actor> actors = stage.getActors();
			for( int i = 0; i < actors.size; i++ ) {
				getGameEngine().deleteGameObject((GameObject) actors.get(i));
			}
			stage.clear();
			this.balloonPopSound.play();
			this.balloonGrowSound.stop();
		}
	}
}
