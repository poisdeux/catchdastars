package com.strategames.catchdastars.screens;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.Level;
import com.strategames.ui.TextButton;
import com.strategames.ui.TextButton.TextButtonListener;

public class LevelEditorMenuScreen extends AbstractScreen implements TextButtonListener {
	private Skin skin;
	private Table levelButtonsTable;
	private int lastLevelNumber;
	private Table table;
	
	public LevelEditorMenuScreen(Game game) {
		super(game);

		this.skin = getSkin();
	}

	@Override
	protected void setupUI(Stage stage) {
		this.table = new Table( skin );
		this.table.setFillParent(true);
		this.table.add( "Level editor" ).expand().top();
		this.table.row();

		this.levelButtonsTable = new Table(skin);
		
		fillLevelButtonsTable();
		
		ScrollPane scrollPane = new ScrollPane(levelButtonsTable, skin);
		this.table.add(scrollPane).expand().fill().colspan(4);;
		this.table.row();
		
		TextButton addLevel = new TextButton( "New level", skin );
		addLevel.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("MenuScreen", "touch done at (" +x+ ", " +y+ ")");
				//				game.getSoundManager().play( TyrianSound.CLICK );
				Gdx.input.getTextInput(new TextInputListener() {
			        @Override
			        public void input(String text) {
			        	Level level = new Level();
			        	level.setLevelNumber(++lastLevelNumber);
			        	level.setName(text);
			        	level.save();
			        	TextButton button = new TextButton(lastLevelNumber + ". " +text, skin);
			        	button.setTag(level);
			        	button.setListener(LevelEditorMenuScreen.this);
						levelButtonsTable.add(button).expand();
						levelButtonsTable.row();
			        }
			        
			        @Override
			        public void canceled() {
			        	
			        }
			      }, "Enter name for new level", "");
				
				
			}
		}); 

		this.table.add( addLevel ).size( Gdx.graphics.getWidth(), 60 ).expand().bottom();
		this.table.row();
		
		stage.addActor(this.table);
		Gdx.input.setInputProcessor( stage );
	}

	@Override
	protected void setupActors(Stage stage) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onTap(TextButton button) {
		Object tag = button.getTag();
		if( ! (tag instanceof Level) ) {
			return;
		}
		Game game = getGame();
		game.setCurrentLevel((Level) tag);
		game.setScreen(new LevelEditorScreen(game));
	}

	@Override
	public void onLongPress(final TextButton button) {
		Object tag = button.getTag();
		if( ! (tag instanceof Level) ) {
			return;
		}
		final Level level = (Level) tag;
		Dialog dialog = new Dialog("Choose action", skin);
		TextButton deleteLevelButton = new TextButton("Delete level", this.skin);
		deleteLevelButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				deleteLevel(level);
			}
		});
		dialog.button(deleteLevelButton);
		
		TextButton changeNameButton = new TextButton("Change name", this.skin);
		changeNameButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				changeLevelName(level, button);
			}
		});
		dialog.button(changeNameButton);
		
		dialog.show(getStageUIElements());
	}
	
	private void changeLevelName(final Level level, final TextButton button) {
		Gdx.input.getTextInput(new TextInputListener() {
	        @Override
	        public void input(String text) {
	        	level.setName(text);
	        	button.setText(level.getLevelNumber() + ". "+level.getName());
	        	Level levelOnDisk = Level.loadLocal(level.getLevelNumber());
	        	if( levelOnDisk != null ) {
	        		levelOnDisk.setName(text);
	        		levelOnDisk.save();
	        	} else {
	        		level.save();
	        	}
	        }
	        
	        @Override
	        public void canceled() {
	        	
	        }
	      }, "Enter name", level.getName());
	}
	
	private void deleteLevel(Level level) {
		Level.deleteLocal(level.getLevelNumber());
		fillLevelButtonsTable();
	}
	
	private void fillLevelButtonsTable() {
		this.levelButtonsTable.clear();
		
		this.lastLevelNumber = 0;
		
		ArrayList<Level> levels = Level.loadAllLocalLevels();
		if( levels.isEmpty() ) {
			return;
		}
		
		Collections.sort(levels);
		this.lastLevelNumber = levels.get(levels.size() - 1).getLevelNumber();
		
		for( Level level : levels ) {
			TextButton button = new TextButton(level.getLevelNumber() + ". " + level.getName(), skin);
			button.setTag(level);
        	button.setListener(LevelEditorMenuScreen.this);
			levelButtonsTable.add(button).expand();
			levelButtonsTable.row();
		}
	}
}
