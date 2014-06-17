package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.utils.Level;

public class EditLevelDialog extends ButtonsDialog {
	public static final int BUTTON_DELETELEVEL_CLICKED = 1;
	public static final int BUTTON_CHANGENAME_CLICKED = 2;
	public static final int BUTTON_COPY_CLICKED = 3;
	public static final int BUTTON_CHANGELEVELNUMBER_CLICKED = 4;
	public static final int BUTTON_CLOSE_CLICKED = BUTTON_POSITIVE;
	
	private Level level;
	
	public EditLevelDialog(Stage stage, Skin skin, Level level) {
		super(stage, skin, ORIENTATION.VERTICAL);
		this.level = level;
	}
	
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public void create() {
		add("Delete level", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_DELETELEVEL_CLICKED);
			}
		});

		add("Change name", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_CHANGENAME_CLICKED);
			}
		});

		add("Change level number", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_CHANGELEVELNUMBER_CLICKED);
			}
		});

		add("Copy level", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_COPY_CLICKED);
			}
		});
		
		setNegativeButton("Close", new OnClickListener() {

			@Override
			public void onClick(Dialog dialog, int which) {
				notifyListener(BUTTON_CLOSE_CLICKED);
			}
		});
		
		super.create();
	}
	
	
}
