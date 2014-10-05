package com.strategames.catchdastars.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.engine.utils.LevelEditorPreferences;
import com.strategames.ui.dialogs.Dialog;

public class LevelEditorOptionsDialog extends Dialog {
	public static final int CHECKBOX_SNAPTOGRID = 1;
	public static final int CHECKBOX_DISPLAYGRID = 2;
	
	private Dialog.OnClickListener listener;
	
	public LevelEditorOptionsDialog(Stage stage, Skin skin, Dialog.OnClickListener listener) {
		super(stage, skin);
		
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
		cBox.setChecked(LevelEditorPreferences.snapToGridEnabled());
		cBox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if( LevelEditorPreferences.snapToGridEnabled() ) {
					LevelEditorPreferences.snapToGrid(false);
				} else {
					LevelEditorPreferences.snapToGrid(true);
				}
				listener.onClick(LevelEditorOptionsDialog.this, CHECKBOX_SNAPTOGRID);
			}
		});
		add(cBox);
		
		cBox = new CheckBox("Display grid", skin);
		cBox.setChecked(LevelEditorPreferences.displayGridEnabled());
		cBox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if( LevelEditorPreferences.displayGridEnabled() ) {
					LevelEditorPreferences.displayGrid(false);
				} else {
					LevelEditorPreferences.displayGrid(true);
				}
				listener.onClick(LevelEditorOptionsDialog.this, CHECKBOX_DISPLAYGRID);
			}
		});
		add(cBox);
		row().fill().expandX();
	}
}
