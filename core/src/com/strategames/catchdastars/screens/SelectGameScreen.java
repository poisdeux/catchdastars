package com.strategames.catchdastars.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.utils.FileWriter;
import com.strategames.engine.utils.Files;
import com.strategames.engine.utils.Game;
import com.strategames.engine.utils.GameLoader;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.ErrorDialog;
import com.strategames.ui.dialogs.TextInputDialog;
import com.strategames.ui.dialogs.TextInputDialog.OnCloseListener;
import com.strategames.ui.dialogs.TextInputDialog.OnInputReceivedListener;
import com.strategames.ui.interfaces.ActorListener;
import com.strategames.ui.widgets.TextButton;

public class SelectGameScreen extends AbstractScreen implements ActorListener {
	private Table gamesButtonsTable;
	
	public SelectGameScreen(GameEngine game) {
		super(game, "Select a game");
	}

	@Override
	protected void setupUI(final Stage stage) {
		addMenuItem("Delete all games");
		
		//Gameloader to load all games
		Array<Game> games = GameLoader.loadAllLocalGames();

		//Show list of games as vertically scrollable buttonlist
		ScrollPane scrollPane = new ScrollPane(fillLevelButtonsTable(games), getSkin());
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
			FileWriter.deleteLocalGamesDir();
		}
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
	
	private Table fillLevelButtonsTable(Array<Game> games) {
		this.gamesButtonsTable = new Table();

		if( ( games != null ) && ( games.size != 0 ) ) {
			for( final Game game : games ) {
				addGameButton(game);
			}
		}
		return this.gamesButtonsTable;
	}

	private void addGameButton(Game game) {
		if( game == null ) {
			return;
		}
		
		TextButton button = new TextButton(game.getName(), getSkin());
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

	@Override
	public void onTap(Actor actor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLongPress(Actor actor) {
		// TODO Auto-generated method stub
		
	}
}
