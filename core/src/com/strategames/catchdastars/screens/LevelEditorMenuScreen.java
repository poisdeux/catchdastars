package com.strategames.catchdastars.screens;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
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
import com.strategames.engine.utils.ScreenshotFactory;
import com.strategames.ui.dialogs.ConfirmationDialog;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
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

		addMenuItem("Import levels");
		addMenuItem("Export levels");
		addMenuItem("Delete game");

		Array<Level> localLevels = LevelLoader.loadAllLocalLevels();
		this.levels.setLevels(localLevels);

		this.levelButtonsGrid = new GridLayout();
		//Center button grid in scrollpane
		this.levelButtonsGrid.setOffset(new Vector2((getStageUIActors().getWidth() / 2f)-30f, 185f));
		this.levelButtonsGrid.setElementSize(25, 40f);

		ScrollPane scrollPane = new ScrollPane(levelButtonsGrid, skin);
		scrollPane.setHeight(400f);
		scrollPane.setWidth(stage.getWidth());
		scrollPane.setPosition(0, 200f);
		stage.addActor(scrollPane);

		TextButton button = new TextButton( "Main menu", skin);
		button.setWidth(stage.getWidth());

		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getGame().showMainMenu();
			}
		});
		stage.addActor(button);
	}

	@Override
	protected void setupActors(Stage stage) {

	}

	@Override
	public void show() {
		super.show();

		if( editingLevel != null ) { // reload level to include added gameobjects
			Array<Level> levelsArrayList = this.levels.getLevels();
			int index = levelsArrayList.indexOf(editingLevel, true);
			editingLevel = LevelLoader.loadLocalSync(editingLevel.getPositionAsString());
			levelsArrayList.set(index, editingLevel);
		}

		//Check if adjacent rooms are still accessible
		markLevelsReachable();

		fillLevelButtonsTable(this.levels.getLevels());
	}

	@Override
	public void onTap(final Button button) {
		if( button instanceof TextButton ) {
			handleTextButtonTap((TextButton) button);
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
				/**
				 * TODO Add check to see if deleting level does not make
				 * other levels unreachable
				 */
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
		this.levelButtonsGrid.layout();
	}

	private void deleteLevel(Level level) {
		LevelWriter.deleteLocal(level);
		this.levels.deleteLevel(level);
	}

	/**
	 * TODO replace loading all levels completely by something less memory hungry. We only need level position, doors, and name.
	 */
	private void fillLevelButtonsTable(Array<Level> levels) {
		this.levelButtonsGrid.clear();
		Vector2 elementSize = this.levelButtonsGrid.getElementSize();
		
		if( ( levels == null ) || ( levels.size == 0 ) ) {
			TextButton button = createLevelButton("");
			this.levelButtonsGrid.set(0, 0, button);
		} else {
			for( final Level level : levels ) {
				Image image = new Image(ScreenshotFactory.loadScreenShot(level));
				image.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						CatchDaStars game = (CatchDaStars) getGame();
						game.setLevel(level);
						editingLevel = level;
						game.showLevelEditor();
					}
				});
				
				image.setScaling(Scaling.fit);
				image.setSize(elementSize.x, elementSize.y);
				
//				TextButton button = createLevelButton(level.getName());
//				button.setTag(level);
				
				int[] position = level.getPosition();
				this.levelButtonsGrid.set(position[0], position[1], image);
				if( level.isReachable() ) {
					addNextLevelButtons(level);
				} else {
					image.setColor(1f, 0.2f, 0.2f, 1f);
				}
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

	private void markLevelsReachable() {
		Level startLevel = null;
		for(Level l : this.levels.getLevels()) {
			int[] pos = l.getPosition();
			if( ( pos[0] == 0 ) && ( pos[1] == 0 ) ) {
				startLevel = l;
			} else {
				l.setReachable(false);
			}
		}

		Array<Level> reachableLevels = new Array<Level>();
		this.levels.getLevelsReachable(startLevel, reachableLevels);
		for(Level l : reachableLevels) {
			l.setReachable(true);
		}
	}

	private void handleTextButtonTap(final TextButton button) {
		CatchDaStars game = (CatchDaStars) getGame();
		Object tag = button.getTag();
		if( tag == null ) { // Create a new level
			Level level = createNewLevel(this.levelButtonsGrid.getPosition(button));
			addLevel(level);
			game.setLevel(level);
			editingLevel = level;
			game.showLevelEditor(); 
		} else if( tag instanceof Level ) {
			game.setLevel((Level) tag);
			this.editingLevel = (Level) tag;
			game.showLevelEditor(); 
		}
	}

	private void deleteAllLevels() {
		Boolean success = true;
		Iterator<Level> itr = this.levels.getLevels().iterator();
		while(itr.hasNext()) {
			Level level = itr.next();
			if( LevelWriter.deleteLocal(level)) {
				if(ScreenshotFactory.deleteScreenshot(level)) {
					itr.remove();
				}
			} else {
				success = false;
				Gdx.app.log("LevelEditorMenuScreen", "Failed to delete "+level.getName());
			}
		}
		if( ! success ) {
			ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Failed to delete some levels", getSkin());
			dialog.create().show();
		}
		
		fillLevelButtonsTable(this.levels.getLevels());
	}

	@Override
	protected void onMenuItemSelected(String text) {
		if(text.contentEquals("Import levels")) {
			if( this.levels.getLevels().size > 0 ) {
				//ask for confirmation
				ConfirmationDialog dialog = new ConfirmationDialog(getStageUIActors(), "Importing will delete current game", getSkin());
				dialog.setPositiveButton("Import", new OnClickListener() {

					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.remove();
						getGame().getExporterImporter().export(levels.getJson());
					}
				});
				dialog.setNegativeButton("Cancel", new OnClickListener() {

					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.remove();
					}
				});
				dialog.create();
				dialog.show();
			} else {
				getGame().getExporterImporter().export(levels.getJson());
			}
		} else if(text.contentEquals("Export levels")) {
			getGame().getExporterImporter().importLevels(LevelEditorMenuScreen.this);
		} else if(text.contentEquals("Delete game")) {
			//ask for confirmation
			ConfirmationDialog dialog = new ConfirmationDialog(getStageUIActors(), "This will delete all levels", getSkin());
			dialog.setPositiveButton("Delete", new OnClickListener() {

				@Override
				public void onClick(Dialog dialog, int which) {
					dialog.remove();
					deleteAllLevels();
				}
			});
			dialog.setNegativeButton("Cancel", new OnClickListener() {

				@Override
				public void onClick(Dialog dialog, int which) {
					dialog.remove();
				}
			});
			dialog.create();
			dialog.show();

		}
		getMainMenu().hide();
	}

	private Level createNewLevel(int[] position) {
		CatchDaStars game = (CatchDaStars) getGame();
		Vector3 worldSize = game.getWorldSize();
		Level level = new Level();
		level.setWorldSize(new Vector2(worldSize.x, worldSize.y));
		level.setViewSize(new Vector2(game.getViewSize()));
		level.setReachable(true); //assume level can only be created if reachable
		level.setPosition(position[0], position[1]);
		ScreenBorder.create(level, game);
		return level;
	}
}
