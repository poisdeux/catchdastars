package com.strategames.catchdastars.screens;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.CatchDaStars;
import com.strategames.catchdastars.gameobjects.BalloonBlue;
import com.strategames.catchdastars.gameobjects.BalloonRed;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.types.Balloon;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.interfaces.OnLevelsReceivedListener;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.utils.FileWriter;
import com.strategames.engine.utils.Files;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.GridLayout;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelLoader;
import com.strategames.engine.utils.ScreenBorder;
import com.strategames.engine.utils.ScreenshotFactory;
import com.strategames.ui.dialogs.ConfirmationDialog;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.EditLevelDialog;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.interfaces.ActorListener;
import com.strategames.ui.widgets.ScreenshotImage;
import com.strategames.ui.widgets.TextButton;



public class LevelEditorMenuScreen extends AbstractScreen implements Dialog.OnClickListener, ActorListener, OnLevelsReceivedListener {
	private GridLayout levelButtonsGrid;
	private Game game;
	private Level editingLevel;
	private Pixmap emptyLevelImage;

	public LevelEditorMenuScreen(GameEngine game) {
		super(game, "Level editor");
		this.game = new Game();
	}

	@Override
	protected void setupUI(Stage stage) {
		Skin skin = getSkin();

		addMenuItem("Import levels");
		addMenuItem("Export levels");
		addMenuItem("Save game");
		addMenuItem("Delete game");

		Array<Level> localLevels = LevelLoader.loadAllLocalLevels(this.game);
		this.game.setLevels(localLevels);

		this.levelButtonsGrid = new GridLayout();
		//Center button grid in scrollpane
		this.levelButtonsGrid.setOffset(new Vector2((getStageUIActors().getWidth() / 2f)-30f, 185f));
		this.levelButtonsGrid.setElementSize(25f, 40f);

		emptyLevelImage = new Pixmap(25, 40, Format.RGBA8888);
		emptyLevelImage.setColor(0, 1, 0, 0.3f);
		emptyLevelImage.fill();

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
				getGameEngine().showMainMenu();
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

		Array<Level> levelsArrayList = this.game.getLevels();

		if( editingLevel != null ) { // reload level to include added gameobjects
			int index = levelsArrayList.indexOf(editingLevel, true);
			editingLevel = LevelLoader.loadLocalSync(editingLevel.getPositionAsString());
			levelsArrayList.set(index, editingLevel);
		}

		//Check if adjacent rooms are still accessible
		this.game.markLevelsReachable();

		fillLevelButtonsTable(levelsArrayList);
	}

	@Override
	public void onTap(final Actor actor) {
		if( actor instanceof ScreenshotImage ) {
			ScreenshotImage image = (ScreenshotImage) actor;

			Object tag = image.getTag();
			if( tag == null ) {
				return;
			}

			CatchDaStars gameEngine = (CatchDaStars) getGameEngine();

			if( tag instanceof Level ) {
				Level level = (Level) tag;
				gameEngine.getGame().setCurrentLevelPosition(level.getPosition());
				editingLevel = level;
				gameEngine.showLevelEditor();
			}
		}
	}

