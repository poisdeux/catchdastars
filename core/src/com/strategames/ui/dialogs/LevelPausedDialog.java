package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LevelPausedDialog extends LevelStateDialog {

	public LevelPausedDialog(Stage stage, Skin skin) {
		super("Level paused", States.PAUSED, stage, skin);
		setLeftButton("Quit");
		setRightButton("Resume");
	}
}
