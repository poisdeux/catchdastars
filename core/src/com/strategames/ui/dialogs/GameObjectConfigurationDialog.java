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
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.utils.ConfigurationItem;
import com.strategames.ui.widgets.TextButton;

public class GameObjectConfigurationDialog extends Dialog {
	public final static int BUTTON_COPY_CLICKED = 1;
	public final static int BUTTON_DELETE_CLICKED = 2;
	public final static int BUTTON_CLOSE_CLICKED = BUTTON_POSITIVE;
	
	private GameObject gameObject;

	public GameObjectConfigurationDialog(Stage stage, final GameObject object, Skin skin) {
		super(stage, skin);
		this.gameObject = object;
	}

	public GameObject getGameObject() {
		return gameObject;
	}
	
	/**
	 * Sets the gameobject the dialog should edit. This can be useful
	 * if you need to change the gameobject while the dialog is still open.
	 * For instance when copying an object and you want the copy to receive
	 * all changes
	 * @param gameObject
	 */
	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}
	
	@Override
	public void create() {
		setCenter(true);
		setBottom(true);
		
		row().fill().expandX();

		setupConfigurationItems();

		addButton("Copy ", BUTTON_COPY_CLICKED);
		addButton("Delete ", BUTTON_DELETE_CLICKED);
		
		setPositiveButton("Close", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
			}
		});

		super.create();
	}
	
	private void addButton(String text, final int index) {
		TextButton button = new TextButton(text, this.skin);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(index);
			}
		});
		add(button).fill().expandX();
	}
	
	private void setupConfigurationItems() {
		gameObject.initializeConfigurationItems();

		ArrayList<ConfigurationItem> configurationItems = gameObject.getConfigurationItems();

		if( configurationItems != null ) {
			for( ConfigurationItem item : configurationItems ) {
				final ConfigurationItem.Type type = item.getType();

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
								ConfigurationItem nItem = getConfigurationItemFromSelectedObject(type);
								if( nItem != null ) {
									nItem.setValueText(buffer.toString());
								}
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
								ConfigurationItem nItem = getConfigurationItemFromSelectedObject(type);
								if( nItem != null ) {
									nItem.setValueNumeric(Float.parseFloat(buffer.toString()));
								}
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
							ConfigurationItem nItem = getConfigurationItemFromSelectedObject(type);
							if( nItem != null ) {
								nItem.setValueNumeric(slider.getValue());	
							}
						}
					});
					add(slider);
				}

				row();
			}
		}
	}
	
	private ConfigurationItem getConfigurationItemFromSelectedObject(ConfigurationItem.Type type) {
		ArrayList<ConfigurationItem> configurationItems = this.gameObject.getConfigurationItems();

		if( configurationItems != null ) {
			for( ConfigurationItem item : configurationItems ) {
				if( item.getType() == type ) {
					return item;
				}
			}
		}
		return null;
	}
}
