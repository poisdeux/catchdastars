package com.strategames.engine.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.game.Game;
import com.strategames.engine.tweens.ActorAccessor;

public class MainMenuScreen extends AbstractScreen {

	private TweenManager manager;
	private Vector2 centerPosition;
	
	private Label label;
	private Array<TextButton> buttons;

	private float buttonHeightPlusPadding;
	
	private int amountOfAnimations;
	
	public MainMenuScreen(Game game) {
		super(game);
		this.buttons = new Array<TextButton>();
	}

	@Override
	protected void setupUI(Stage stage) {
		final Game game = getGame();
		Skin skin = getSkin();
		this.manager = new TweenManager();
		this.centerPosition = new Vector2(stage.getWidth() / 2f, stage.getHeight() / 2f);

		label = new Label("Welcome to Catch Da Stars!", skin);
		float x = (centerPosition.x) - (label.getWidth() / 2f);
		label.setPosition(x, stage.getHeight() + label.getHeight());
		Tween.to(label, ActorAccessor.POSITION_Y, 0.8f)
		.target(700)
		.start(manager);
		stage.addActor(label);
		
		TextButton button = new TextButton( "New game", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				game.setTotalScore(0);
				game.startLevel(1);
			}
		});
		stage.addActor(button);
		buttons.add(button);
		buttonHeightPlusPadding = button.getHeight() + 20f;
		
		
		button = new TextButton( "Settings", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().showSettings();
			}
		});
		stage.addActor(button);
		buttons.add(button);
		
		button = new TextButton( "High Scores", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
			}
		} );
		stage.addActor(button);
		buttons.add(button);
		
		button = new TextButton( "Game editor", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().showLevelEditorMenu();
			}
		} );
		stage.addActor(button);
		buttons.add(button);
	}
	
	@Override
	public void render(float delta) {
		this.manager.update(delta);
		super.render(delta);
	}
	
	@Override
	protected void setupActors(Stage stage) {
	}
	
	@Override
	protected boolean handleBackNavigation() {
		Gdx.app.exit();
		return true;
	}
	
	@Override
	protected Timeline createShowAnimation() {
		Stage stage = getStageUIActors();
		Timeline timeline = Timeline.createSequence();
		float y = 600f;
		
		for(TextButton button : this.buttons) {
			timeline.push(createTextButtonShowAnimation(button, y));
			stage.addActor(button);
			y -= buttonHeightPlusPadding;
		}
		
		return timeline;
	}

	@Override
	protected Timeline createHideAnimation() {
		Stage stage = getStageUIActors();
		Timeline timeline = Timeline.createParallel();
		
		timeline.push(Tween.to(label, ActorAccessor.POSITION_Y, 0.8f)
		.target(stage.getHeight() + label.getHeight()));
		
		float y = -buttonHeightPlusPadding;
		
		this.amountOfAnimations = this.buttons.size;
		
		for(TextButton button : this.buttons) {
			timeline.push(createTextButtonHideAnimation(button, y));
			y -= buttonHeightPlusPadding;
		}
		
		return timeline;
	}
	
	private Tween createTextButtonShowAnimation(TextButton button, float endYposition) {
		float x = (centerPosition.x) - (button.getWidth() / 2f);
		button.setPosition(x, -button.getHeight());
		return Tween.to(button, ActorAccessor.POSITION_Y, 0.2f)
	    .target(endYposition);
	}
	
	private Tween createTextButtonHideAnimation(TextButton button, float endYposition) {
		return Tween.to(button, ActorAccessor.POSITION_Y, 0.2f)
				.setCallback(new TweenCallback() {
					
					@Override
					public void onEvent(int arg0, BaseTween<?> arg1) {
						amountOfAnimations--;
						if( amountOfAnimations == 0 ) {
							Gdx.app.log("MainMenuScreen", "Last animation finished");
						}
					}
				})
	    .target(endYposition);
	}

	
}


