package com.strategames.catchdastars.screens;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.CatchDaStars;
import com.strategames.engine.game.Game;
import com.strategames.engine.interfaces.OnLevelsReceivedListener;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.utils.GridLayout;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelLoader;
import com.strategames.engine.utils.LevelWriter;
import com.strategames.engine.utils.Levels;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.EditLevelDialog;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.interfaces.ButtonListener;
import com.strategames.ui.widgets.TextButton;



public class LevelEditorMenuScreen extends AbstractScreen implements Dialog.OnClickListener, ButtonListener, OnLevelsReceivedListener {
	private GridLayout levelButtonsGrid;
	private Levels levels;

	public LevelEditorMenuScreen(Game game) {
		super(game, "Level editor");
	}

	@Override
	protected void setupUI(Stage stage) {

		this.levels = new Levels();
		this.levels.setLevels(LevelLoader.loadAllLocalLevels());

		Skin skin = getSkin();

		this.levelButtonsGrid = new GridLayout();

		fillLevelButtonsTable(this.levels.getLevels());

		ScrollPane scrollPane = new ScrollPane(levelButtonsGrid, skin);
		scrollPane.setHeight(400f);
		scrollPane.setWidth(stage.getWidth());
		scrollPane.setPosition(0, 200f);
		stage.addActor(scrollPane);

		Table bottomButtonsTable = new Table();
		bottomButtonsTable.setWidth(stage.getWidth());

		TextButton button = new TextButton("export", skin);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getGame().getExporterImporter().export(levels.getJson());
			}
		});
		bottomButtonsTable.add( button ).fillX().expand();

		button = new TextButton("import", skin);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getGame().getExporterImporter().importLevels(LevelEditorMenuScreen.this);
			}
		});
		bottomButtonsTable.add( button ).fillX().expand();

		button = new TextButton( "Main menu", skin);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getGame().showMainMenu();
			}
		});
		bottomButtonsTable.add( button ).fillX().expand();
		bottomButtonsTable.setHeight(button.getHeight());
		stage.addActor(bottomButtonsTable);
	}

	@Override
	protected void setupActors(Stage stage) {
	}

	@Override
	public void onTap(final Button button) {
		if( ! ( button instanceof TextButton ) ) {
			return;
		}

		Object tag = ((TextButton) button).getTag();
		if( tag == null ) {
			//Add a new level
			Gdx.input.getTextInput(new TextInputListener() {
				@Override
				public void input(String text) {
					Level level = new Level();
					level.setName(text);
					level.setWorldSize(new Vector2(getGame().getWorldSize().x, getGame().getWorldSize().y));
					level.setViewSize(new Vector2(getGame().getViewSize()));
					int[] position = levelButtonsGrid.getPosition(button);
					level.setPosition(position[0], position[1]);
					addLevel(level);
				}

				@Override
				public void canceled() {

				}
			}, "Enter name for new level", "");
		} else if( tag instanceof Level ) {
			CatchDaStars game = (CatchDaStars) getGame();
			game.setLevel((Level) tag);
			game.showLevelEditor(); 
		}
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

		Dialog dialog = new EditLevelDialog(getStageUIActors(), getSkin(), (Level) tag);
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
		if( dialog instanceof EditLevelDialog ) {
			final Color colorWhite = getSkin().getColor("white");
			final Level level = ((EditLevelDialog) dialog).getLevel();
			final Button button = (Button) dialog.getTag();
			switch(which) {
			//			case EditLevelDialog.BUTTON_CHANGELEVELNUMBER_CLICKED:
			//				WheelSpinnerDialog levelNumberDialog = createChangeLevelNumberDialog(level.getLevelNumber());
			//				levelNumberDialog.setOnClickListener(new OnClickListener() {
			//					
			//					@Override
			//					public void onClick(Dialog dialog, int which) {
			//						int selectedItem = ((WheelSpinnerDialog) dialog).getSelectedItem();
			//						switch(which) {
			//						case WheelSpinnerDialog.ITEM_SELECTED:
			//							reorderLevels(level, selectedItem);
			//							break;
			//						}
			//						dialog.remove();
			//						button.setColor(colorWhite);
			//					}
			//				});
			//				levelNumberDialog.setPosition(dialog.getX(), dialog.getY());
			//				levelNumberDialog.show();
			//				dialog.remove();
			//				break;
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
		int[] levelPosition = level.getPosition();
		TextButton button = new TextButton(level.getName(), getSkin());
		button.setTag(level);
		button.setListener(LevelEditorMenuScreen.this);
		this.levelButtonsGrid.set(levelPosition[0], levelPosition[1], button);

		int[] gridSize = this.levelButtonsGrid.getSize();
		if( levelPosition[0] == (gridSize[0] - 1) ) {
			this.levelButtonsGrid.addColumn();
		}
		if( levelPosition[1] == (gridSize[1] - 1) ) {
			this.levelButtonsGrid.addRow();
		}
	}

	private void deleteLevel(Level level) {
		LevelWriter.deleteLocal(level.getName());
		this.levels.deleteLevel(level);
		this.levels.renumberLevels();
		fillLevelButtonsTable(this.levels.getLevels());
	}

	//	private WheelSpinnerDialog createChangeLevelNumberDialog(int levelNumber) {
	//		String[] levelNumbers = new String[this.lastLevelNumber - 1];
	//		int index = 0;
	//		for(int i = 1; i <= this.lastLevelNumber; i++) {
	//			if( levelNumber != i) {
	//				levelNumbers[index++] = String.valueOf(i);
	//			}
	//		}
	//
	//		WheelSpinnerDialog spinner = new WheelSpinnerDialog(getStageUIActors(), getSkin(),
	//				"Select a number", levelNumbers);
	//		spinner.create();
	//		return spinner;
	//	}

	/**
	 * TODO replace loading all levels completely by something less memory hungry. We only need level position and name.
	 */
	private void fillLevelButtonsTable(ArrayList<Level> levels) {
		this.levelButtonsGrid.setElementSize(60f, 30f);
		
		if( levels.isEmpty() ) {
			TextButton button = createLevelButton("");
			this.levelButtonsGrid.set(0, 0, button);
		} else {
			Collections.sort(levels);

			for( Level level : levels ) {
				TextButton button = createLevelButton(level.getName());
				button.setTag(level);
				int[] position = level.getPosition();
				this.levelButtonsGrid.set(position[0], position[1], button);
			}
			
			//Add new level row and column with new level button
			int[] size = this.levelButtonsGrid.getSize();

			for( int i = 0; i <= size[0]; i++ ) {
				TextButton button = createLevelButton("");
				this.levelButtonsGrid.set(i, size[1], button);
			}

			for( int i = 0; i < size[1]; i++ ) {
				TextButton button = createLevelButton("");
				this.levelButtonsGrid.set(size[0], i, button);
			}
		}
	}

	private TextButton createLevelButton(String name) {
		TextButton button = new TextButton("", getSkin());
		button.setListener(this);
		button.setWidth(60f);
		button.setHeight(30f);
		return button;
	}
	
	private void showErrorDialog(String title, String message) {
		ErrorDialog dialog = new ErrorDialog(getStageUIActors(), title, getSkin());
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
