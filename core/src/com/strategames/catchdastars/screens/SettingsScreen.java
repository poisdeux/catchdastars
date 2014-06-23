package com.strategames.catchdastars.screens;


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
import com.strategames.catchdastars.utils.MusicPlayer;
import com.strategames.catchdastars.utils.Sounds;

public class SettingsScreen extends AbstractScreen {


	public SettingsScreen(Game game) {
		super(game);
	}

	@Override
	protected void setupUI(Stage stage) {
		Skin skin = getSkin();
		
		Table table = new Table( getSkin() );
		table.setFillParent(true);
		table.add( "Settings" ).spaceBottom( 50 );
		table.row();

		Label label = new Label("SFX volume", skin);
		table.add(label);
		
		Slider slider = new Slider(0f, 1f, 0.01f, false, skin);
		slider.setValue(Sounds.getVolume());
		slider.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Slider slider = (Slider) actor;
				Sounds.setVolume(slider.getValue());	
			}
		});
		
		table.add( slider ).uniform().fill().spaceBottom( 10 );
		table.row();

		label = new Label("Music volume", skin);
		table.add(label);
		
		final MusicPlayer musicPlayer = MusicPlayer.getInstance();
		slider = new Slider(0f, 1f, 0.01f, false, skin);
		slider.setValue(musicPlayer.getVolume());
		slider.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Slider slider = (Slider) actor;
				musicPlayer.setVolume(slider.getValue());	
			}
		});
		
		table.add( slider ).uniform().fill().spaceBottom( 10 );
		table.row();

		Button button = new TextButton( "Select music", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
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
}


