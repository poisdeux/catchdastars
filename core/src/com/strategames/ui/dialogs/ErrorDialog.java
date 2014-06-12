package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ErrorDialog extends Dialog {

	public static final int BUTTON_CLOSE = BUTTON_POSITIVE;
	
	/**
	 * Creates a simple error dialog with a title
	 * @param stage
	 * @param title
	 * @param skin
	 */
	public ErrorDialog(Stage stage, String message, Skin skin) {
		super(stage, skin);
		setPositiveButton("Close", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
				notifyListener(BUTTON_POSITIVE);
			}
		});
		setCenter(true);
		setMessage(message);
	}
}
