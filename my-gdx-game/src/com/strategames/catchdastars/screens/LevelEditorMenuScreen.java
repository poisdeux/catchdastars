package com.strategames.catchdastars.screens;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.interfaces.OnLevelsReceivedListener;
import com.strategames.catchdastars.utils.Level;
import com.strategames.catchdastars.utils.LevelLoader;
import com.strategames.catchdastars.utils.LevelWriter;
import com.strategames.catchdastars.utils.Levels;
import com.strategames.interfaces.ButtonListener;
import com.strategames.ui.dialogs.ButtonsDialog;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.widgets.TextButton;



public class LevelEditorMenuScreen extends AbstractScreen 
implements ButtonListener, OnLevelsReceivedListener {
	private Skin skin;
	private Table levelButtonsTable;
	private int lastLevelNumber;
	private Table table;
	private Levels levels;
	
	public LevelEditorMenuScreen(Game game) {
		super(game);

		this.skin = getSkin();
		
		this.levels = new Levels(); 
		this.levels.setLevels(LevelLoader.loadAllLocalLevels());
	}

	@Override
	protected void setupUI(Stage stage) {
		this.table = new Table( skin );
		this.table.setFillParent(true);
		this.table.add( "Level editor" ).expand().top();
		this.table.row();

		this.levelButtonsTable = new Table(skin);
		
		fillLevelButtonsTable(this.levels.getLevels());

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
						LevelWriter.save(level);
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
				getGame().getExporter().export(levels.getJson());
			}
		});

		this.table.add( export ).fillX().expand().bottom();

		TextButton importButton = new TextButton("import", skin);
		importButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getGame().getImporter().importLevels(LevelEditorMenuScreen.this);
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

		dialog.add("Change level number", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				changeLevelNumber(level);
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
				LevelWriter.save(level);
			}

			@Override
			public void canceled() {

			}
		}, "Enter name", level.getName());
	}

	private void deleteLevel(Level level) {
		LevelWriter.deleteLocal(level.getLevelNumber());
		this.levels.deleteLevel(level);
		this.levels.renumberLevels();
		fillLevelButtonsTable(this.levels.getLevels());
	}

	private void changeLevelNumber(final Level level) {
		String[] levelNumbers = new String[this.lastLevelNumber + 1];
		
		SelectBox box = new SelectBox(levelNumbers, getSkin());
		box.setSelection(level.getLevelNumber());
		box.addListener(new EventListener() {
			
			@Override
			public boolean handle(Event event) {
				SelectBox box = (SelectBox) event.getListenerActor();
				level.setLevelNumber(Integer.parseInt(box.getSelection()));
				box.remove();
				return true;
			}
			
			
		});
		
		getStageUIElements().addActor(box);
	}
	
	/**
	 * TODO replace loading all levels completely by something less memory hungry. We only need level number and name.
	 */

	private void fillLevelButtonsTable(ArrayList<Level> levels) {
		if( levels.isEmpty() ) {
			return;
		}

		this.levelButtonsTable.clear();

		this.lastLevelNumber = 0;

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

	@Override
	public void levelsReceived(String json) {
		Gdx.app.log("LevelEditorMenuScreen", "levelsReceived: "+json);
		ArrayList<Level> levels = LevelLoader.getLevels(json);
		if( levels != null ) {
			ArrayList<Level> levelsFailedToSave = null;
			if( LevelWriter.deleteLocalLevelsDir() ) {
				levelsFailedToSave = LevelWriter.save(levels);
			} else {
				ErrorDialog dialog = new ErrorDialog(getStageUIElements(), "Error deleting directory", getSkin());
				dialog.setMessage("Failed to delete directory holding the levels");
				dialog.setCenter(true);
				dialog.create();
				dialog.show();
			}
			if( levelsFailedToSave != null ) {
				if( levelsFailedToSave.size() == 0 ) {
					fillLevelButtonsTable(levels);
				} else {
					for(Level level : levelsFailedToSave) {
						Gdx.app.log("LevelEditorMenuScreen", "Failed to save level: "+level);
					}
					ErrorDialog dialog = new ErrorDialog(getStageUIElements(), "Error saving levels", getSkin());
					dialog.setMessage("Failed to save one or more levels");
					dialog.setCenter(true);
					dialog.create();
					dialog.show();
				}
			} else {
				ErrorDialog dialog = new ErrorDialog(getStageUIElements(), "Error deleting levels", getSkin());
				dialog.setMessage("Failed to delete directory that holds the levels");
				dialog.setCenter(true);
				dialog.create();
				dialog.show();
			}
		} else {
			ErrorDialog dialog = new ErrorDialog(getStageUIElements(), "Error importing", getSkin());
			dialog.setMessage("Failed to import levels");
			dialog.setCenter(true);
			dialog.create();
			dialog.show();
		}
	}
}
