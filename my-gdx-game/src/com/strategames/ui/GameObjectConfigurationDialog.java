package com.strategames.ui;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
	private final GameObject object;
	private final Skin skin;
	private ArrayList<TextButton> textButtons;

	public GameObjectConfigurationDialog(final GameObject object, Skin skin) {
		super("", skin);
		this.object = object;
		this.skin = skin;
		this.textButtons = new ArrayList<TextButton>();
	}

	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public void show(Stage stage) {
		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();

		object.initializeConfigurationItems();

		ArrayList<ConfigurationItem> configurationItems = object.getConfigurationItems();

		if( configurationItems != null ) {
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

				row();
			}
		}

		for( TextButton button : this.textButtons ) {
			add(button).fill().expandX();
			row();
		}

		TextButton delButton = new TextButton("Delete object", skin);
		delButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				object.remove();
				GameObjectConfigurationDialog.this.remove();
			}
		});
		add(delButton).expand().left();

		TextButton closeButton = new TextButton("Close", skin);
		closeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameObjectConfigurationDialog.this.remove();
			}
		});
		add(closeButton).expand().bottom().right();

		row();

		pack();

		stage.addActor(this);
	}

	public void addButton(String name, final OnSelectListener listener) {
		final TextButton button = new TextButton(name, this.skin);
		button.setName(name);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				GameObjectConfigurationDialog.this.remove();
				listener.onPressedListener(button);
			}
		});
		this.textButtons.add(button);
	}
}
