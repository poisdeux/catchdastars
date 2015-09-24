/**
 * 
 * Copyright 2013 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
import com.strategames.engine.scenes.scene2d.ui.TextButton;

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
