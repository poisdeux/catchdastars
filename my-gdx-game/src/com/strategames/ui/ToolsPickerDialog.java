package com.strategames.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.ScreenBorder;
import com.strategames.interfaces.DialogInterface.OnClickListener;

public class ToolsPickerDialog extends Dialog {
	private final Skin skin;
	private final Game game;
	
	public ToolsPickerDialog(Game game, Skin skin) {
		super("Select a tool", skin);
		this.skin = skin;
		this.game = game;
	}
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public void show(Stage stage) {
		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();
		
		createScreenBorderTool();
		
		setPositiveButton("Close", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
			}
		});
		pack();
		
		super.show(stage);
	}
	
	private void createScreenBorderTool() {
		TextButton tButton = new TextButton("Create screen border", skin);
		tButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ScreenBorder.create(game);
				ToolsPickerDialog.this.remove();
			}
		});
		add(tButton);
		row().fill().expandX();
	}
}
