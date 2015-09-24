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

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ConfirmationDialog extends Dialog {

	public static final int BUTTON_CLOSE = BUTTON_POSITIVE;
	
	/**
	 * Creates a simple confirmation dialog with a title and a Yes and No button
	 * <br/>
	 * Use {@link #setPositiveButton(String)} and {@link #setNegativeButton(String)} to 
	 * change the button text
	 * @param stage
	 * @param message
	 * @param skin
	 */
	public ConfirmationDialog(Stage stage, String message, Skin skin) {
		super(stage, skin);
		setPositiveButton("Yes", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
				notifyListener(BUTTON_POSITIVE);
			}
		});
		setNegativeButton("No", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
				notifyListener(BUTTON_NEGATIVE);
			}
		});
		setCenter(true);
		setMessage(message);
	}
}
