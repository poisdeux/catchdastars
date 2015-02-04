package com.strategames.catchdastars.screens.game;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.strategames.engine.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Settings;

public class SettingsScreen extends AbstractScreen {

	public SettingsScreen(GameEngine game) {
		super(game);
        setTitle(new Label("Settings", getSkin()));
	}

	@Override
	protected void setupUI(Stage stage) {
		Skin skin = getSkin();
		final Settings settings = Settings.getInstance();
		float x = stage.getWidth() / 2f;
		float y = 600f;
		
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
		table.setPosition(x - (table.getWidth() / 2f), y);
		table.setHeight(60f);
		stage.addActor(table);
		y -= 60f;
		
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
		table.setPosition(x - (table.getWidth() / 2f), y);
		table.setHeight(60f);
		stage.addActor(table);
		y -= 60f;
		
		Button button = new TextButton( "Select music", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGameEngine().selectMusicFiles();
			}
		} );
		button.setPosition(x - (button.getWidth() / 2f), y);
		stage.addActor(button);
		y -= 60f;
		
		button = new TextButton( "Exit", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGameEngine().stopScreen();
			}
		} );
		button.setPosition(x - (button.getWidth() / 2f), y);
		stage.addActor(button);
	}
	
	@Override
	protected void setupActors(Stage stage) {
	}
	
	@Override
	public void hide() {
		Settings.getInstance().save();
		super.hide();
	}
}


