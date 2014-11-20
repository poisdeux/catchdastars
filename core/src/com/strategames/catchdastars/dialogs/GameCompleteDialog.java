package com.strategames.catchdastars.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strategames.ui.dialogs.GameStateDialog;

public class GameCompleteDialog extends GameStateDialog {

	public GameCompleteDialog(Stage stage, Skin skin) {
		super("Game Complete", stage, skin);
		setPositiveButton("Main menu");
	}
}
