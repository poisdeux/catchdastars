package com.strategames.ui.dialogs;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class WheelSpinnerDialog extends Window {

	private Stage stage;
	private String[] items;
	private Skin skin;
	
	public WheelSpinnerDialog(String title, String[] items, Stage stage, Skin skin) {
		super(title, skin);
		this.stage = stage;
		this.items = items;
		this.skin = skin;
		setVisible(false);
	}
	
	public void create() {
		center();
		
		Table table = new Table(this.skin);
		
		for(int i = 0; i < items.length; i++) {
			Label label = new Label(items[i], this.skin);
			label.addListener(new ClickListener() {
				
			});
			table.add(label);
			table.row();
		}
		
		ScrollPane scrollPane = new ScrollPane(table, this.skin);
		
		add(scrollPane).fill().expand().maxHeight(200);
	}
	
	public void show() {
		setVisible(true);
		stage.addActor(this);
	}
	
	public void hide() {
		setVisible(false);
		remove();
	}
}
