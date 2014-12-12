package com.strategames.catchdastars.screens.editor;

import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
import com.strategames.engine.scenes.scene2d.ui.GridLayout;
import com.strategames.engine.scenes.scene2d.ui.GridLayout.OnItemClickedListener;
import com.strategames.engine.scenes.scene2d.ui.ScreenshotImage;
import com.strategames.engine.scenes.scene2d.ui.Table;
import com.strategames.engine.scenes.scene2d.ui.TextButton;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.utils.FileWriter;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.GameLoader;
import com.strategames.engine.utils.Level;
import com.strategames.engine.utils.LevelLoader;
import com.strategames.engine.utils.ScreenBorder;
import com.strategames.engine.utils.ScreenshotFactory;
import com.strategames.ui.dialogs.ConfirmationDialog;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.EditLevelDialog;
import com.strategames.ui.dialogs.ErrorDialog;

public class LevelEditorMenuScreen extends AbstractScreen implements Dialog.OnClickListener, OnLevelsReceivedListener {
	private GridLayout levelButtonsGrid;
	private Level editingLevel;
	private Pixmap emptyLevelImage;
	private ScrollPane scrollPane;
	
	public LevelEditorMenuScreen(GameEngine game) {
		super(game, "Level editor");
	}

	@Override
	protected void setupUI(Stage stage) {
		Game game = getGameEngine().getGame();
		Skin skin = getSkin();

		addMenuItem("Play game");
		addMenuItem("Save game");
		addMenuItem("Delete game");
		addMenuItem("Import levels");
		addMenuItem("Export levels");

		Array<Level> localLevels = LevelLoader.loadAllLocalLevels(game);
		game.clearLevels();
		for(Level level : localLevels) {
			game.setLevel(level);
		}

		this.levelButtonsGrid = new GridLayout();
		this.levelButtonsGrid.setListener(new OnItemClickedListener() {

			@Override
			public void onTap(int x, int y, Actor actor) {
				if( actor instanceof ScreenshotImage ) {
					startLevelEditor(x, y, (ScreenshotImage) actor);
				}
			}

			@Override
			public void onLongPress(int x, int y, Actor actor) {
				if( actor instanceof ScreenshotImage ) {
					startEditLevelDialog(x, y, (ScreenshotImage) actor);
				}
			}
		});

		//Center button grid in scrollpane
//		this.levelButtonsGrid.setPosition((stage.getWidth() / 2f)-12f, 180f);
		this.levelButtonsGrid.setElementSize(25f, 40f);
		stage.addListener(this.levelButtonsGrid);
		
		this.emptyLevelImage = new Pixmap(25, 40, Format.RGBA8888);
		this.emptyLevelImage.setColor(0, 1, 0, 0.3f);
		this.emptyLevelImage.fill();
		
		Table table = new Table();
		table.setSkin(skin);
		table.add("test1");
//		table.add("test2");
//		table.add("test3");
//		table.add("test4");
//		table.add("test5");
//		table.add("test6");
//		table.add("test7");
//		table.add("test8");
//		table.add("test9");
//		table.add("test10");
//		table.add("test11");
//		table.add("test12");
//		table.add("test13");
//		table.add("test14");
//		table.add("test15");
//		table.add("test16");
		
		this.scrollPane = new ScrollPane(this.levelButtonsGrid, skin);
		this.scrollPane.setHeight(400f);
		this.scrollPane.setWidth(stage.getWidth());
		this.scrollPane.setPosition(0, 200f);
		stage.addActor(this.scrollPane);
		
		
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
		Game game = getGameEngine().getGame();

		Collection<Level> levelsArrayList = game.getLevels().values();

		if( editingLevel != null ) { // reload level to include added gameobjects
			editingLevel = LevelLoader.loadLocalSync(game, editingLevel.getPosition());
			game.setLevel(editingLevel);
		}

		//Check if adjacent rooms are still accessible
		game.markLevelsReachable();

		fillLevelButtonsTable(levelsArrayList);
		
		Gdx.app.log("LevelEditorMenuScreen", "show: this.levelButtonsGrid: pos=("+this.levelButtonsGrid.getX()+", "+this.levelButtonsGrid.getY());
	}

	@Override
	public void onClick(Dialog dialog, int which) {
		if( dialog instanceof EditLevelDialog ) {
			final Color colorWhite = getSkin().getColor("white");
			final Level level = ((EditLevelDialog) dialog).getLevel();
			final ScreenshotImage image = (ScreenshotImage) dialog.getTag();
			switch(which) {
			case EditLevelDialog.BUTTON_COPY_CLICKED:
				copyLevel(level);
				dialog.remove();
				image.setColor(colorWhite);
				break;
			case EditLevelDialog.BUTTON_DELETELEVEL_CLICKED:
				/**
				 * TODO Add check to see if deleting level does not make
				 * other levels unreachable
				 */
				int[] pos = level.getPosition();
				deleteLevel(level);
				ScreenshotImage newImage = createLevelImage(null);
				this.levelButtonsGrid.set(pos[0], pos[1], newImage);
				dialog.remove();
				break;
			case EditLevelDialog.BUTTON_CLOSE_CLICKED:
				dialog.remove();
				image.setColor(colorWhite);
				break;
			}
		}
	}

