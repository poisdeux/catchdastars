package com.strategames.catchdastars.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.CatchDaStars;
import com.strategames.engine.game.Game;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.interfaces.OnLevelsReceivedListener;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.utils.GridLayout;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelLoader;
import com.strategames.engine.utils.LevelWriter;
import com.strategames.engine.utils.Levels;
import com.strategames.engine.utils.ScreenBorder;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.EditLevelDialog;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.interfaces.ButtonListener;
import com.strategames.ui.widgets.TextButton;



public class LevelEditorMenuScreen extends AbstractScreen implements Dialog.OnClickListener, ButtonListener, OnLevelsReceivedListener {
	private GridLayout levelButtonsGrid;
	private Levels levels;
	private Level editingLevel;

	public LevelEditorMenuScreen(Game game) {
		super(game, "Level editor");
		this.levels = new Levels();
	}

	@Override
	protected void setupUI(Stage stage) {
		Skin skin = getSkin();

		Array<Level> localLevels = LevelLoader.loadAllLocalLevels();
		this.levels.setLevels(localLevels);
		
		this.levelButtonsGrid = new GridLayout();

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
	public void show() {
		super.show();

		Gdx.app.log("LevelEditorMenuScreen", "show(): editingLevel="+editingLevel);
		if( editingLevel != null ) { // reload level to include added gameobjects
			Array<Level> levelsArrayList = this.levels.getLevels();
			int index = levelsArrayList.indexOf(editingLevel, true);
			editingLevel = LevelLoader.loadLocalSync(editingLevel.getPositionAsString());
			levelsArrayList.set(index, editingLevel);

			//Check if adjacent rooms are still accessible
			adjacentRoomsAccessible(editingLevel);
		}

		fillLevelButtonsTable(this.levels.getLevels());
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
					Game game = getGame();
					Vector3 worldSize = game.getWorldSize();
					Level level = new Level();
					level.setName(text);
					level.setWorldSize(new Vector2(worldSize.x, worldSize.y));
					level.setViewSize(new Vector2(game.getViewSize()));
					int[] position = levelButtonsGrid.getPosition(button);
					level.setPosition(position[0], position[1]);
					ScreenBorder.create(level, game);
					addLevel(level);
				}

				@Override
				public void canceled() {

				}
			}, "Enter name for new level", "");
		} else if( tag instanceof Level ) {
			CatchDaStars game = (CatchDaStars) getGame();
			game.setLevel((Level) tag);
			this.editingLevel = (Level) tag;
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
		Array<Level> levels = LevelLoader.getLevels(json);
		if( levels != null ) {
			Array<Level> levelsFailedToSave = null;
			if( LevelWriter.deleteLocalLevelsDir() ) {
				levelsFailedToSave = LevelWriter.save(levels);
			} else {
				showErrorDialog("Error deleting directory", "Failed to delete directory holding the levels");
			}
			if( levelsFailedToSave != null ) {
				if( levelsFailedToSave.size == 0 ) {
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
				button.setText(level.getName());
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
		/**
		 * TODO
		 * User should select other level
		 * If level is already filled ask user for confirmation
		 * otherwise simply create a copy at new position
		 */

	}

	private void addLevel(Level level) {
		this.levels.addLevel(level);
		LevelWriter.save(level);
		int[] levelPosition = level.getPosition();
		TextButton button = createLevelButton(level.getName());
		button.setTag(level);
		this.levelButtonsGrid.set(levelPosition[0], levelPosition[1], button);
	}

	private void deleteLevel(Level level) {
		LevelWriter.deleteLocal(level.getName());
		this.levels.deleteLevel(level);
		fillLevelButtonsTable(this.levels.getLevels());
	}

	/**
	 * TODO replace loading all levels completely by something less memory hungry. We only need level position and name.
	 */
	private void fillLevelButtonsTable(Array<Level> levels) {
		this.levelButtonsGrid.setElementSize(60f, 30f);
		if( ( levels == null ) || ( levels.size == 0 ) ) {
			TextButton button = createLevelButton("");
			this.levelButtonsGrid.set(0, 0, button);
		} else {
			for( Level level : levels ) {
				TextButton button = createLevelButton(level.getName());
				button.setTag(level);
				int[] position = level.getPosition();
				this.levelButtonsGrid.set(position[0], position[1], button);
				addNextLevelButtons(level);
			}
		}
	}

	private void addNextLevelButtons(Level level) {
		Array<Door> doors = level.getDoors();
		for(Door door : doors ) {
			int[] nextLevelPosition = door.getNextLevelPosition();
			if( this.levelButtonsGrid.get(nextLevelPosition[0], nextLevelPosition[1]) == null ) {
				TextButton button = createLevelButton("");
				this.levelButtonsGrid.set(nextLevelPosition[0], nextLevelPosition[1], button);
			}
		}
	}

	private TextButton createLevelButton(String name) {
		TextButton button = new TextButton(name, getSkin());
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

	private void adjacentRoomsAccessible(Level level) {
		int[] position = level.getPosition();
		Gdx.app.log("LevelEditorMenuScreen", "adjacentRoomsAccessible: position=("+position[0]+","+position[1]+")");

		Array<int[]> unreachableLevelsFromCurrentLevel = new Array<int[]>();
		unreachableLevelsFromCurrentLevel.add(new int[]{position[0]+1, position[1]});
		unreachableLevelsFromCurrentLevel.add(new int[]{position[0]-1, position[1]});
		unreachableLevelsFromCurrentLevel.add(new int[]{position[0], position[1]+1});
		unreachableLevelsFromCurrentLevel.add(new int[]{position[0], position[1]-1});

		for(Door door : level.getDoors()) {
			Gdx.app.log("LevelEditorMenuScreen", "adjacentRoomsAccessible:position=("+position[0]+","+position[1]+")"+", checking door: "+door);
			int[] nextLevelPosition = door.getNextLevelPosition();
			for(int i = 0; i < unreachableLevelsFromCurrentLevel.size; i++) {
				int[] pos = unreachableLevelsFromCurrentLevel.get(i);
				if( ( pos[0] == nextLevelPosition[0] ) && ( pos[1] == nextLevelPosition[1] ) ) {
					unreachableLevelsFromCurrentLevel.removeIndex(i);
					break;
				}
			}
		}

		//Check if unreachable room from current room is accessible through
		//a different door
		for(int i = 0; i < unreachableLevelsFromCurrentLevel.size; i++) {
			int[] pos = unreachableLevelsFromCurrentLevel.get(i);
			Gdx.app.log("LevelEditorMenuScreen", "adjacentRoomsAccessible: pos=("+pos[0]+","+pos[1]+")");

			Actor actor = this.levelButtonsGrid.get(pos[0], pos[1]);
			if( ( actor != null ) && ( actor instanceof TextButton ) ) {
				Level adjacentLevel = (Level) ((TextButton) actor).getTag();
				if( adjacentLevel != null ) {
					Array<Door> doorsToThisLevel = getDoorsAccessingLevel(adjacentLevel);
					if( doorsToThisLevel.size == 0 ) {
						//Mark button to indicate level unreachable
						((TextButton) actor).setColor(1f, 0f, 0f, 1f);
					} else {
						//Mark button to indicate level reachable
						((TextButton) actor).setColor(0f, 1f, 0f, 1f);
					}
				} else {
					//Remove inaccessible empty level
					this.levelButtonsGrid.remove(pos[0], pos[1]).remove();

				}
			}
		}
	}

	private Array<Door> getDoorsAccessingLevel(Level level) {
		Gdx.app.log("LevelEditorMenuScreen", "getDoorsAccessingLevel: level="+level);
		Array<Door> doorsToThisLevel = new Array<Door>();

		int[] position = level.getPosition();
		Array<int[]> nextLevels = new Array<int[]>();
		nextLevels.add(new int[]{position[0]+1, position[1]});
		nextLevels.add(new int[]{position[0], position[1]+1});
		nextLevels.add(new int[]{position[0]-1, position[1]});
		nextLevels.add(new int[]{position[0], position[1]-1});

		for(int i = 0; i < nextLevels.size; i++) {
			int[] pos = nextLevels.get(i);
			Actor actor = this.levelButtonsGrid.get(pos[0], pos[1]);
			if( ( actor != null ) && ( actor instanceof TextButton ) ) {
				Level adjacentLevel = (Level) ((TextButton) actor).getTag();
				if( adjacentLevel != null ) {
					Array<Door> doors = adjacentLevel.getDoors();
					Gdx.app.log("LevelEditorMenuScreen", "getDoorsAccessingLevel: checking adjacentLevel="+adjacentLevel);
					if( doors != null ) {
						for(Door door : doors) {
							Gdx.app.log("LevelEditorMenuScreen", "getDoorsAccessingLevel: checking door="+door);
							int[] nextPos = door.getNextLevelPosition();
							if( ( nextPos[0] == pos[0] ) && ( nextPos[1] == pos[1] ) ) {
								doorsToThisLevel.add(door);
								Gdx.app.log("LevelEditorMenuScreen", "getDoorsAccessingLevel: DoorsToThisLevel.add: "+door);
							}
						}
					}
				}
			}
		}

		return doorsToThisLevel;
	}
}
