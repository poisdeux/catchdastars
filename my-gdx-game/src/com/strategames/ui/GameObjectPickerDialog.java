package com.strategames.ui;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.GameObject;

public class GameObjectPickerDialog extends Window {

	public interface SelectListener {
		public void onSelectListener(GameObject object);
	}
	
	public GameObjectPickerDialog(Game game, Skin skin, final SelectListener listener) {
		super("Select a game object", skin);
		
		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();
		
		ArrayList<GameObject> gameObjects = game.getAvailableGameObjects();
		
		for(GameObject object : gameObjects ) {
			final GameObject gameObject = object;
			ImageButton iButton = new ImageButton(gameObject.getDrawable());
			iButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					listener.onSelectListener(gameObject);
					GameObjectPickerDialog.this.remove();
				}
			});
			add(iButton);
			
			TextButton tButton = new TextButton(gameObject.getName(), skin);
			tButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					listener.onSelectListener(gameObject);
					GameObjectPickerDialog.this.remove();
				}
			});
			add(tButton);
			row().fill().expandX();
		}
		pack();
	}
}
