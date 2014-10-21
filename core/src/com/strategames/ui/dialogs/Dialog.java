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
	
	private OnClickListener onClickListener;
	
	public static final int BUTTON_NEGATIVE = -2;
	public static final int BUTTON_NEUTRAL = -3;
	public static final int BUTTON_POSITIVE = -4;
	public static final int BUTTON_USER1 = -5;
	public static final int BUTTON_USER2 = -6;
	public static final int BUTTON_USER3 = -7;
	
	private TextButton buttonNegative;
	private TextButton buttonNeutral;
	private TextButton buttonPositive;
	
	private String message;
	
	protected final Skin skin;
	protected Stage stage;
	
	private boolean center;
	private boolean bottom;
	private boolean top;
	
	private Object tag;
	
	public Dialog(Stage stage, Skin skin) {
		setSkin(skin);
		this.skin = skin;
		this.stage = stage;
		setVisible(false);
		setCenter(false);
		setStyle(skin.get(Style.class));
		setStage(stage);
	}

	public void setOnClickListener(OnClickListener listener) {
		this.onClickListener = listener;
	}
	
	public OnClickListener getOnClickListener() {
		return onClickListener;
	}
	
	/**
	 * Use this to pass along data
	 * @param tag
	 */
	public void setTag(Object tag) {
		this.tag = tag;
	}
	
	/**
	 * Use this to get the data set using {{@link #setTag(Object)}
	 * @return Object set using {{@link #setTag(Object)}
	 */
	public Object getTag() {
		return tag;
	}
	
	public void setCenter(boolean center) {
		this.center = center;
	}
	
	public void setTop(boolean top) {
		this.top = top;
	}
	
	public void setBottom(boolean bottom) {
		this.bottom = bottom;
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

	/**
	 * Creates a TextButton and calls listener set using {@link #setOnClickListener(OnClickListener)}
	 * when clicked
	 * @param text
	 */
	public void setPositiveButton(String text) {
		this.buttonPositive = new TextButton(text, this.skin);
		this.buttonPositive.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_POSITIVE);
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
	
	/**
	 * Creates a TextButton and calls listener set using {@link #setOnClickListener(OnClickListener)}
	 * when clicked
	 * @param text
	 */
	public void setNegativeButton(String text) {
		this.buttonNegative = new TextButton(text, this.skin);
		this.buttonNegative.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_NEGATIVE);
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
	 * Creates a TextButton and calls listener set using {@link #setOnClickListener(OnClickListener)}
	 * when clicked
	 * @param text
	 */
	public void setNeutralButton(String text) {
		this.buttonNeutral = new TextButton(text, this.skin);
		this.buttonNeutral.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_NEUTRAL);
			}
		});
	}
	
	/**
	 * Use this to create and add the actual dialog to the stage.
	 * @param stage the stage this dialog should be added to as an Actor
	 */
	public Dialog create() {
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
		
		positionDialog();
		
		return this;
	}
	
	public void show() {
		this.stage.addActor(this);
		setVisible(true);
	}
	
	public void hide() {
		this.remove();
		setVisible(false);
	}
	
	/**
	 * Use this to notify caller that used {@link #setOnClickListener(OnClickListener)} 
	 * to connect a listener, which button was clicked in which dialog.
	 * @param which
	 */
	protected void notifyListener(int which) {
		OnClickListener listener = getOnClickListener();
		if( listener != null ) {
			listener.onClick(this, which);
		}
	}
	
	public void setStyle (Style style) {
		if (style == null) throw new IllegalArgumentException("style cannot be null.");
		setBackground(style.background);
		invalidateHierarchy();
	}
	
	private void positionDialog() {
		float x = getX();
		float y = getY();
		if( this.center ) {
			x = (float) ((stage.getWidth()/2.0) - (getWidth()/2.0));
			y = stage.getHeight()/2f;
		}
		if( this.bottom ) {
			y = 0;
		} else if ( this.top ) {
			y = stage.getHeight() - getHeight();
		}
		setPosition(x, y);
	}
	
	static public class Style {
		/** Optional. */
		public Drawable background;
		public BitmapFont titleFont;
		/** Optional. */
		public Color titleFontColor = new Color(1, 1, 1, 1);
		/** Optional. */
		public Drawable stageBackground;

		public Style () {
		}

		public Style (BitmapFont titleFont, Color titleFontColor, Drawable background) {
			this.background = background;
			this.titleFont = titleFont;
			this.titleFontColor.set(titleFontColor);
		}

		public Style (Style style) {
			this.background = style.background;
			this.titleFont = style.titleFont;
			this.titleFontColor = new Color(style.titleFontColor);
		}
	}
}
