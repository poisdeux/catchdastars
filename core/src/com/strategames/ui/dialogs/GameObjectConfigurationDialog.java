package com.strategames.ui.dialogs;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.utils.ConfigurationItem;

public class GameObjectConfigurationDialog extends Dialog {
	public final static int BUTTON_CLOSE_CLICKED = BUTTON_POSITIVE;

	private GameObject gameObject;

	public GameObjectConfigurationDialog(Stage stage, final GameObject object, Skin skin) {
		super(stage, skin);
		this.gameObject = object;
		setPositiveButton("Close", new OnClickListener() {

			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
			}
		});
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
	public Dialog create() {
		setCenter(true);
		setBottom(true);

		row().fill().expandX();

		setupConfigurationItems();

		return super.create();
	}

	/**
	 * By default the positive button is used to set a Close button.
	 * Call this method to replace the close button
	 */
	@Override
	public void setPositiveButton(String text) {
		// TODO Auto-generated method stub
		super.setPositiveButton(text);
	}


	/**
	 * By default the positive button is used to set a Close button.
	 * Call this method to replace the close button
	 */
	@Override
	public void setPositiveButton(String text, OnClickListener onClickListener) {
		// TODO Auto-generated method stub
		super.setPositiveButton(text, onClickListener);
	}

	private void setupConfigurationItems() {
		Table table = new Table(getSkin());
		
		gameObject.initializeConfigurationItems();

		ArrayList<ConfigurationItem> configurationItems = gameObject.getConfigurationItems();

		if( configurationItems != null ) {
			for( ConfigurationItem item : configurationItems ) {
				final ConfigurationItem.Type type = item.getType();

				if( item.getType() == ConfigurationItem.Type.TEXT ) {
					Label label = new Label(item.getName(), getSkin());
					table.add(label);

					TextField tf = new TextField(String.valueOf(item.getValueText()), getSkin());
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
					table.add(tf).left();
					table.row();
				} else if( item.getType() == ConfigurationItem.Type.NUMERIC ) {
					Label label = new Label(item.getName(), getSkin());
					table.add(label).left();

					TextField tf = new TextField(String.valueOf(item.getValueNumeric()), getSkin());
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
					table.add(tf).left();
					table.row();
				} else if( item.getType() == ConfigurationItem.Type.NUMERIC_RANGE ) {
					Label label = new Label(item.getName(), getSkin());
					table.add(label).left();

					Slider slider = new Slider(item.getMinValue(), 
							item.getMaxValue(), item.getStepSize(), false, getSkin());
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
					table.add(slider).left();
					table.row();
				} else if( item.getType() == ConfigurationItem.Type.BOOLEAN ) {
					Label label = new Label(item.getName(), getSkin());
					table.add(label).left();
					
					final CheckBox cb = new CheckBox("", getSkin());
					cb.setChecked(item.getValueBoolean());
					cb.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							ConfigurationItem nItem = getConfigurationItemFromSelectedObject(type);
							if( nItem != null ) {
								nItem.setValueBoolean( cb.isChecked() );
							}
						}
					});

					table.add(cb).left();
					table.row();
				}
				add(table);
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
