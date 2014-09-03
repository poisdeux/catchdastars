package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LevelFailedDialog extends GameStateDialog {

	public static final int BUTTON_QUIT_CLICKED = BUTTON_LEFT_CLICKED;
	public static final int BUTTON_RETRY_CLICKED = BUTTON_RIGHT_CLICKED;
	
	public LevelFailedDialog(Stage stage, Skin skin) {
		super("Level failed", stage, skin);
		setLeftButton("Quit");
		setRightButton("Retry");
	}
}
