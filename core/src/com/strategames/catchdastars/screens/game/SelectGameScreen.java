/**
 * 
 * Copyright 2014 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.catchdastars.screens.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.game.CatchDaStars;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;
import com.strategames.engine.scenes.scene2d.ui.TextButton;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.storage.GameMetaData;
import com.strategames.engine.storage.GameLoader;
import com.strategames.engine.utils.Game;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.Dialog.OnClickListener;
import com.strategames.ui.dialogs.ErrorDialog;

public class SelectGameScreen extends AbstractScreen {
	private Table gamesButtonsTable;
	
	public SelectGameScreen(GameEngine game) {
		super(game);
        setTitle(new Label("Select a game", getSkin()));
	}

	@Override
	protected void setupUI(final Stage stage) {
		//Gameloader to loadSync all games
		Array<GameMetaData> games = GameLoader.loadAllOriginalGames();

		if( games.size < 1 ) {
			ErrorDialog dialog = new ErrorDialog(stage, "No games available", getSkin());
			dialog.create();
			dialog.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(Dialog dialog, int which) {
					getGameEngine().stopScreen();
					dialog.remove();
				}
			});
			dialog.show();
			return;
		}
		
		this.gamesButtonsTable = new Table();
		fillGamesButtonsTable(games);
		
		//Show list of games as vertically scrollable buttonlist
		ScrollPane scrollPane = new ScrollPane(this.gamesButtonsTable, getSkin());
		scrollPane.setHeight(400f);
		scrollPane.setWidth(stage.getWidth());
		scrollPane.setPosition(0, 200f);
		stage.addActor(scrollPane);

		TextButton button = new TextButton( "Main menu", getSkin());
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				getGameEngine().showMainMenu();
			}
		});
		button.setWidth(stage.getWidth());
		button.bottom();
		stage.addActor(button);
	}

	@Override
	protected void setupActors(Stage stage) {

	}
	
	private void fillGamesButtonsTable(Array<GameMetaData> games) {
		if( ( games != null ) && ( games.size != 0 ) ) {
			for( GameMetaData gameMetaData : games ) {
				addGameButton(gameMetaData);
			}
		}
	}

	private void addGameButton(final GameMetaData gameMetaData) {
		if( gameMetaData == null ) {
			return;
		}
		
		TextButton button = new TextButton(gameMetaData.getName(), getSkin());
		button.setListener(new ActorListener() {
			
			@Override
			public void onTap(Actor actor) {
				CatchDaStars gameEngine = (CatchDaStars) getGameEngine();
                Game game = new Game(gameMetaData);
                gameEngine.setGame(game);
                gameEngine.showGameMenuScreen();
			}
			
			@Override
			public void onLongPress(Actor actor) {
			}
		});
		this.gamesButtonsTable.add(button);
		this.gamesButtonsTable.row();
	}
}
