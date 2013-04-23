package com.strategames.ui;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.actors.GameObject;
import com.strategames.interfaces.OnSelectListener;

public class GameObjectConfigurationDialog extends Window implements TextField.TextFieldFilter {

	public GameObjectConfigurationDialog(GameObject object, Skin skin, final OnSelectListener listener) {
		super("", skin);

		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();

		HashMap<String, Float> configurationItems = object.getConfigurationItems();

		if( configurationItems == null ) {
			Label label = new Label("No configuration options available", skin);
			add(label);
			row().fill().expandX();
		} else {
			for( String name : configurationItems.keySet() ) {
				Label label = new Label(name, skin);
				add(label);

				TextField tf = new TextField(configurationItems.get(name).toString(), skin);
				tf.setTextFieldFilter( this );
				add(tf);
				row().fill().expandX();
			}
		}
		
		TextButton cancelButton = new TextButton("Cancel", skin);
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

	@Override
	public boolean acceptChar(TextField textField, char key) {
		return Character.isDigit(key);
	}
}
