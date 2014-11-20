package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LevelFailedDialog extends GameStateDialog {

	public static final int BUTTON_QUIT_CLICKED = BUTTON_NEGATIVE;
	public static final int BUTTON_RETRY_CLICKED = BUTTON_NEUTRAL;
	
	public LevelFailedDialog(Stage stage, Skin skin) {
		super("Level failed", stage, skin);
		setNegativeButton("Quit");
		setPositiveButton("Retry");
	}
}
