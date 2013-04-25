package com.strategames.ui;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.interfaces.OnSelectListener;

public class GameObjectConfigurationDialog extends Window {

	public GameObjectConfigurationDialog(GameObject object, Skin skin, final OnSelectListener listener) {
		super("", skin);

		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();

		object.initializeConfigurationItems();
		
		ArrayList<ConfigurationItem> configurationItems = object.getConfigurationItems();

		if( configurationItems == null ) {
			Label label = new Label("No configuration options available", skin);
			add(label);
			row().fill().expandX();
		} else {
			for( ConfigurationItem item : configurationItems ) {
				final ConfigurationItem fItem = item;
				
				Label label = new Label(item.getName(), skin);
				add(label);

				if( item.getType() == ConfigurationItem.Type.TEXT ) {
					TextField tf = new TextField(String.valueOf(item.getValueText()), skin);
					final StringBuffer buffer = new StringBuffer();
					tf.setTextFieldListener(new TextFieldListener() {
						
						@Override
						public void keyTyped(TextField textField, char key) {
							if (key == '\n') {
								textField.getOnscreenKeyboard().show(false);
								fItem.setValueText(buffer.toString());
							}
							buffer.append(key);
						}
					});
					add(tf);
				} else if( item.getType() == ConfigurationItem.Type.NUMERIC ) {
					TextField tf = new TextField(String.valueOf(item.getValueNumeric()), skin);
					tf.setTextFieldFilter( new TextFieldFilter() {
						@Override
						public boolean acceptChar(TextField textField, char key) {
							return Character.isDigit(key);
						}
					} );
					
					final StringBuffer buffer = new StringBuffer();
					tf.setTextFieldListener(new TextFieldListener() {
						
						@Override
						public void keyTyped(TextField textField, char key) {
							if (key == '\n') {
								textField.getOnscreenKeyboard().show(false);
								fItem.setValueNumeric(Float.parseFloat(buffer.toString()));
							}
							buffer.append(key);
						}
					});
					add(tf);
				} else if( item.getType() == ConfigurationItem.Type.NUMERIC_RANGE ) {
					Slider slider = new Slider(item.getMinValue(), 
							item.getMaxValue(), item.getStepSize(), false, skin);
					slider.setValue(item.getValueNumeric());
					slider.addListener(new ChangeListener() {

						@Override
						public void changed(ChangeEvent event, Actor actor) {
							Slider slider = (Slider) actor;
							fItem.setValueNumeric(slider.getValue());	
						}
					});
					add(slider);
				}

				row().fill().expandX();
			}
		}

		TextButton cancelButton = new TextButton("Close", skin);
		cancelButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameObjectConfigurationDialog.this.remove();
			}
		});
		add(cancelButton);
		row().fill().expandX();

		pack();
	}
}
