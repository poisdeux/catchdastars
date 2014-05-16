package com.strategames.catchdastars.screens;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.LevelLoader;
import com.strategames.catchdastars.utils.Levels;
import com.strategames.interfaces.ButtonListener;
import com.strategames.ui.dialogs.ButtonsDialog;
import com.strategames.ui.widgets.TextButton;



public class LevelEditorMenuScreen extends AbstractScreen implements ButtonListener {
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

		this.table.add( addLevel ).fillX().expand().bottom();
		
		TextButton export = new TextButton("export", skin);
		export.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Levels levels = new Levels();
				levels.setLevels(LevelLoader.loadAllLocalLevels());
				getGame().getExporter().export(levels.getJson());
			}
		});
		
		this.table.add( export ).fillX().expand().bottom();
		
		TextButton importButton = new TextButton("import", skin);
		importButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getGame().getImporter().importLevels();
			}
		});
		
		this.table.add( importButton ).fillX().expand().bottom();
		
		TextButton mainMenu = new TextButton( "Main menu", skin);
		mainMenu.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getGame().setScreen(new MainMenuScreen(getGame()));
			}
		});
		this.table.add( mainMenu ).fillX().expand().bottom();
		
		this.table.row();
		
		stage.addActor(this.table);
	}

	@Override
	protected void setupActors(Stage stage) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onTap(Button button) {
		if( ! ( button instanceof TextButton ) ) {
			return;
		}
		
		Object tag = ((TextButton) button).getTag();
		if( ! (tag instanceof Level) ) {
			return;
		}
		Game game = getGame();
		LoadingScreen screen = new LoadingScreen(new LevelEditorScreen(game), game, ((Level) tag).getLevelNumber());
		game.setScreen(screen);
	}

	@Override
	public void onLongPress(final Button button) {
		if( ! ( button instanceof TextButton ) ) {
			return;
		}
		
		Object tag = ((TextButton) button).getTag();
		
		if( ! (tag instanceof Level) ) {
			return;
		}
		
		final Level level = (Level) tag;
		ButtonsDialog dialog = new ButtonsDialog(stageUIActors, "Choose action", skin, ButtonsDialog.ORIENTATION.HORIZONTAL);
		dialog.add("Delete level", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				deleteLevel(level);
			}
		});
		
		dialog.add("Change name", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				changeLevelName(level, (TextButton) button);
			}
		});
		
		final ButtonsDialog fDialog = dialog;
		dialog.add("Cancel", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				fDialog.remove();
			}
		});
		
		dialog.create();
		dialog.show();
	}
	
	private void changeLevelName(final Level level, final TextButton button) {
		Gdx.input.getTextInput(new TextInputListener() {
	        @Override
	        public void input(String text) {
	        	level.setName(text);
	        	button.setText(level.getLevelNumber() + ". "+level.getName());
	        	Level levelOnDisk = LevelLoader.loadLocalSync(level.getLevelNumber());
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
		LevelLoader.deleteLocal(level.getLevelNumber());
		fillLevelButtonsTable();
	}
	
	/**
	 * TODO replace loading all levels completely by something less memory hungry. We only need level number and name.
	 */
	
	private void fillLevelButtonsTable() {
		this.levelButtonsTable.clear();
		
		this.lastLevelNumber = 0;
		
		ArrayList<Level> levels = LevelLoader.loadAllLocalLevels();
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
	
	@Override
	protected boolean handleBackNavigation() {
		getGame().setScreen(new MainMenuScreen(getGame()));
		return true;
	}
}
