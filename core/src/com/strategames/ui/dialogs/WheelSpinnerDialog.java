package com.strategames.ui.dialogs;


import com.badlogic.gdx.Gdx;
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
	private String[] items;
	private Skin skin;
	private String title;

	public WheelSpinnerDialog(String title, String[] items, Stage stage, Skin skin) {
		super(stage, skin);
		this.stage = stage;
		this.items = items;
		this.skin = skin;
		this.title = title;
		setVisible(false);
		setWidth(150);
		setHeight(200);
	}

	public void create() {

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
					OnClickListener listener = getOnClickListener();
					if( listener != null ) {
						getOnClickListener().onClick(WheelSpinnerDialog.this, Integer.parseInt(button.getText().toString()));
					}
				}
			});
			buttonTable.add(button).width(50);
			buttonTable.row();
		}

		ScrollPane scrollPane = new ScrollPane(buttonTable, this.skin);
		add(scrollPane).maxHeight(150);
		row();

		super.create();
	}

	public void show() {
		setVisible(true);
		this.stage.addActor(this);
	}

	public void hide() {
		setVisible(false);
		remove();
	}


}
