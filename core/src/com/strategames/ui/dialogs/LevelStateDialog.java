package com.strategames.ui.dialogs;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.strategames.ui.interfaces.ButtonListener;
import com.strategames.ui.widgets.TextButton;

/**
 * 
 * @author mbrekhof
 *
 */
abstract public class LevelStateDialog extends Dialog {
	public final static int BUTTON_LEFT_CLICKED = 0;
	public final static int BUTTON_RIGHT_CLICKED = 1;
	
	public static enum States {
		PAUSED, FAILED, COMPLETE
	}
	
	private States state;
	
	private String message;
	private Label messageLabel;
	private TextButton buttonLeft;
	private TextButton buttonRight;
	
	protected LevelStateDialog(String message, States state, Stage stage, Skin skin) {
		super(stage, skin);
		this.message = message;
		this.state = state;
	}
	
	public States getState() {
		return state;
	}
	
	public Label getMessageLabel() {
		return messageLabel;
	}
	
	public void setLeftButton(String text) {
		this.buttonLeft = new TextButton(text, skin);
		this.buttonLeft.setListener(new ButtonListener() {
			
			@Override
			public void onTap(Button button) {
				notifyListener(BUTTON_LEFT_CLICKED);
			}
			
			@Override
			public void onLongPress(Button button) {
				
			}
		});
		this.buttonLeft.getColor().a = 0f;
		this.buttonLeft.addAction( sequence( fadeIn( 0.25f ) ) );
	}
	
	
	public void setRightButton(String text) {
		this.buttonRight = new TextButton(text, skin);
		this.buttonRight.setListener(new ButtonListener() {
			
			@Override
			public void onTap(Button button) {
				notifyListener(BUTTON_RIGHT_CLICKED);
			}
			
			@Override
			public void onLongPress(Button button) {
				
			}
		});
		this.buttonRight.getColor().a = 0f;
		this.buttonRight.addAction( sequence( fadeIn( 0.25f ) ) );
	}
	
	@Override
	public void create() {
		
		this.messageLabel = new Label(this.message, skin);
		float xMiddle = (super.stage.getWidth() / 2) - (this.messageLabel.getWidth() / 2);
		this.messageLabel.setPosition(xMiddle, super.stage.getHeight() / 2);
		this.messageLabel.addAction( sequence( fadeIn( 0.25f ) ) );
		this.messageLabel.getColor().a = 0f;
		addActor(this.messageLabel);
		
		final Table table = new Table();
		table.setFillParent(true);
		table.bottom();
		
		table.add(this.buttonLeft).expandX().fillX().left();
		
		table.add(this.buttonRight).expandX().fillX().right();
		
		addActor(table);
		
		setFillParent(true);
		super.create();
	}
	
	@Override
	public void setStyle(Style style) {
		//Make sure background is not set by Dialog class
	}
}
