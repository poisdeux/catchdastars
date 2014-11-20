package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LevelPausedDialog extends GameStateDialog {

	public static final int BUTTON_QUIT_CLICKED = BUTTON_NEGATIVE;
	public static final int BUTTON_RESUME_CLICKED = BUTTON_POSITIVE;
	public static final int BUTTON_RETRY_CLICKED = BUTTON_NEUTRAL;
	
	public LevelPausedDialog(Stage stage, Skin skin) {
		super("Level paused", stage, skin);
		setNegativeButton("Quit");
		setNeutralButton("Retry");
		setPositiveButton("Resume");
	}
}
