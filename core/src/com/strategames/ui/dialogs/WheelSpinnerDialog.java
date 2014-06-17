package com.strategames.ui.dialogs;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * A table that holds a title, a spinner, and a cancel button
 * @author mbrekhof
 *
 */
public class WheelSpinnerDialog extends Dialog {
	public static final int BUTTON_CANCELED_CLICKED = BUTTON_NEGATIVE;
	public static final int ITEM_SELECTED = BUTTON_USER1;
	
	private String[] items;
	private Skin skin;
	private String title;
	private int selectedItem;
	
	public WheelSpinnerDialog(Stage stage, Skin skin, String title, String[] items) {
		super(stage, skin);
		this.stage = stage;
		this.items = items;
		this.skin = skin;
		this.title = title;
	}

	public int getSelectedItem() {
		return selectedItem;
	}
	
	public void create() {
		setWidth(150);
		setHeight(200);
		
		if( this.title != null ) {
			Label label = new Label(this.title, this.skin);
			add(label).top();
			row();
		}

		Table buttonTable = new Table(this.skin);
		for(int i = 0; i < items.length; i++) {
			final TextButton button = new TextButton(items[i], this.skin);
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					selectedItem = Integer.parseInt(button.getText().toString());
					OnClickListener listener = getOnClickListener();
					if( listener != null ) {
						listener.onClick(WheelSpinnerDialog.this, ITEM_SELECTED);
					}
				}
			});
			buttonTable.add(button).width(50);
			buttonTable.row();
		}

		ScrollPane scrollPane = new ScrollPane(buttonTable, this.skin);
		add(scrollPane).maxHeight(150);
		row();

		setNegativeButton("Cancel");
		
		super.create();
	}
}
