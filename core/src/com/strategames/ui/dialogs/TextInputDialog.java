package com.strategames.ui.dialogs;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.utils.Array;

public class TextInputDialog extends Dialog {
	private OnInputReceivedListener listener;
	//	private HashMap<String, Table> textfields;
	private Array<String> inputFieldNames;

	public interface OnCloseListener {
		public void onClosed(Dialog dialog);
	}

	public interface OnInputReceivedListener {
		public void onInputReceived(TextInputDialog dialog, String name, String input);
	}

	public TextInputDialog(Stage stage, Skin skin) {
		super(stage, skin);
		inputFieldNames = new Array<String>();
		setPositiveButton("Close");
	}

	public void setOnInputReceivedListener(OnInputReceivedListener listener) {
		this.listener = listener;
	}

	@Override
	public Dialog create() {
		for(String name : inputFieldNames ) {
			add(createInputField(name));
			row();
		}
		return super.create();
	}

	public void addInputField(String name) {
		this.inputFieldNames.add(name);
	}

	public void setOnCloseListener(final OnCloseListener listener) {
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(Dialog dialog, int which) {
				dialog.remove();
				listener.onClosed(dialog);
			}
		});
	}

	private Table createInputField(String name) {
		Table table = new Table(getSkin());

		TextField tf = new TextField("", getSkin());
		tf.setName(name);
		final StringBuffer buffer = new StringBuffer();
		tf.setTextFieldListener(new TextFieldListener() {

			@Override
			public void keyTyped(TextField textField, char key) {
				if ( key == '\r' || key == '\n' ) {
					textField.next(true);
					if( listener != null ) {
						listener.onInputReceived(TextInputDialog.this, textField.getName(), buffer.toString());
					}
				}
				buffer.append(key);
			}
		});
		Label label = new Label(name, getSkin());
		table.add(label).fillX();
		table.add(tf).width(200).right();
		return table;
	}
}
