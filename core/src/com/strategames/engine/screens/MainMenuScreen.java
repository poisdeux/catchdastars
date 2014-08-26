package com.strategames.engine.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

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

	private Vector2 centerPosition;

	private Label label;
	private Array<TextButton> buttons;

	private float buttonHeightPlusPadding;

	public MainMenuScreen(Game game) {
		super(game);
	}

	@Override
	protected void setupUI(Stage stage) {
		this.buttons = new Array<TextButton>();
		final Game game = getGame();
		Skin skin = getSkin();
		this.centerPosition = new Vector2(stage.getWidth() / 2f, stage.getHeight() / 2f);

		label = new Label("Welcome to Catch Da Stars!", skin);
		float x = (centerPosition.x) - (label.getWidth() / 2f);
		label.setPosition(x, stage.getHeight() + label.getHeight());
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
	protected void setupActors(Stage stage) {
	}

	@Override
	protected boolean handleBackNavigation() {
		Gdx.app.exit();
		return true;
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		Gdx.app.log("MainMenuScreen", "render: label (x,y)="+label.getY()+", "+label.getX());
	}
	
	@Override
	protected Timeline createShowAnimation() {
		Timeline timelineParallel = Timeline.createParallel();
		Timeline timelineSequence = Timeline.createSequence();
		
		timelineParallel.push(Tween.to(label, ActorAccessor.POSITION_Y, 0.8f)
				.target(700));

		float y = 600f;
		for(TextButton button : this.buttons) {
			timelineSequence.push(createTextButtonShowAnimation(button, y));
			y -= buttonHeightPlusPadding;
		}

		timelineParallel.push(timelineSequence);
		return timelineParallel;
	}

	@Override
	protected Timeline createHideAnimation() {
		Stage stage = getStageUIActors();
		Timeline timeline = Timeline.createParallel();

		timeline.push(Tween.to(label, ActorAccessor.POSITION_Y, 0.8f)
				.target(stage.getHeight() + label.getHeight()));

		float y = -buttonHeightPlusPadding;

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
				.target(endYposition);
	}
}


