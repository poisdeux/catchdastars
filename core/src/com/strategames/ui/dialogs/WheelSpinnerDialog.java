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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * A table that holds a title, a spinner, and a cancel button
 * @author mbrekhof
 *
 */
public class WheelSpinnerDialog extends Dialog {
	public static final int BUTTON_CANCELED_CLICKED = BUTTON_NEGATIVE;
	public static final int ITEM_SELECTED = BUTTON_USER1;
	
	private String[] items;
	private Skin skin;
	private String title;
	private int selectedItem;
	
	public WheelSpinnerDialog(Stage stage, Skin skin, String title, String[] items) {
		super(stage, skin);
		this.stage = stage;
		this.items = items;
		this.skin = skin;
		this.title = title;
	}

	public int getSelectedItem() {
		return selectedItem;
	}
	
	public Dialog create() {
		setWidth(150);
		setHeight(200);
		
		if( this.title != null ) {
			Label label = new Label(this.title, this.skin);
			add(label).top();
			row();
		}

		Table buttonTable = new Table(this.skin);
		for(int i = 0; i < items.length; i++) {
			final TextButton button = new TextButton(items[i], this.skin);
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					selectedItem = Integer.parseInt(button.getText().toString());
					OnClickListener listener = getOnClickListener();
					if( listener != null ) {
						listener.onClick(WheelSpinnerDialog.this, ITEM_SELECTED);
					}
				}
			});
			buttonTable.add(button).width(50);
			buttonTable.row();
		}

		ScrollPane scrollPane = new ScrollPane(buttonTable, this.skin);
		add(scrollPane).maxHeight(150);
		row();

		setNegativeButton("Cancel");
		
		return super.create();
	}
}
