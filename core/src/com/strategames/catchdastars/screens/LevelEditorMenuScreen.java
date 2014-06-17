package com.strategames.catchdastars.screens;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.EditLevelDialog;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.dialogs.WheelSpinnerDialog;
import com.strategames.ui.widgets.TextButton;



public class LevelEditorMenuScreen extends AbstractScreen implements Dialog.OnClickListener, ButtonListener, OnLevelsReceivedListener {
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
						addLevel(level);
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
				getGame().showMainMenu();
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
		game.setLevel((Level) tag);
		game.showLevelEditor(); 
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

		Dialog dialog = new EditLevelDialog(getStageUIElements(), getSkin(), (Level) tag);
		dialog.create();
		dialog.setTag(button);
		dialog.setPosition(button.getX(), button.getY());
		dialog.setOnClickListener(this);
		dialog.show();

		Color selectedColor = getSkin().getColor("red");
		button.setColor(selectedColor);
	}
	
	@Override
	public void onClick(Dialog dialog, int which) {
		final Color colorWhite = getSkin().getColor("white");
		if( dialog instanceof EditLevelDialog ) {
			final Level level = ((EditLevelDialog) dialog).getLevel();
			final Button button = (Button) dialog.getTag();
			switch(which) {
			case EditLevelDialog.BUTTON_CHANGELEVELNUMBER_CLICKED:
				WheelSpinnerDialog levelNumberDialog = createChangeLevelNumberDialog(level.getLevelNumber());
				levelNumberDialog.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(Dialog dialog, int which) {
						int selectedItem = ((WheelSpinnerDialog) dialog).getSelectedItem();
						switch(which) {
						case WheelSpinnerDialog.ITEM_SELECTED:
							reorderLevels(level, selectedItem);
							break;
						}
						dialog.remove();
						button.setColor(colorWhite);
					}
				});
				levelNumberDialog.setPosition(dialog.getX(), dialog.getY());
				levelNumberDialog.show();
				dialog.remove();
				break;
			case EditLevelDialog.BUTTON_CHANGENAME_CLICKED:
				changeLevelName(level, (TextButton) button);
				dialog.remove();
				button.setColor(colorWhite);
				break;
			case EditLevelDialog.BUTTON_COPY_CLICKED:
				copyLevel(level);
				dialog.remove();
				button.setColor(colorWhite);
				break;
			case EditLevelDialog.BUTTON_DELETELEVEL_CLICKED:
				deleteLevel(level);
				dialog.remove();
				break;
			case EditLevelDialog.BUTTON_CLOSE_CLICKED:
				dialog.remove();
				button.setColor(colorWhite);
				break;
			}
		}
	}

	@Override
	public void levelsReceived(String json) {
		ArrayList<Level> levels = LevelLoader.getLevels(json);
		if( levels != null ) {
			ArrayList<Level> levelsFailedToSave = null;
			if( LevelWriter.deleteLocalLevelsDir() ) {
				levelsFailedToSave = LevelWriter.save(levels);
			} else {
				showErrorDialog("Error deleting directory", "Failed to delete directory holding the levels");
			}
			if( levelsFailedToSave != null ) {
				if( levelsFailedToSave.size() == 0 ) {
					fillLevelButtonsTable(levels);
				} else {
					for(Level level : levelsFailedToSave) {
						Gdx.app.log("LevelEditorMenuScreen", "Failed to save level: "+level);
					}
					showErrorDialog("Error saving levels", "Failed to save one or more levels");
				}
			} else {
				showErrorDialog("Error deleting levels", "Failed to delete directory that holds the levels");
			}
		} else {
			showErrorDialog("Error importing", "Failed to import levels");
		}
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

	private void copyLevel(Level level) {
		Level newLevel = level.copy();
		newLevel.setName("(copy) "+ newLevel.getName());
		newLevel.setLevelNumber(level.getLevelNumber() + 1);
		addLevel(newLevel);
		reorderLevels(newLevel, level.getLevelNumber() + 1);
	}
	
	private void addLevel(Level level) {
		this.levels.addLevel(level);
		LevelWriter.save(level);
		TextButton button = new TextButton(level.getLevelNumber() + ". " +level.getName(), skin);
		button.setTag(level);
		button.setListener(LevelEditorMenuScreen.this);
		levelButtonsTable.add(button).expand();
		levelButtonsTable.row();
	}

	private void deleteLevel(Level level) {
		LevelWriter.deleteLocal(level.getLevelNumber());
		this.levels.deleteLevel(level);
		this.levels.renumberLevels();
		fillLevelButtonsTable(this.levels.getLevels());
	}

	private WheelSpinnerDialog createChangeLevelNumberDialog(int levelNumber) {
		String[] levelNumbers = new String[this.lastLevelNumber - 1];
		int index = 0;
		for(int i = 1; i <= this.lastLevelNumber; i++) {
			if( levelNumber != i) {
				levelNumbers[index++] = String.valueOf(i);
			}
		}

		WheelSpinnerDialog spinner = new WheelSpinnerDialog(getStageUIElements(), this.skin,
				"Select a number", levelNumbers);
		spinner.create();
		return spinner;
	}

	/**
	 * TODO replace loading all levels completely by something less memory hungry. We only need level number and name.
	 */

	private void fillLevelButtonsTable(ArrayList<Level> levels) {
		this.levelButtonsTable.clear();
		this.lastLevelNumber = 0;

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

	private void showErrorDialog(String title, String message) {
		ErrorDialog dialog = new ErrorDialog(getStageUIElements(), title, getSkin());
		dialog.setMessage(message);
		dialog.setCenter(true);
		dialog.create();
		dialog.show();
	}

	private void reorderLevels(Level level, int newLevelNumber) {
		int currentLevelNumber = level.getLevelNumber();
		if( currentLevelNumber > newLevelNumber ) {
			//moving backwards so we need to move elements forwards to make room
			for( Level l : this.levels.getLevels() ) {
				int lNumber = l.getLevelNumber();
				if( ( lNumber >= newLevelNumber ) && ( lNumber < currentLevelNumber ) ) {
					l.setLevelNumber(lNumber + 1);
				}
			}
			level.setLevelNumber(newLevelNumber);
		} else if ( currentLevelNumber < newLevelNumber ) {
			//moving forward so we need to move elements backwards to make room
			for( Level l : this.levels.getLevels() ) {
				int lNumber = l.getLevelNumber();
				if( ( lNumber <= newLevelNumber ) && ( lNumber > currentLevelNumber ) ) {
					l.setLevelNumber(lNumber - 1);
				}
			}
			level.setLevelNumber(newLevelNumber);
		}
		
		this.levels.renumberLevels();
		fillLevelButtonsTable(this.levels.getLevels());
	}
}
