package com.strategames.ui.dialogs;

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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.catchdastars.utils.ConfigurationItem;
import com.strategames.ui.widgets.TextButton;

public class GameObjectConfigurationDialog extends Dialog {
	private final GameObject gameObject;
	
	private ArrayList<TextButton> textButtons;

	public GameObjectConfigurationDialog(Stage stage, final GameObject object, Skin skin) {
		super(stage, skin);
		this.gameObject = object;
		this.textButtons = new ArrayList<TextButton>();
	}

	public GameObject getGameObject() {
		return gameObject;
	}
	
	@Override
	public void create() {
		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();

		gameObject.initializeConfigurationItems();

		ArrayList<ConfigurationItem> configurationItems = gameObject.getConfigurationItems();

		if( configurationItems != null ) {
			for( ConfigurationItem item : configurationItems ) {
				final ConfigurationItem fItem = item;

				Label label = new Label(item.getName(), super.skin);
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

		int count = 0;
		for( TextButton button : this.textButtons ) {
			add(button).fill().expandX();
			if( count++ > 3 ) {
				row();
				count = 0;
			}
		}

		super.create();
	}
	
	public int addButton(String text, final Dialog.OnClickListener listener) {
		TextButton button = new TextButton(text, this.skin);
		this.textButtons.add(button);
		final int index = this.textButtons.indexOf(button);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.onClick(GameObjectConfigurationDialog.this, index);
			}
		});
		
		return index;
	}
}
