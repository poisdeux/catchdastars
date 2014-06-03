package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LevelFailedDialog extends LevelStateDialog {

	public LevelFailedDialog(Stage stage, Skin skin) {
		super("Level failed", States.FAILED, stage, skin);
		setLeftButton("Quit");
		setRightButton("Retry");
	}
}
