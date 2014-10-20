package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ConfirmationDialog extends Dialog {

	public static final int BUTTON_CLOSE = BUTTON_POSITIVE;
	
	/**
	 * Creates a simple confirmation dialog with a title and a Yes and No button
	 * <br/>
	 * Use {@link #setPositiveButton(String)} and {@link #setNegativeButton(String)} to 
	 * change the button text
	 * @param stage
	 * @param title
	 * @param skin
	 */
	public ConfirmationDialog(Stage stage, String message, Skin skin) {
		super(stage, skin);
		setPositiveButton("Yes", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
				notifyListener(BUTTON_POSITIVE);
			}
		});
		setNegativeButton("No", new OnClickListener() {
			
			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
				notifyListener(BUTTON_NEGATIVE);
			}
		});
		setCenter(true);
		setMessage(message);
	}
}
