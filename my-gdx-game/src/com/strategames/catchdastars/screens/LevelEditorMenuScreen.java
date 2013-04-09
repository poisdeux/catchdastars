package com.strategames.catchdastars.screens;



import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;

public class LevelEditorMenuScreen extends AbstractScreen {
	private Skin skin;
	private Table levelButtonsTable;
	private int lastLevelNumber;
	private String dialogMessage;
	
	public LevelEditorMenuScreen(Game game) {
		super(game);

		this.skin = getSkin();
	}

	@Override
	public void show() {
		Table table = new Table( skin );
		table.setFillParent(true);
		table.add( "Level editor" ).expand().top();
		table.row();

		this.levelButtonsTable = new Table(skin);
		
		ArrayList<String> levelNames = getGame().getLevelNames();
			
		this.lastLevelNumber = 1;
		for( String name : levelNames ) {
			TextButton button = new TextButton(this.lastLevelNumber++ + ". " +name, skin);
			levelButtonsTable.add(button).expand();
			levelButtonsTable.row();
		}
		
		ScrollPane scrollPane = new ScrollPane(levelButtonsTable, skin);
		table.add(scrollPane).expand().fill().colspan(4);;
		table.row();
		
		TextButton addLevel = new TextButton( "New level", skin );
		addLevel.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("MenuScreen", "touch done at (" +x+ ", " +y+ ")");
				//				game.getSoundManager().play( TyrianSound.CLICK );
				Gdx.input.getTextInput(new TextInputListener() {
			        @Override
			        public void input(String text) {
			        	TextButton button = new TextButton(lastLevelNumber++ + ". " +text, skin);
						levelButtonsTable.add(button).expand();
						levelButtonsTable.row();
			        }
			        
			        @Override
			        public void canceled() {
			        	
			        }
			      }, "Enter name for new level", "");
				
				
			}
		}); 

		table.add( addLevel ).size( Gdx.graphics.getWidth(), 60 ).expand().bottom();
		table.row();

		Stage stage = getStage();
		stage.addActor(table);
		Gdx.input.setInputProcessor( stage );
	}
}
