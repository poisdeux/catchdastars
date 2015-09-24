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

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strategames.engine.scenes.scene2d.ui.TextButton;

public class ButtonsDialog extends Dialog {
	public static enum ORIENTATION {HORIZONTAL, VERTICAL};
	private ORIENTATION orientation;
	private ArrayList<TextButton> textButtons;

	/**
	 * Creates a simple buttons dialog with a title
	 * @param stage
	 * @param title
	 * @param skin
	 * @param orientation if buttons should be layed out vertically or horizontally
	 */
	public ButtonsDialog(Stage stage, Skin skin, ORIENTATION orientation) {
		super(stage, skin);
		this.textButtons = new ArrayList<TextButton>();
		this.orientation = orientation;
	}

	public void add(String text, EventListener listener) {
		TextButton tButton = new TextButton(text, getSkin());
		tButton.addListener(listener);
		this.textButtons.add(tButton);
	}

	/**
	 * Use this to create the actual dialog.
	 * Note that this needs to be called before {@link #
	 */
	public Dialog create() {
		defaults().spaceBottom(10);
		defaults().spaceTop(10);
		
		if( this.orientation == ORIENTATION.VERTICAL ) { 
			row().fill().expandX();

			for( TextButton button : this.textButtons ) {
				add(button);
				row().fill().expandX();
			}
		} else {
			for( TextButton button : this.textButtons ) {
				add(button);
			}
		}
		pack();
		return super.create();
	}
}
