package com.strategames.ui;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.interfaces.OnSelectListener;

public class GameObjectPickerDialog extends Window {
	private final Skin skin;
	private ArrayList<TextButton> textButtons;
	private final OnSelectListener listener;
	private final Game game;
	
	public GameObjectPickerDialog(Game game, Skin skin, final OnSelectListener listener) {
		super("Select a game object", skin);
		this.skin = skin;
		this.textButtons = new ArrayList<TextButton>();
		this.listener = listener;
		this.game = game;
	}
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public void show(Stage stage) {
		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();
		
		ArrayList<GameObject> gameObjects = this.game.getAvailableGameObjects();
		
		for(GameObject object : gameObjects ) {
			final GameObject gameObject = object;
			ImageButton iButton = new ImageButton(gameObject.getDrawable());
			iButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					listener.onObjectSelectListener(gameObject);
					GameObjectPickerDialog.this.remove();
				}
			});
			add(iButton);
			
			TextButton tButton = new TextButton(gameObject.getName(), skin);
			tButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					listener.onObjectSelectListener(gameObject);
					GameObjectPickerDialog.this.remove();
				}
			});
			add(tButton);
			row().fill().expandX();
		}
		
		for( TextButton button : this.textButtons ) {
			add(button);
			row().fill().expandX();
		}
		
		final TextButton cancelButton = new TextButton("Cancel", skin);
		cancelButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameObjectPickerDialog.this.remove();
			}
		});
		add(cancelButton);
		pack();
		
		stage.addActor(this);
	}
	
	public void addButton(String name, final OnSelectListener listener) {
		final TextButton button = new TextButton(name, this.skin);
		button.setName(name);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameObjectPickerDialog.this.remove();
				listener.onPressedListener(button);
			}
		});
		this.textButtons.add(button);
	}
}
