package com.strategames.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.strategames.ui.widgets.TextButton;

/**
 * Use {@link Dialog#create()} to build the dialog and {@link Dialog#show()}
 * and {@link Dialog#hide()} to show and hide the dialog
 * TODO make this a builder class by making all builder methods static and
 * return the dialog so we can chain the methods. See for example the AlertDialog
 * from the Android SDK.
 * @author martijn
 *
 */
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
	
	private TextButton negativeButton;
	private TextButton neutralButton;
	private TextButton positiveButton;
	
	private static int amountOfDialogsVisible;
	
	private String message;
	
	private final Skin skin;
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

	public static int getAmountOfDialogsVisible() {
		return amountOfDialogsVisible;
	}
	
	public void setOnClickListener(OnClickListener listener) {
		this.onClickListener = listener;
	}
	
	public OnClickListener getOnClickListener() {
		return onClickListener;
	}
	
	public Skin getSkin() {
		return skin;
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
	
	public TextButton getPositiveButton() {
		return positiveButton;
	}
	
	public TextButton getNeutralButton() {
		return neutralButton;
	}
	
	public TextButton getNegativeButton() {
		return negativeButton;
	}
	
	public void setPositiveButton(String text, final Dialog.OnClickListener onClickListener) {
		this.positiveButton = new TextButton(text, this.skin);
		this.positiveButton.addListener(new ClickListener() {
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
		this.positiveButton = new TextButton(text, this.skin);
		this.positiveButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_POSITIVE);
			}
		});
	}

	public void setNegativeButton(String text, final Dialog.OnClickListener onClickListener) {
		this.negativeButton = new TextButton(text, this.skin);
		this.negativeButton.addListener(new ClickListener() {
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
		this.negativeButton = new TextButton(text, this.skin);
		this.negativeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				notifyListener(BUTTON_NEGATIVE);
			}
		});
	}

	public void setNeutralButton(String text, final Dialog.OnClickListener onClickListener) {
		this.neutralButton = new TextButton(text, this.skin);
		this.neutralButton.addListener(new ClickListener() {
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
		this.neutralButton = new TextButton(text, this.skin);
		this.neutralButton.addListener(new ClickListener() {
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
		
		createButtons();
		
		row();

		pack();
		
		positionDialog();
		
		return this;
	}
	
	/**
	 * Creates the positive, neutral and negative buttons that should be displayed at the bottom of the dialog.
	 * <br/>
	 * Override to change the bottom buttons layout.
	 */
	public void createButtons() {
		Table table = new Table(skin);
		if( this.negativeButton != null ) {
			table.add(this.negativeButton).expandX();
		}
		if( this.neutralButton != null ) {
			table.add(this.neutralButton).expandX();
		}
		if( this.positiveButton != null ) {
			table.add(this.positiveButton).expandX();
		}
		add(table).right().bottom();
	}
	
	public void show() {
		this.stage.addActor(this);
		setVisible(true);
		amountOfDialogsVisible++;
	}
	
	public void hide() {
		this.remove();
		setVisible(false);
		amountOfDialogsVisible--;
		if( amountOfDialogsVisible < 0 ) {
			Gdx.app.log("Dialog", "hide(): amountOfDialogsVisible="+amountOfDialogsVisible+", dialog="+this);
		}
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
