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

public class LevelPausedDialog extends GameStateDialog {

	public static final int BUTTON_QUIT_CLICKED = BUTTON_NEGATIVE;
	public static final int BUTTON_RESUME_CLICKED = BUTTON_POSITIVE;
	public static final int BUTTON_RETRY_CLICKED = BUTTON_NEUTRAL;
	
	public LevelPausedDialog(Stage stage, Skin skin) {
		super("Level paused", stage, skin);
		setNegativeButton("Quit");
		setNeutralButton("Retry");
		setPositiveButton("Resume");
	}
}
