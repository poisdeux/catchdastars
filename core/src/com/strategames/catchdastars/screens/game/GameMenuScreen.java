/**
 * 
 * Copyright 2015 Martijn Brekhof
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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.game.CatchDaStars;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.storage.GameLoader;
import com.strategames.engine.storage.GameMetaData;
import com.strategames.engine.storage.GameWriter;
import com.strategames.engine.utils.Game;
import com.strategames.ui.dialogs.ConfirmationDialog;
import com.strategames.ui.dialogs.Dialog;

/**
 * TODO save settings per game
 */
public class GameMenuScreen extends AbstractScreen {

    private TextButton startGameButton;

	public GameMenuScreen(GameEngine game) {
		super(game);
        setTitle(new Label(game.getGame().getGameMetaData().getName(), getSkin()));
	}

	@Override
	protected void setupUI(Stage stage) {
		float x = stage.getWidth() / 2f;
		float y = 600f;

        addMenuItem("Delete progress");

        createStartGameButton();

        startGameButton.setPosition(x - (startGameButton.getWidth() / 2f), y);
        stage.addActor(startGameButton);
        y -= startGameButton.getHeight() + 20f;

		TextButton button = new TextButton( "High Scores", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
			}
		} );
		button.setPosition(x - (button.getWidth() / 2f), y);
		stage.addActor(button);
		y -= button.getHeight() + 20f;

        button = new TextButton( "Settings", getSkin() );
        button.addListener( new ClickListener() {

            public void clicked(InputEvent event, float x, float y) {
                ((CatchDaStars) getGameEngine()).showSettings();
            }
        });
        button.setPosition(x - (button.getWidth() / 2f), y);
        stage.addActor(button);
        y -= button.getHeight() + 20f;
	}

    @Override
    public void show() {
        super.show();
        setStartGameButtonText();
    }

    @Override
	protected void setupActors(Stage stage) {
	}

    @Override
    protected void onMenuItemSelected(String text) {
        final GameMetaData gameMetaData = getGameEngine().getGame().getGameMetaData();
        if(text.contentEquals("Delete progress")) {
            ConfirmationDialog dialog = new ConfirmationDialog(getStageUIActors(), "This will delete all levels", getSkin());
            dialog.setPositiveButton("Delete", new Dialog.OnClickListener() {

                @Override
                public void onClick(Dialog dialog, int which) {
                    dialog.remove();
                    GameWriter.deleteInprogress(gameMetaData);
                    getGameEngine().getGame().reset();
                    setStartGameButtonText();

                }
            });
            dialog.setNegativeButton("Cancel", new Dialog.OnClickListener() {

                @Override
                public void onClick(Dialog dialog, int which) {
                    dialog.remove();
                }
            });
            dialog.create();
            dialog.show();
            hideMainMenu();
        }
    }

    private void createStartGameButton() {
        final GameEngine gameEngine = getGameEngine();

        startGameButton = new TextButton("Continue game", getSkin());

        setStartGameButtonText();

        startGameButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Game game = gameEngine.getGame();
                gameEngine.startLevel(game.getCurrentLevelPosition());
            }
        });
    }

    private void setStartGameButtonText() {
        GameMetaData gameMetaDataInprogress = GameLoader.loadInProgress(getGameEngine().getGame().getGameMetaData());
        if( gameMetaDataInprogress == null ) {
            startGameButton.setText("New game");
        } else {
            startGameButton.setText("Continue game");
        }
    }
}