	@Override
	public void levelsReceived(String json) {
		Collection<Level> levels = null;

		try {
			levels = GameLoader.getGame(json).getLevels().values();
		} catch (Exception e) {
			showErrorDialog("Error importing", "Failed to import levels");
			return;
		}

		boolean levelsFailedToSave = false;
		if( FileWriter.deleteLocalGame(getGameEngine().getGame())) {
			for( Level level : levels ) {
				if( ! FileWriter.saveLevelLocal(getGameEngine().getGame(), level) ) {
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

	private void startLevelEditor(int x, int y, ScreenshotImage image) {
		CatchDaStars gameEngine = (CatchDaStars) getGameEngine();
		Level level = null;

		Object tag = image.getTag();
		if( tag instanceof Level ) {
			level = (Level) tag;
		} else {
			level = createNewLevel(x, y);
			image.setTag(level);
			addLevel(level);
		}

		gameEngine.getGame().setCurrentLevelPosition(level.getPosition());
		editingLevel = level;
		gameEngine.showLevelEditor();
	}

	private void startEditLevelDialog(int x, int y, ScreenshotImage image) {

		Object tag = image.getTag();
		if( ! (tag instanceof Level ) ) {
			return;
		}

		Dialog dialog = new EditLevelDialog(getStageUIActors(), getSkin(), (Level) tag);
		dialog.create();
		dialog.setTag(image);
		dialog.setPosition(image.getX(), image.getY());
		dialog.setOnClickListener(this);
		dialog.show();

		Color selectedColor = getSkin().getColor("red");
		image.setColor(selectedColor);
	}
	
	private void addLevel(Level level) {
		Game game = getGameEngine().getGame();
		game.addLevel(level);
		FileWriter.saveLevelLocal(game, level);
	}

	private void deleteLevel(Level level) {
		Game game = getGameEngine().getGame();
		FileWriter.deleteLevelLocal(game, level);
		int[] pos = level.getPosition();
		game.deleteLevel(pos[0], pos[1]);
	}

	/**
	 * TODO replace loading all levels completely by something less memory hungry. We only need level position, doors, and name.
	 * Also add caching to prevent loading the complete game from scratch each time we show this screen
	 */
	private void fillLevelButtonsTable(Collection<Level> levels) {
		this.levelButtonsGrid.clear();

		if( ( levels == null ) || ( levels.size() == 0 ) ) {
			ScreenshotImage image = createLevelImage(null);
			this.levelButtonsGrid.set(0, 0, image);
		} else {
			for( Level level : levels ) {
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
				ScreenshotImage image = createScreenshotImage(new Texture(emptyLevelImage), null, (int) elementSize.x, (int) elementSize.y);
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
		Game game = getGameEngine().getGame();
		Boolean success = true;
		Collection<Level> levels = game.getLevels().values();
		for(Level level : levels) {
			if( ! FileWriter.deleteLevelLocal(game, level) ) {
				success = false;
				Gdx.app.log("LevelEditorMenuScreen", "Failed to delete level "+level.getFilename());
			}
			if( ! ScreenshotFactory.deleteScreenshot(level) ) {
				success = false;
				Gdx.app.log("LevelEditorMenuScreen", "Failed to delete screenshot for "+level.getFilename());
			}
		}
		if( ! success ) {
			ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Failed to delete some levels", getSkin());
			dialog.create().show();
		}

		fillLevelButtonsTable(game.getLevels().values());
	}

	@Override
	protected void onMenuItemSelected(String text) {
		Game game = getGameEngine().getGame();
		if(text.contentEquals("Import levels")) {
			if( game.getLevels().size() > 0 ) {
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
		}else if(text.contentEquals("Play game")) {
			((CatchDaStars) getGameEngine()).startLevel(new int[] {0,0});
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
		hideMainMenu();
	}

	private Level createNewLevel(int x, int y) {
		CatchDaStars game = (CatchDaStars) getGameEngine();
		Vector3 worldSize = game.getWorldSize();
		Level level = new Level();
		level.setWorldSize(new Vector2(worldSize.x, worldSize.y));
		level.setViewSize(new Vector2(game.getViewSize()));
		level.setReachable(true); //assume level can only be created if reachable
		level.setPosition(x, y);
		ScreenBorder.create(level, game);


		Balloon balloon = new BalloonBlue();
		float xWorld = worldSize.x / 3f;
		float yWorld = 1f;

		balloon.setPosition(xWorld, yWorld);
		balloon.setNew(false);
		level.addGameObject(balloon);

		balloon = new BalloonRed();
		balloon.setPosition(xWorld + xWorld, yWorld);
		balloon.setNew(false);
		level.addGameObject(balloon);

		return level;
	}

	private ScreenshotImage createScreenshotImage(Texture texture, Level level, int width, int height) {
		ScreenshotImage image = new ScreenshotImage(texture);
		image.setTag(level);
		image.setScaling(Scaling.fit);
		image.setSize(width, height);
		return image;
	}
}
