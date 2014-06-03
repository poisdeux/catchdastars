package com.strategames.ui.dialogs;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.strategames.interfaces.ButtonListener;
import com.strategames.ui.widgets.TextButton;

/**
 * 
 * @author mbrekhof
 *
 */
abstract public class LevelStateDialog extends Dialog {
	public final static int LEFT_BUTTON_CLICKED = 0;
	public final static int RIGHT_BUTTON_CLICKED = 1;
	
	public static enum States {
		PAUSED, FAILED, COMPLETE
	}
	
	private States state;
	
	private String message;
	private TextButton buttonLeft;
	private TextButton buttonRight;
	
	protected LevelStateDialog(String message, States state, Stage stage, Skin skin) {
		super(stage, skin);
		this.message = message;
		this.state = state;
	}
	
	@Override
	public void show() {
		setVisible(true);
	}
	
	public States getState() {
		return state;
	}
	
	public void setLeftButton(String text) {
		this.buttonLeft = new TextButton(text, skin);
		this.buttonLeft.setListener(new ButtonListener() {
			
			@Override
			public void onTap(Button button) {
				notifyListener(LEFT_BUTTON_CLICKED);
			}
			
			@Override
			public void onLongPress(Button button) {
				
			}
		});
		this.buttonLeft.getColor().a = 0f;
		this.buttonLeft.addAction( sequence( fadeIn( 0.25f ) ) );
	}
	
	
	public void setRightButton(String text) {
		this.buttonLeft = new TextButton(text, skin);
		this.buttonLeft.setListener(new ButtonListener() {
			
			@Override
			public void onTap(Button button) {
				notifyListener(LEFT_BUTTON_CLICKED);
			}
			
			@Override
			public void onLongPress(Button button) {
				
			}
		});
		this.buttonLeft.getColor().a = 0f;
		this.buttonLeft.addAction( sequence( fadeIn( 0.25f ) ) );
	}
	
	@Override
	public void create() {
		
		final Label gamePauseLabel = new Label(this.message, skin);
		float xMiddle = (super.stage.getWidth() / 2) - (gamePauseLabel.getWidth() / 2);
		gamePauseLabel.setPosition(xMiddle, super.stage.getHeight() / 2);
		gamePauseLabel.addAction( sequence( fadeIn( 0.25f ) ) );
		gamePauseLabel.getColor().a = 0f;
		addActor(gamePauseLabel);
		
		final Table table = new Table();
		table.setFillParent(true);
		table.bottom();
		
		table.add(this.buttonLeft).expandX().fillX().left();
		
		table.add(this.buttonRight).expandX().fillX().right();
		
		addActor(table);
		
		super.create();
	}

}
