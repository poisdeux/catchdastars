/**
 * 
 * Copyright 2014 Martijn Brekhof
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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.engine.utils.Level;

public class EditLevelDialog extends ButtonsDialog {
	public static final int BUTTON_DELETELEVEL_CLICKED = 1;
	public static final int BUTTON_CHANGENAME_CLICKED = 2;
	public static final int BUTTON_COPY_CLICKED = 3;
	public static final int BUTTON_CHANGELEVELNUMBER_CLICKED = 4;
	public static final int BUTTON_CLOSE_CLICKED = BUTTON_POSITIVE;
	
	private Level level;
	
	public EditLevelDialog(Stage stage, Skin skin, Level level) {
		super(stage, skin, ORIENTATION.VERTICAL);
		this.level = level;
	}
	
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public Dialog create() {
		add("Delete level", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_DELETELEVEL_CLICKED);
			}
		});

		add("Copy level", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_COPY_CLICKED);
			}
		});
		
		setNegativeButton("Close", new OnClickListener() {

			@Override
			public void onClick(Dialog dialog, int which) {
				notifyListener(BUTTON_CLOSE_CLICKED);
			}
		});
		
		return super.create();
	}
	
	
}
