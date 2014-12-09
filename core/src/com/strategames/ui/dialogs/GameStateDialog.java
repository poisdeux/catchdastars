package com.strategames.ui.dialogs;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.strategames.ui.helpers.FilledRectangleImage;
import com.strategames.ui.helpers.Screen;
import com.strategames.ui.interfaces.ActorListener;
import com.strategames.ui.widgets.TextButton;

/**
 * 
 * @author mbrekhof
 *
 */
abstract public class GameStateDialog extends Dialog {
	public final static int BUTTON_LEFT_CLICKED = 0;
	public final static int BUTTON_RIGHT_CLICKED = 1;
	
	public static enum States {
		PAUSED, FAILED, COMPLETE
	}
	
	private String message;
	private Label messageLabel;
	private TextButton buttonLeft;
	private TextButton buttonRight;
	
	private FilledRectangleImage filter;
	
	protected Stage stage;
	
	protected GameStateDialog(String message, Stage stage, Skin skin) {
		super(stage, skin);
		this.message = message;
		this.stage = stage;
	}
	
	public Label getMessageLabel() {
		return messageLabel;
	}
	
	public void setLeftButton(String text) {
		this.buttonLeft = new TextButton(text, getSkin());
		this.buttonLeft.setListener(new ActorListener() {
			
			@Override
			public void onTap(Actor actor) {
				notifyListener(BUTTON_LEFT_CLICKED);
			}
			
			@Override
			public void onLongPress(Actor actor) {
				
			}
			
			@Override
			public void setListener(ActorListener listener) {
				// TODO Auto-generated method stub
				
			}
		});
		this.buttonLeft.getColor().a = 0f;
		this.buttonLeft.addAction( sequence( fadeIn( 0.25f ) ) );
	}
	
	
	public void setRightButton(String text) {
		this.buttonRight = new TextButton(text, getSkin());
		this.buttonRight.setListener(new ActorListener() {
			
			@Override
			public void onTap(Actor actor) {
				notifyListener(BUTTON_RIGHT_CLICKED);
			}
			
			@Override
			public void onLongPress(Actor actor) {
				
			}
			
			@Override
			public void setListener(ActorListener listener) {
				// TODO Auto-generated method stub
				
			}
		});
		this.buttonRight.getColor().a = 0f;
		this.buttonRight.addAction( sequence( fadeIn( 0.25f ) ) );
	}
	
	@Override
	public Dialog create() {
		Vector2 start = new Vector2();
		Vector2 end = new Vector2();
		Screen.getFullScreenCoordinates(this.stage, start, end);
		
		this.filter = new FilledRectangleImage(this.stage);
		this.filter.setPosition(start.x, start.y);
		this.filter.setWidth(end.x);
		this.filter.setHeight(end.y);
		this.filter.setColor(0f, 0f, 0f, 0.4f);
		
		this.messageLabel = new Label(this.message, getSkin());
		float xMiddle = (super.stage.getWidth() / 2) - (this.messageLabel.getWidth() / 2);
		this.messageLabel.setPosition(xMiddle, super.stage.getHeight() / 2);
		this.messageLabel.addAction( fadeIn( 0.25f ) );
		this.messageLabel.getColor().a = 0f;
		addActor(this.messageLabel);
				
		setFillParent(true);
		
		return super.create();
	}
	
	@Override
	public void createButtons() {
		Table table = new Table(getSkin());
		table.setFillParent(true);
		table.bottom();

		TextButton button = getNegativeButton();
		if( button != null ) {
			table.add(button).expandX().fillX();
		}
		button = getNeutralButton();
		if( button != null ) {
			table.add(button).expandX().fillX();
		}
		button = getPositiveButton();
		if( button != null ) {
			table.add(button).expandX().fillX();
		}
		
		addActor(table);
	}

	@Override
	public void show() {
		this.stage.addActor(this.filter);
		super.show();
	}
	
	@Override
	public void hide() {
		this.filter.remove();
		super.hide();
	}
	
	@Override
	public void setStyle(Style style) {
		//Make sure background is not set by Dialog class
	}
}
