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

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.utils.Array;

public class TextInputDialog extends Dialog {
	private Array<String> inputFieldNames;
	private HashMap<String, StringBuffer> inputFieldValues;

	public interface OnCloseListener {
		/**
		 * Called when dialog is closed.
		 * @param dialog closed
		 * @param values containing entered values for the inputfields. Name of the inputfield is used as key.
		 */
		public void onClosed(Dialog dialog, HashMap<String, StringBuffer> values);
	}

	public interface OnInputReceivedListener {
		public void onInputReceived(TextInputDialog dialog, String name, String input);
	}

	public TextInputDialog(Stage stage, Skin skin) {
		super(stage, skin);
		inputFieldNames = new Array<String>();
		inputFieldValues = new HashMap<String, StringBuffer>();
		setPositiveButton("Close");
	}

	@Override
	public Dialog create() {
		for(String name : inputFieldNames ) {
			Table table = createInputField(name);
			if( table != null ) {
				add(table);
				row();
			}
		}
		return super.create();
	}

	/**
	 * Adds a new input field with the given name.
	 * <br/>
	 * You cannot add multiple fields with the same name.
	 * If you do add multiple fields with the same name, only one will be displayed.
	 * @param name of the field
	 */
	public void addInputField(String name) {
		this.inputFieldNames.add(name);
	}

	public void setOnCloseListener(final OnCloseListener listener) {
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
				listener.onClosed(dialog, inputFieldValues);
			}
		});
	}

	private Table createInputField(final String name) {
		if( inputFieldValues.get(name) != null ) {
			return null;
		}
		inputFieldValues.put(name, new StringBuffer());
		
		Table table = new Table(getSkin());

		TextField tf = new TextField("", getSkin());
		tf.setName(name);
		tf.setTextFieldListener(new TextFieldListener() {

			@Override
			public void keyTyped(TextField textField, char key) {
				if ( key == '\r' || key == '\n' ) {
					textField.next(true);
				} else {
					inputFieldValues.get(name).append(key);
				}
			}
		});
		Label label = new Label(name, getSkin());
		table.add(label);
		table.add(tf).width(200).right();
		return table;
	}
}
