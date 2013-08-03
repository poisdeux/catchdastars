package com.strategames.ui;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strategames.catchdastars.Game;

public class ButtonsDialog extends Dialog {
	private final Skin skin;
	private final Dialog.OnClickListener listener;
	private final Game game;
	private ArrayList<TextButton> textButtons;
	
	public ButtonsDialog(Game game, Skin skin, final Dialog.OnClickListener listener) {
		super("", skin);
		this.skin = skin;
		this.textButtons = new ArrayList<TextButton>();
		this.listener = listener;
		this.game = game;
	}

	public void add(String text, EventListener listener) {
		TextButton tButton = new TextButton(text, skin);
		tButton.addListener(listener);
		this.textButtons.add(tButton);
	}
		
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public void show(final Stage stage) {
		setPosition(0, 0);
		defaults().spaceBottom(10);
		row().fill().expandX();

		for( TextButton button : this.textButtons ) {
			add(button);
			row().fill().expandX();
		}
		
		pack();

		super.show(stage);
	}
}
