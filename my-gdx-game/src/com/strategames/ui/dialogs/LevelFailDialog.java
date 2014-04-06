package com.strategames.ui.dialogs;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.screens.MainMenuScreen;
import com.strategames.interfaces.ButtonListener;
import com.strategames.ui.widgets.TextButton;

public class LevelFailDialog extends Dialog {
	private Skin skin;
	private Game game;
	
	public LevelFailDialog(Game game, Skin skin) {
		super("", skin);
		this.skin = skin;
		this.game = game;
	}
	
	@Override
	public void show(Stage stage) {
		
		final Label gameOverLabel = new Label("Game Over", skin);
		float xMiddle = (Gdx.graphics.getWidth() / 2) - (gameOverLabel.getWidth() / 2);
		gameOverLabel.setPosition(xMiddle, Gdx.graphics.getHeight() / 2);
		gameOverLabel.addAction( sequence( fadeIn( 0.25f ) ) );
		
		gameOverLabel.getColor().a = 0f;
		
		stage.addActor(gameOverLabel);
		
		final Table table = new Table();
		table.setFillParent(true);
		table.bottom();
		
		TextButton retryButton = new TextButton("Retry", skin);
		retryButton.setListener(new ButtonListener() {
			
			@Override
			public void onTap(Button button) {
//				gameOverLabel.clear();
				gameOverLabel.remove();
				table.clear();
				table.remove();
				game.reset();
			}
			
			@Override
			public void onLongPress(Button button) {
				
			}
		});
		retryButton.getColor().a = 0f;
		retryButton.addAction( sequence( fadeIn( 0.25f ) ) );
		
		table.add(retryButton).expandX().fillX().left();
		
		TextButton mainMenuButton = new TextButton("Main menu", skin);
		mainMenuButton.setListener(new ButtonListener() {
			
			@Override
			public void onTap(Button button) {
				game.setScreen(new MainMenuScreen(game));
			}
			
			@Override
			public void onLongPress(Button button) {
				
			}
		});
		mainMenuButton.getColor().a = 0f;
		mainMenuButton.addAction( sequence( fadeIn( 0.25f ) ) );
		
		table.add(mainMenuButton).expandX().fillX().right();
		
		stage.addActor(table);
	}

}
