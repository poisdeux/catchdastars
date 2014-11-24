package com.strategames.ui.dialogs;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.ui.widgets.TextButton;

public class GameObjectPickerDialog extends Dialog {
	public static final int BUTTON_GAMEOBJECTSELECTED = 1;
	private ArrayList<TextButton> textButtons;
	private final Dialog.OnClickListener listener;
	private final GameEngine game;
	private GameObject selectedGameObject;
	private float IMAGEWIDTH = GameEngine.convertWorldToScreen(0.30f);
	
	public GameObjectPickerDialog(Stage stage, GameEngine game, Skin skin, final Dialog.OnClickListener listener) {
		super(stage, skin);
		this.textButtons = new ArrayList<TextButton>();
		this.listener = listener;
		this.game = game;
	}
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public Dialog create() {
		setPosition(0, 0);
		defaults().spaceBottom(2);
		row();
		
		Array<GameObject> gameObjects = this.game.getAvailableGameObjects();
		
		for(GameObject object : gameObjects ) {
			final GameObject gameObject = object;
			Drawable drawable = gameObject.getDrawable();
			ImageButton iButton = new ImageButton(drawable);
			Image image = iButton.getImage();
			image.setScale(IMAGEWIDTH / drawable.getMinWidth());
			iButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					selectedGameObject = gameObject;
					listener.onClick(GameObjectPickerDialog.this, BUTTON_GAMEOBJECTSELECTED);
					GameObjectPickerDialog.this.remove();
				}
			});
			add(iButton);

			row();
		}
		
		for( TextButton button : this.textButtons ) {
			add(button);
			row();
		}
		
		pack();
		
		return super.create();
	}
	
	public GameObject getSelectedGameObject() {
		return this.selectedGameObject;
	}
}
