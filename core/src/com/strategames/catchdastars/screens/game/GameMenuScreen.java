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
import com.strategames.ui.dialogs.ConfirmationDialog;
import com.strategames.ui.dialogs.Dialog;

/**
 * TODO save settings per game
 */
public class GameMenuScreen extends AbstractScreen {

    private TextButton startGameButton;

	public GameMenuScreen(GameEngine game) {
		super(game);
        setTitle(new Label(getGameEngine().getGameMetaData().getName(), getSkin()));
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
        GameMetaData gameMetaData = getGameEngine().getGameMetaData();
        if(text.contentEquals("Delete progress")) {
            ConfirmationDialog dialog = new ConfirmationDialog(getStageUIActors(), "This will delete all levels", getSkin());
            dialog.setPositiveButton("Delete", new Dialog.OnClickListener() {

                @Override
                public void onClick(Dialog dialog, int which) {
                    dialog.remove();
                    GameWriter.deleteInprogress(getGameEngine().getGameMetaData());
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
        }
    }

    private void createStartGameButton() {
        final GameEngine gameEngine = getGameEngine();

        startGameButton = new TextButton("Continue game", getSkin());

        setStartGameButtonText();

        startGameButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                String buttonText = startGameButton.getText().toString();
                if(buttonText.contains("New game") ) {
                    GameMetaData g = gameEngine.getGameMetaData();
                    GameWriter.saveInProgress(gameEngine.getGameMetaData());
                    gameEngine.setGameMetaData(g);
                    gameEngine.startLevel(g.getCurrentLevelPosition());
                } else {
                    GameMetaData gameMetaDataInprogress = GameLoader.loadInProgress(gameEngine.getGameMetaData());
                    gameEngine.setGameMetaData(gameMetaDataInprogress);
                    gameEngine.startLevel(gameMetaDataInprogress.getCurrentLevelPosition());
                }
            }
        });
    }

    private void setStartGameButtonText() {
        GameMetaData gameMetaDataInprogress = GameLoader.loadInProgress(getGameEngine().getGameMetaData());
        if( gameMetaDataInprogress == null ) {
            startGameButton.setText("New game");
        } else {
            startGameButton.setText("Continue game");
        }
    }
}