package com.strategames.ui;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.screens.MainMenuScreen;
import com.strategames.ui.TextButton.TextButtonListener;

public class LevelPauseDialog extends Dialog {
	private Skin skin;
	private Game game;
	
	public LevelPauseDialog(Game game, Skin skin) {
		super("", skin);
		this.skin = skin;
		this.game = game;
	}
	
	@Override
	public void show(Stage stage) {
		
		final Label gamePauseLabel = new Label("Game Paused", skin);
		float xMiddle = (Gdx.graphics.getWidth() / 2) - (gamePauseLabel.getWidth() / 2);
		gamePauseLabel.setPosition(xMiddle, Gdx.graphics.getHeight() / 2);
		gamePauseLabel.addAction( sequence( fadeIn( 0.25f ) ) );
		
		gamePauseLabel.getColor().a = 0f;
		
		stage.addActor(gamePauseLabel);
		
		final Table table = new Table();
		table.setFillParent(true);
		table.bottom();
		
		TextButton quitButton = new TextButton("Quit", skin);
		quitButton.setListener(new TextButtonListener() {
			
			@Override
			public void onTap(TextButton button) {
				table.clear();
				table.remove();
				game.setScreen(new MainMenuScreen(null, game));
			}
			
			@Override
			public void onLongPress(TextButton button) {
				
			}
		});
		quitButton.getColor().a = 0f;
		quitButton.addAction( sequence( fadeIn( 0.25f ) ) );
		
		table.add(quitButton).expandX().fillX().left();
		
		TextButton resumeButton = new TextButton("Resume", skin);
		resumeButton.setListener(new TextButtonListener() {
			
			@Override
			public void onTap(TextButton button) {
				gamePauseLabel.remove();
				table.clear();
				table.remove();
				game.resumeGame();
			}
			
			@Override
			public void onLongPress(TextButton button) {
				
			}
		});
		resumeButton.getColor().a = 0f;
		resumeButton.addAction( sequence( fadeIn( 0.25f ) ) );
		
		table.add(resumeButton).expandX().fillX().right();
		
		stage.addActor(table);
	}

}
