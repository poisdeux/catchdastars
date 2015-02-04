package com.strategames.catchdastars.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.strategames.catchdastars.game.CatchDaStars;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.scenes.scene2d.Stage;
import com.strategames.engine.screens.AbstractScreen;
import com.strategames.engine.storage.LevelWriter;
import com.strategames.engine.utils.Game;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.ErrorDialog;

public class GameMenuScreen extends AbstractScreen {

	public GameMenuScreen(GameEngine game) {
		super(game);
        setTitle(new Label(getGameEngine().getGame().getName(), getSkin()));
	}

	@Override
	protected void setupUI(Stage stage) {
		float x = stage.getWidth() / 2f;
		float y = 600f;
        final Game game = getGameEngine().getGame();
        int[] pos = game.getCurrentLevelPosition();
        TextButton button;

        //Check if in progress game exists
        if( ( pos[0] != 0 ) || ( pos[1] != 0 ) ) {
            button = new TextButton("Continue game", getSkin());
            button.addListener(new ClickListener() {

                public void clicked(InputEvent event, float x, float y) {

                }
            });
            button.setPosition(x - (button.getWidth() / 2f), y);
            stage.addActor(button);
            y -= button.getHeight() + 20f;

        }

		button = new TextButton( "New game", getSkin() );
		button.addListener( new ClickListener() {

			public void clicked(InputEvent event, float x, float y) {
                if( ! LevelWriter.deleteAllCompletedLevels(game) ) {
                    ErrorDialog dialog = new ErrorDialog(getStageUIActors(), "Error deleting saved game", getSkin());
                    dialog.create();
                    dialog.show();
                } else {
                    getGameEngine().startLevel(new int[]{0,0});
                }
			}
		});
		button.setPosition(x - (button.getWidth() / 2f), y);
		stage.addActor(button);
		y -= button.getHeight() + 20f;


		
		button = new TextButton( "High Scores", getSkin() );
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
	protected void setupActors(Stage stage) {
	}
}