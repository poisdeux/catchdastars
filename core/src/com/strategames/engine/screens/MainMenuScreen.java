package com.strategames.engine.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.engine.game.Game;
import com.strategames.engine.tweens.ActorAccessor;

public class MainMenuScreen extends AbstractScreen {

	private TweenManager manager;
	private Vector2 centerPosition;
	
	public MainMenuScreen(Game game) {
		super(game);
	}

	@Override
	protected void setupUI(Stage stage) {
		final Game game = getGame();
		Skin skin = getSkin();
		this.manager = new TweenManager();
		this.centerPosition = new Vector2(stage.getWidth() / 2f, stage.getHeight() / 2f);
		Timeline timeline = Timeline.createSequence();
		
		float buttonHeightPlusPadding;
		Vector2 buttonPosition =new Vector2(100f, 600f);
		
		Label label = new Label("Welcome to Catch Da Stars!", skin);
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
		buttonHeightPlusPadding = button.getHeight() + 20f;
		timeline.push(createTextButtonTween(button, buttonPosition));
		stage.addActor(button);
		
		button = new TextButton( "Settings", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().showSettings();
			}
		});
		buttonPosition.y -= buttonHeightPlusPadding;
		timeline.push(createTextButtonTween(button, buttonPosition));
		stage.addActor(button);
		
		button = new TextButton( "High Scores", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
			}
		} );
		buttonPosition.y -= buttonHeightPlusPadding;
		timeline.push(createTextButtonTween(button, buttonPosition));
		stage.addActor(button);
		
		button = new TextButton( "Game editor", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().showLevelEditorMenu();
			}
		} );
		buttonPosition.y -= buttonHeightPlusPadding;
		timeline.push(createTextButtonTween(button, buttonPosition));
		stage.addActor(button);
		
		timeline.start(manager);
//		Gdx.input.setInputProcessor( stage );
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
	
	private Tween createTextButtonTween(TextButton button, Vector2 endPosition) {
		float x = (centerPosition.x) - (button.getWidth() / 2f);
		button.setPosition(x, -button.getHeight());
		return Tween.to(button, ActorAccessor.POSITION_Y, 0.2f)
	    .target(endPosition.y);
	}
}


