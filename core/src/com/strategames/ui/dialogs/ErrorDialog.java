package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ErrorDialog extends Dialog {

	/**
	 * Creates a simple error dialog with a title
	 * @param stage
	 * @param title
	 * @param skin
	 */
	public ErrorDialog(Stage stage, String title, Skin skin) {
		super(stage, skin);
		setPositiveButton("Close", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
			}
		});
	}
}
