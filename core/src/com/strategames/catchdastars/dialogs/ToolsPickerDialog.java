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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.engine.scenes.scene2d.ui.TextButton;
import com.strategames.engine.utils.Level;
import com.strategames.ui.dialogs.Dialog;

public class ToolsPickerDialog extends Dialog {
	private final Skin skin;
	private final Level level;
	
	public ToolsPickerDialog(Stage stage, Level level, Skin skin) {
		super(stage, skin);
		this.skin = skin;
		this.level = level;
	}
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public Dialog create() {
		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();
		
		createChangeWorldSizeDialog();
		
		setPositiveButton("Close", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
			}
		});
		pack();
		
		return super.create();
	}
	
	private void createChangeWorldSizeDialog() {
		TextButton tButton = new TextButton("Change world size", skin);
		tButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ChangeWorldSizeDialog dialog = new ChangeWorldSizeDialog(stage, skin, level);
				dialog.setPosition(getX(), getY());
				dialog.setOnClickListener(getOnClickListener());
				dialog.create();
				dialog.show();
				ToolsPickerDialog.this.remove();
			}
		});
		add(tButton);
		row().fill().expandX();
	}
}
