package com.strategames.catchdastars.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobjects.Balloon;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.sounds.BalloonGrowSound;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.tweens.ActorAccessor;

public class GameCompleteScreen extends AbstractScreen implements TweenCallback {

	private Stage stageActors;
	private SoundEffect balloonGrowSound = new BalloonGrowSound();;
	private Balloon balloon;
	
	public GameCompleteScreen(Game game, Stage stageActors) {
		super(game, null);
		this.stageActors = stageActors;
	}

	@Override
	protected void setupUI(Stage stage) {
		
	}

	@Override
	protected void setupActors(Stage stage) {
		Array<Actor> actors = stageActors.getActors();
		for(int i = 0; i <  actors.size; i++) {
			Actor actor = actors.get(i);
			stage.addActor(actor);
			Gdx.app.log("GameCompleteScreen", "setupActors: actor="+actor);
			if( actor instanceof Balloon ) {
				balloon = (Balloon) actor;
			}
		}
	}
	
	@Override
	protected Timeline showAnimation() {
		Timeline timeline = Timeline.createSequence();
//		createBlowUpAnimation(timeline);
		return timeline;
	}

	private void createBlowUpAnimation(Timeline timeline) {
		Tween tween = Tween.to(balloon, ActorAccessor.SCALE, 2f)
				.target(2f, 2f)
				.ease(TweenEquations.easeInOutQuad)
				.setCallbackTriggers(TweenCallback.START)
				.setCallback(this);
		timeline.push(tween);
		timeline.pushPause(0.5f);
		tween = Tween.to(balloon, ActorAccessor.SCALE, 2f)
				.target(4f,4f)
				.ease(TweenEquations.easeInOutQuad)
				.setCallbackTriggers(TweenCallback.START)
				.setCallback(this);
		timeline.push(tween);
		timeline.pushPause(0.5f);
		tween = Tween.to(balloon, ActorAccessor.SCALE, 2f)
				.target(8f, 8f)
				.ease(TweenEquations.easeInOutQuad)
				.setCallbackTriggers(TweenCallback.START)
				.setCallback(this);
		timeline.push(tween);
		timeline.pushPause(0.5f);
		tween = Tween.to(balloon, ActorAccessor.SCALE, 2f)
				.target(16f, 16f)
				.ease(TweenEquations.easeInOutQuad)
				.setCallbackTriggers(TweenCallback.START)
				.setCallback(this);
		timeline.push(tween);
	}

	@Override
	public void onEvent(int arg0, BaseTween<?> arg1) {
		if( arg0 == TweenCallback.START ) {
			this.balloonGrowSound.play();
		} else if( arg0 == TweenCallback.END ) {
			this.balloonGrowSound.stop();
		}
	}
}
