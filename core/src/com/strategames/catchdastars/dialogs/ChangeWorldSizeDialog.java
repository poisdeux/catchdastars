package com.strategames.catchdastars.dialogs;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.engine.utils.Level;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.ErrorDialog;

public class ChangeWorldSizeDialog extends Dialog {

	public static final int VALUE_CHANGED = BUTTON_USER1;
	public static final int BUTTON_CLOSE = BUTTON_POSITIVE;
	
	private Label horizontalLabel;
	private Label verticalLabel;
	private Drawable drawableMinus;
	private Drawable drawablePlus;
	
	public ChangeWorldSizeDialog(Stage stage, Skin skin, Level level) {
		super(stage, skin);
		
		try{
			drawableMinus = new TextureRegionDrawable(skin.get("tree-minus", TextureRegion.class));
			drawablePlus = new TextureRegionDrawable(skin.get("tree-plus", TextureRegion.class));
		} catch (Exception e) {
			ErrorDialog dialog = new ErrorDialog(stage, "Error, could not load required image from skin", skin);
			dialog.setOnClickListener(new OnClickListener() {
				public void onClick(Dialog dialog, int which) {
					Gdx.app.exit();
				}
			});
			dialog.create();
			dialog.show();
			return;
		}

		setPositiveButton("Close");

		Vector2 worldSize = level.getWorldSize();
		Vector2 viewSize = level.getViewSize();

		int horizontal = (int) (worldSize.x / viewSize.x);
		int vertical = (int) (worldSize.y / viewSize.y);

		Table table = new Table(skin);
		
		this.horizontalLabel = new Label(""+horizontal, skin);
		table.add(this.horizontalLabel).pad(10);
		table.add(createIncrementDecrementTable(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int newValue = Integer.parseInt(horizontalLabel.getText().toString()) + 1;
				horizontalLabel.setText("" + newValue);
				notifyListener(VALUE_CHANGED);
			}
		}, new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int newValue = Integer.parseInt(horizontalLabel.getText().toString()) - 1;
				if( newValue < 1 ) {
					newValue = 1;
				}
				horizontalLabel.setText("" + newValue);
				notifyListener(VALUE_CHANGED);
			}
		}));

		this.verticalLabel = new Label(""+vertical, skin);
		table.add(this.verticalLabel).pad(10);
		table.add(createIncrementDecrementTable(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int newValue = Integer.parseInt(verticalLabel.getText().toString()) + 1;
				verticalLabel.setText("" + newValue);
				notifyListener(VALUE_CHANGED);
			}
		}, new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int newValue = Integer.parseInt(verticalLabel.getText().toString()) - 1;
				if( newValue < 1 ) {
					newValue = 1;
				}
				verticalLabel.setText("" + newValue);
				notifyListener(VALUE_CHANGED);
			}
		}));
		
		add(table);
		row();
	}

	public int getHorizontalAmount() {
		return Integer.parseInt(horizontalLabel.getText().toString());
	}
	
	public int getVertialAmount() {
		return Integer.parseInt(verticalLabel.getText().toString());
	}

	private Table createIncrementDecrementTable(ChangeListener incrementListener, ChangeListener decrementListener) {
		Table table = new Table(getSkin());
		ImageButton button = new ImageButton(drawablePlus);
		button.addListener( incrementListener );
		
		table.add(button);
		table.row();
		button = new ImageButton(drawableMinus);
		button.addListener( decrementListener );
		table.add(button);

		return table;
	}

}
