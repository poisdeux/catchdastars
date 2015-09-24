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

package com.strategames.catchdastars.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.engine.utils.LevelEditorPreferences;
import com.strategames.ui.dialogs.Dialog;

public class LevelEditorOptionsDialog extends Dialog {
	public static final int CHECKBOX_SNAPTOGRID = 1;
	public static final int CHECKBOX_DISPLAYGRID = 2;
	
	private Dialog.OnClickListener listener;
	
	public LevelEditorOptionsDialog(Stage stage, Skin skin, Dialog.OnClickListener listener) {
		super(stage, skin);
		
		this.listener = listener;
	}
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public Dialog create() {
		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();
		
		createSnapToGridTool();
		
		setPositiveButton("Close", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
			}
		});
		pack();
		
		return super.create();
	}
	
	private void createSnapToGridTool() {
		CheckBox cBox = new CheckBox("Snap to grid", getSkin());
		cBox.setChecked(LevelEditorPreferences.snapToGridEnabled());
		cBox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if( LevelEditorPreferences.snapToGridEnabled() ) {
					LevelEditorPreferences.snapToGrid(false);
				} else {
					LevelEditorPreferences.snapToGrid(true);
				}
				listener.onClick(LevelEditorOptionsDialog.this, CHECKBOX_SNAPTOGRID);
			}
		});
		add(cBox);
		
		cBox = new CheckBox("Display grid", getSkin());
		cBox.setChecked(LevelEditorPreferences.displayGridEnabled());
		cBox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if( LevelEditorPreferences.displayGridEnabled() ) {
					LevelEditorPreferences.displayGrid(false);
				} else {
					LevelEditorPreferences.displayGrid(true);
				}
				listener.onClick(LevelEditorOptionsDialog.this, CHECKBOX_DISPLAYGRID);
			}
		});
		add(cBox);
		row().fill().expandX();
	}
}
