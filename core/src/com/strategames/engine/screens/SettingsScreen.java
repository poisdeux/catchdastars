package com.strategames.engine.screens;


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
import com.strategames.catchdastars.Game;
import com.strategames.engine.utils.MusicPlayer;
import com.strategames.engine.utils.Settings;
import com.strategames.engine.utils.Sounds;

public class SettingsScreen extends AbstractScreen {


	public SettingsScreen(Game game) {
		super(game);
	}

	@Override
	protected void setupUI(Stage stage) {
		Skin skin = getSkin();
		final Settings settings = Settings.getInstance();
		
		Table table = new Table( getSkin() );
		table.setFillParent(true);
		table.add( "Settings" ).spaceBottom( 50 );
		table.row();

		Label label = new Label("SFX volume", skin);
		table.add(label);
		
		Slider slider = new Slider(0f, 1f, 0.01f, false, skin);
		slider.setValue(settings.getSfxVolume());
		slider.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float volume = ((Slider) actor).getValue();
				settings.setSfxVolume(volume);
				Sounds.getInstance().setVolume(volume);
			}
		});
		
		table.add( slider ).uniform().fill().spaceBottom( 10 );
		table.row();

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
		table.row();

		Button button = new TextButton( "Select music", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().selectMusicFiles();
			}
		} );
		table.add( button ).uniform().fill().spaceBottom( 10 );
		table.row();

		button = new TextButton( "Main menu", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				getGame().stopScreen();
			}
		} );
		table.add( button ).uniform().fill().spaceBottom( 10 );
		table.row();
		stage.addActor(table);
	}
	
	@Override
	protected void setupActors(Stage stage) {
	}
	
	@Override
	public void hide() {
		Settings.getInstance().save();
	}
}


