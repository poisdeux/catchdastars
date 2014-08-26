package com.strategames.engine.screens;


import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.game.Game;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.tweens.ActorAccessor;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Settings;

public class SettingsScreen extends AbstractScreen {

	private Label title;
	private Array<Actor> actors;
	private Vector2 centerPosition;
	private float buttonHeightPlusPadding;
	
	public SettingsScreen(Game game) {
		super(game, "Settings");
	}

	@Override
	protected void setupUI(Stage stage) {
		this.actors = new Array<Actor>();
		Skin skin = getSkin();
		final Settings settings = Settings.getInstance();
		
		this.centerPosition = new Vector2(stage.getWidth() / 2f, stage.getHeight() / 2f);
		
		this.title = new Label("Settings", skin);
		float x = (centerPosition.x) - (title.getWidth() / 2f);
		title.setPosition(x, stage.getHeight() + title.getHeight());
		stage.addActor(title);
		
		Table table = new Table();
		Label label = new Label("SFX volume", skin);
		table.add(label);
		
		Slider slider = new Slider(0f, 1f, 0.01f, false, skin);
		slider.setValue(settings.getSfxVolume());
		slider.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float volume = ((Slider) actor).getValue();
				settings.setSfxVolume(volume);
				SoundEffect.setVolume(volume);
			}
		});
		table.add( slider ).uniform().fill().spaceBottom( 10 );
		actors.add(table);
		stage.addActor(table);
		
		table = new Table();
		label = new Label("Music volume", skin);
		table.add(label);
		
		slider = new Slider(0f, 1f, 0.01f, false, skin);
		slider.setValue(settings.getMusicVolume());
		slider.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float volume = ((Slider) actor).getValue();
				settings.setMusicVolume(volume);
				MusicPlayer.getInstance().setVolume(volume);
			}
		});
		table.add( slider ).uniform().fill().spaceBottom( 10 );
		actors.add(table);
		stage.addActor(table);
		
		Button button = new TextButton( "Select music", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().selectMusicFiles();
			}
		} );
		actors.add(button);
		stage.addActor(button);
		buttonHeightPlusPadding = button.getHeight() + 20f;
		
		button = new TextButton( "Main menu", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().stopScreen();
			}
		} );
		actors.add(button);
		stage.addActor(button);
	}
	
	@Override
	protected void setupActors(Stage stage) {
	}
	
	@Override
	public void hide() {
		Settings.getInstance().save();
		super.hide();
		dispose();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		Gdx.app.log("SettingsScreen", "button: "+actors.get(0).getY()+" title="+title.getY());
	}
}


