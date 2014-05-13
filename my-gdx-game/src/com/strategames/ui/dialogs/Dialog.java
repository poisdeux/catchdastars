package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.ui.widgets.TextButton;

abstract public class Dialog extends Window {
	
	public interface OnClickListener {
		public void onClick(Dialog dialog, int which);
	}
	
	public static final int BUTTON_NEGATIVE = -2;
	public static final int BUTTON_NEUTRAL = -3;
	public static final int BUTTON_POSITIVE = -4;

	private TextButton buttonNegative;
	private TextButton buttonNeutral;
	private TextButton buttonPositive;
	
	protected Array<TextButton> buttons;
	protected final Skin skin;
	protected Stage stage;
	
	public Dialog(Stage stage, String title, Skin skin) {
		super(title, skin);
		this.skin = skin;
		this.buttons = new Array<TextButton>();
		this.stage = stage;
		setVisible(false);
	}

	public void setPositiveButton(String text, final Dialog.OnClickListener onClickListener) {
		this.buttonPositive = new TextButton(text, this.skin);
		this.buttonPositive.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onClickListener.onClick(Dialog.this, BUTTON_POSITIVE);
			}
		});
	}

	public void setNegativeButton(String text, final Dialog.OnClickListener onClickListener) {
		this.buttonNegative = new TextButton(text, this.skin);
		this.buttonNegative.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onClickListener.onClick(Dialog.this, BUTTON_NEGATIVE);
			}
		});
	}

	public void setNeutralButton(String text, final Dialog.OnClickListener onClickListener) {
		this.buttonNeutral = new TextButton(text, this.skin);
		this.buttonNeutral.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onClickListener.onClick(Dialog.this, BUTTON_NEUTRAL);
			}
		});
	}
	
	public int addButton(String text, final Dialog.OnClickListener onClickListener) {
		TextButton button = new TextButton(text, this.skin);
		this.buttons.add(button);
		final int index = this.buttons.indexOf(button, true);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onClickListener.onClick(Dialog.this, index);
			}
		});
		
		return index;
	}
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public void create() {
		
		for( TextButton button : this.buttons ) {
			add(button);
		}
		
		if( this.buttonNegative != null ) {
			add(this.buttonNegative).expand().bottom().right();
		}
		if( this.buttonNeutral != null ) {
			add(this.buttonNeutral).expand().bottom().right();
		}
		if( this.buttonPositive != null ) {
			add(this.buttonPositive).expand().bottom().right();
		}
		row();

		pack();
	}
	
	public void show() {
		this.stage.addActor(this);
		setVisible(true);
	}
	
	public void hide() {
		this.remove();
		setVisible(false);
	}
}
