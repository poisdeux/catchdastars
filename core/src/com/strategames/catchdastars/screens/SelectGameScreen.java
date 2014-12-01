package com.strategames.catchdastars.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.CatchDaStars;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.utils.FileWriter;
import com.strategames.engine.utils.Files;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.GameLoader;
import com.strategames.ui.dialogs.ConfirmationDialog;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.dialogs.TextInputDialog;
import com.strategames.ui.dialogs.TextInputDialog.OnCloseListener;
import com.strategames.ui.interfaces.ActorListener;
import com.strategames.ui.widgets.TextButton;

public class SelectGameScreen extends AbstractScreen {
	private Table gamesButtonsTable;
	
	public SelectGameScreen(GameEngine game) {
		super(game, "Select a game");
	}

	@Override
	protected void setupUI(final Stage stage) {
		addMenuItem("Delete all games");
		
		//Gameloader to load all games
		Array<Game> games = GameLoader.loadAllLocalGames();

		this.gamesButtonsTable = new Table();
		fillGamesButtonsTable(games);
		
		//Show list of games as vertically scrollable buttonlist
		ScrollPane scrollPane = new ScrollPane(this.gamesButtonsTable, getSkin());
		scrollPane.setHeight(400f);
		scrollPane.setWidth(stage.getWidth());
		scrollPane.setPosition(0, 200f);
		stage.addActor(scrollPane);

		Table table = new Table(getSkin());
		table.setWidth(stage.getWidth());
		
		TextButton button = new TextButton( "Main menu", getSkin());
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getGameEngine().showMainMenu();
			}
		});
		table.add(button).fillX().expandX();
		
		button = new TextButton( "New game", getSkin());
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				handleButtonClicked();
			}	
		});
		table.add(button).fillX().expandX();
		table.bottom();
		table.setHeight(button.getHeight());
		stage.addActor(table);
	}

	@Override
	protected void setupActors(Stage stage) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMenuItemSelected(String text) {
		if(text.contentEquals("Delete all games")) {
			ConfirmationDialog dialog = new ConfirmationDialog(getStageUIActors(), "This will delete all games", getSkin());
			dialog.setPositiveButton("Delete", new OnClickListener() {

				@Override
				public void onClick(Dialog dialog, int which) {
					dialog.remove();
					FileWriter.deleteLocalGamesDir();
					gamesButtonsTable.clear();
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
		
		hideMainMenu();
	}
	
	private void handleButtonClicked() {
		final Game game = new Game();
		TextInputDialog dialog = new TextInputDialog(getStageUIActors(), getSkin());
		dialog.addInputField("Game name: ");
		dialog.addInputField("Designer: ");
		dialog.setWidth(200);
		dialog.setHeight(60);
		dialog.setCenter(true);
		dialog.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public void onClosed(Dialog dialog,
					HashMap<String, StringBuffer> values) {
				for(String name : values.keySet()) {
					String value = values.get(name).toString();
					if( name.contentEquals("Game name: ")) {
						game.setName(value);
					} else if( name.contentEquals("Designer: ")) {
						game.setDesigner(value);
					} 
				}
				addNewGame(game);
			}
		});
		dialog.create();
		dialog.show();
	}
	
	private void fillGamesButtonsTable(Array<Game> games) {
		if( ( games != null ) && ( games.size != 0 ) ) {
			for( Game game : games ) {
				addGameButton(game);
			}
		}
	}

	private void addGameButton(final Game game) {
		if( game == null ) {
			return;
		}
		
		TextButton button = new TextButton(game.getName(), getSkin());
		button.setListener(new ActorListener() {
			
			@Override
			public void onTap(Actor actor) {
				CatchDaStars gameEngine = (CatchDaStars) getGameEngine();
				gameEngine.setGame(game);
				gameEngine.showLevelEditorMenu();
			}
			
			@Override
			public void onLongPress(Actor actor) {
				showGameConfigurationDialog(game);
			}
		});
		this.gamesButtonsTable.add(button);
		this.gamesButtonsTable.row();
	}
	
	private void addNewGame(Game game) {
		if( FileWriter.saveGameLocal(Files.getGamePath(game), game) ) {
			addGameButton(game);
		} else {
			ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Failed to save game", getSkin());
			dialog.create();
			dialog.show();
		}
	}
	
	private void showGameConfigurationDialog(Game game) {
		
	}
}
