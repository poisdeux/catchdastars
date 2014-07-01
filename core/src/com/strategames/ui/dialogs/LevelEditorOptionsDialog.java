package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.engine.utils.LevelEditorPreferences;

public class LevelEditorOptionsDialog extends Dialog {
	public static final int CHECKBOX_SNAPTOGRID = 1;
	public static final int CHECKBOX_DISPLAYGRID = 2;
	
	private LevelEditorPreferences preferences;
	private Dialog.OnClickListener listener;
	
	public LevelEditorOptionsDialog(Stage stage, Skin skin, LevelEditorPreferences preferences, Dialog.OnClickListener listener) {
		super(stage, skin);
		this.preferences = preferences;
		this.listener = listener;
	}
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public void create() {
		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();
		
		createSnapToGridTool();
		
		setPositiveButton("Close", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
			}
		});
		pack();
		
		super.create();
	}
	
	private void createSnapToGridTool() {
		CheckBox cBox = new CheckBox("Snap to grid", skin);
		cBox.setChecked(this.preferences.snapToGridEnabled());
		cBox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if( preferences.snapToGridEnabled() ) {
					preferences.snapToGrid(false);
				} else {
					preferences.snapToGrid(true);
				}
				listener.onClick(LevelEditorOptionsDialog.this, CHECKBOX_SNAPTOGRID);
			}
		});
		add(cBox);
		
		cBox = new CheckBox("Display grid", skin);
		cBox.setChecked(this.preferences.displayGridEnabled());
		cBox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if( preferences.displayGridEnabled() ) {
					preferences.displayGrid(false);
				} else {
					preferences.displayGrid(true);
				}
				listener.onClick(LevelEditorOptionsDialog.this, CHECKBOX_DISPLAYGRID);
			}
		});
		add(cBox);
		row().fill().expandX();
	}
}