	@Override
	public void onLongPress(final Actor actor) {
		if( actor instanceof TextButton ) {
			TextButton button = (TextButton) actor;
			Object tag = button.getTag();
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
	}

	@Override
	public void onClick(Dialog dialog, int which) {
		if( dialog instanceof EditLevelDialog ) {
			final Color colorWhite = getSkin().getColor("white");
			final Level level = ((EditLevelDialog) dialog).getLevel();
			final Button button = (Button) dialog.getTag();
			switch(which) {
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
			boolean levelsFailedToSave = false;
			if( FileWriter.deleteLocalGamesDir() ) {
				for( Level level : levels ) {
					if( ! FileWriter.saveLocal(
							Files.getGamePath(getGameEngine().getGame()), level) ) {
						levelsFailedToSave = true;
					}
				}
			} else {
				showErrorDialog("Error deleting directory", "Failed to delete directory holding the levels");
			}
			if( levelsFailedToSave ) {
				showErrorDialog("Error saving levels", "Failed to save one or more levels");
			} else {
				fillLevelButtonsTable(levels);
			}
		} else {
			showErrorDialog("Error importing", "Failed to import levels");
		}
	}

	private void copyLevel(Level level) {
		Level newLevel = level.copy();
		/**
		 * TODO
		 * User should select other level
		 * If level is already filled ask user for confirmation
		 * otherwise simply create a copy at new position
		 */

	}

	private void addLevel(Level level) {
		this.game.addLevel(level);
		FileWriter.saveLocal(Files.getGamePath(this.game), level);
	}

	private void deleteLevel(Level level) {
		FileWriter.deleteLocal(level);
		this.game.deleteLevel(level);
	}

	/**
	 * TODO replace loading all levels completely by something less memory hungry. We only need level position, doors, and name.
	 * Also add caching to prevent loading the complete game from scratch each time we show this screen
	 */
	private void fillLevelButtonsTable(Array<Level> levels) {
		this.levelButtonsGrid.clear();

		if( ( levels == null ) || ( levels.size == 0 ) ) {
			int[] pos = new int[] {0,0};
			Level level = createNewLevel(pos);
			addLevel(level);

			ScreenshotImage image = createLevelImage(level);

			this.levelButtonsGrid.set(pos[0], pos[1], image);
		} else {
			for( final Level level : levels ) {
				int[] position = level.getPosition();
				this.levelButtonsGrid.set(position[0], position[1], createLevelImage(level));
			}

			for( Level level : levels ) {
				int[] position = level.getPosition();
				ScreenshotImage image = (ScreenshotImage) this.levelButtonsGrid.get(position[0], position[1]);
				if( level.isReachable() ) {
					addNextLevelButtons(level);
				} else {
					image.setColor(1f, 0.2f, 0.2f, 1f);
				}
			}
		}
	}

	private ScreenshotImage createLevelImage(Level level) {
		Vector2 elementSize = this.levelButtonsGrid.getElementSize();
		Texture texture = ScreenshotFactory.loadScreenShot(level);
		if( texture == null ) {
			texture = new Texture(emptyLevelImage);
		}
		return createScreenshotImage(texture, level, (int) elementSize.x, (int) elementSize.y);
	}

	private void addNextLevelButtons(Level level) {
		Vector2 elementSize = this.levelButtonsGrid.getElementSize();
		Array<Door> doors = level.getDoors();
		for(Door door : doors ) {
			final int[] nextLevelPosition = door.getNextLevelPosition();
			if( this.levelButtonsGrid.get(nextLevelPosition[0], nextLevelPosition[1]) == null ) {
				Level nextLevel = createNewLevel(nextLevelPosition);
				addLevel(nextLevel);
				ScreenshotImage image = createScreenshotImage(new Texture(emptyLevelImage), level, (int) elementSize.x, (int) elementSize.y);
				image.setTag(nextLevel);
				this.levelButtonsGrid.set(nextLevelPosition[0], nextLevelPosition[1], image);	
			}
		}
	}

	private void showErrorDialog(String title, String message) {
		ErrorDialog dialog = new ErrorDialog(getStageUIActors(), title, getSkin());
		dialog.setMessage(message);
		dialog.setCenter(true);
		dialog.create();
		dialog.show();
	}

	private void deleteAllLevels() {
		Boolean success = true;
		Iterator<Level> itr = this.game.getLevels().iterator();
		while(itr.hasNext()) {
			Level level = itr.next();
			if( FileWriter.deleteLocal(level)) {
				if(ScreenshotFactory.deleteScreenshot(level)) {
					itr.remove();
				}
			} else {
				success = false;
				Gdx.app.log("LevelEditorMenuScreen", "Failed to delete "+level.getFilename());
			}
		}
		if( ! success ) {
			ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Failed to delete some levels", getSkin());
			dialog.create().show();
		}

		fillLevelButtonsTable(this.game.getLevels());
	}

	@Override
	protected void onMenuItemSelected(String text) {
		if(text.contentEquals("Import levels")) {
			if( this.game.getLevels().size > 0 ) {
				//ask for confirmation
				ConfirmationDialog dialog = new ConfirmationDialog(getStageUIActors(), "Importing will delete current game", getSkin());
				dialog.setPositiveButton("Import", new OnClickListener() {

					@Override
					public void onClick(Dialog dialog, int which) {
						dialog.remove();
						getGameEngine().getExporterImporter().importLevels(LevelEditorMenuScreen.this);
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
				getGameEngine().getExporterImporter().export(game.getJson());
			}
		} else if(text.contentEquals("Export levels")) {
			getGameEngine().getExporterImporter().export(getGameEngine().getGame().getJson());
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

		} else if(text.contentEquals("Save game")) {

		}
		getMainMenu().hide();
	}

	private Level createNewLevel(int[] position) {
		CatchDaStars game = (CatchDaStars) getGameEngine();
		Vector3 worldSize = game.getWorldSize();
		Level level = new Level();
		level.setWorldSize(new Vector2(worldSize.x, worldSize.y));
		level.setViewSize(new Vector2(game.getViewSize()));
		level.setReachable(true); //assume level can only be created if reachable
		level.setPosition(position[0], position[1]);
		ScreenBorder.create(level, game);


		Balloon balloon = new BalloonBlue();
		float x = worldSize.x / 3f;
		float y = 1f;

		balloon.setPosition(x, y);
		balloon.setNew(false);
		level.addGameObject(balloon);

		balloon = new BalloonRed();
		balloon.setPosition(x + x, y);
		balloon.setNew(false);
		level.addGameObject(balloon);

		return level;
	}

	private ScreenshotImage createScreenshotImage(Texture texture, Level level, int width, int height) {
		ScreenshotImage image = new ScreenshotImage(texture);
		image.setListener(this);
		image.setTag(level);
		image.setScaling(Scaling.fit);
		image.setSize(width, height);
		return image;
	}
}
