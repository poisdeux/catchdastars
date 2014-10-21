package com.strategames.ui.dialogs;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strategames.ui.widgets.TextButton;

public class ButtonsDialog extends Dialog {
	public static enum ORIENTATION {HORIZONTAL, VERTICAL};
	private ORIENTATION orientation;
	private ArrayList<TextButton> textButtons;

	/**
	 * Creates a simple buttons dialog with a title
	 * @param stage
	 * @param title
	 * @param skin
	 * @param orientation if buttons should be layed out vertically or horizontally
	 */
	public ButtonsDialog(Stage stage, Skin skin, ORIENTATION orientation) {
		super(stage, skin);
		this.textButtons = new ArrayList<TextButton>();
		this.orientation = orientation;
	}

	public void add(String text, EventListener listener) {
		TextButton tButton = new TextButton(text, skin);
		tButton.addListener(listener);
		this.textButtons.add(tButton);
	}

	/**
	 * Use this to create the actual dialog.
	 * Note that this needs to be called before {@link #
	 */
	public Dialog create() {
		defaults().spaceBottom(10);
		defaults().spaceTop(10);
		
		if( this.orientation == ORIENTATION.VERTICAL ) { 
			row().fill().expandX();

			for( TextButton button : this.textButtons ) {
				add(button);
				row().fill().expandX();
			}
		} else {
			for( TextButton button : this.textButtons ) {
				add(button);
			}
		}
		pack();
		return super.create();
	}
}
