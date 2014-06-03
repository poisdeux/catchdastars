package com.strategames.ui.dialogs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.strategames.ui.widgets.TextButton;

abstract public class Dialog extends Table {
	
	public interface OnClickListener {
		public void onClick(Dialog dialog, int which);
	}
	
	public static final int BUTTON_NEGATIVE = -2;
	public static final int BUTTON_NEUTRAL = -3;
	public static final int BUTTON_POSITIVE = -4;

	private TextButton buttonNegative;
	private TextButton buttonNeutral;
	private TextButton buttonPositive;
	
	private String message;
	
	protected final Skin skin;
	protected Stage stage;
	
	private boolean center;
	
	public Dialog(Stage stage, Skin skin) {
		setSkin(skin);
		this.skin = skin;
		this.stage = stage;
		setVisible(false);
		setCenter(false);
		setStyle(skin.get(DialogStyle.class));
	}

	public void setCenter(boolean center) {
		this.center = center;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
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
	
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public void create() {
		if( this.center ) {
			float x = (float) ((stage.getWidth()/2.0) - getWidth());
			setPosition(x, stage.getHeight()/2f);
		}
		
		if( this.message != null ) {
			add(message);
			row();
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
	
	public void setStyle (DialogStyle style) {
		if (style == null) throw new IllegalArgumentException("style cannot be null.");
		setBackground(style.background);
		invalidateHierarchy();
	}
	
	static public class DialogStyle {
		/** Optional. */
		public Drawable background;
		public BitmapFont titleFont;
		/** Optional. */
		public Color titleFontColor = new Color(1, 1, 1, 1);
		/** Optional. */
		public Drawable stageBackground;

		public DialogStyle () {
		}

		public DialogStyle (BitmapFont titleFont, Color titleFontColor, Drawable background) {
			this.background = background;
			this.titleFont = titleFont;
			this.titleFontColor.set(titleFontColor);
		}

		public DialogStyle (DialogStyle style) {
			this.background = style.background;
			this.titleFont = style.titleFont;
			this.titleFontColor = new Color(style.titleFontColor);
		}
	}
}
