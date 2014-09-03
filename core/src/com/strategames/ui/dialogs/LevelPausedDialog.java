package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LevelPausedDialog extends GameStateDialog {

	public static final int BUTTON_QUIT_CLICKED = BUTTON_LEFT_CLICKED;
	public static final int BUTTON_RESUME_CLICKED = BUTTON_RIGHT_CLICKED;
	
	public LevelPausedDialog(Stage stage, Skin skin) {
		super("Level paused", stage, skin);
		setLeftButton("Quit");
		setRightButton("Resume");
	}
}
