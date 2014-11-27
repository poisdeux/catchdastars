package com.strategames.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;

public class TextInputDialog extends Dialog {
	private OnInputReceivedListener listener;
	
	public interface OnInputReceivedListener {
		public void onInputReceived(TextInputDialog dialog, String input);
	}
	
	public TextInputDialog(Stage stage, Skin skin) {
		super(stage, skin);
	}

	public void setOnInputReceivedListener(OnInputReceivedListener listener) {
		this.listener = listener;
	}
	
	@Override
	public Dialog create() {
		TextField tf = new TextField("", getSkin());
		final StringBuffer buffer = new StringBuffer();
		tf.setTextFieldListener(new TextFieldListener() {

			@Override
			public void keyTyped(TextField textField, char key) {
				if ( key == '\r' || key == '\n' ) {
					textField.next(true);
					if( listener != null ) {
						listener.onInputReceived(TextInputDialog.this, buffer.toString());
					}
				}
				buffer.append(key);
			}
		});
		add(tf).fillX().expandX();
		return super.create();
	}
}
