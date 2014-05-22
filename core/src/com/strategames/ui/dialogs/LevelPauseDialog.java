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

public class LevelPauseDialog extends Dialog {
	private Skin skin;
	private Game game;
	
	public LevelPauseDialog(Stage stage, Game game, Skin skin) {
		super(stage, "", skin);
		this.skin = skin;
		this.game = game;
	}
	
	@Override
	public void show() {
		setVisible(true);
	}
	
	@Override
	public void create() {
		
		final Label gamePauseLabel = new Label("Game Paused", skin);
		float xMiddle = (super.stage.getWidth() / 2) - (gamePauseLabel.getWidth() / 2);
		gamePauseLabel.setPosition(xMiddle, super.stage.getHeight() / 2);
		gamePauseLabel.addAction( sequence( fadeIn( 0.25f ) ) );
		gamePauseLabel.getColor().a = 0f;
		super.stage.addActor(gamePauseLabel);
		
		final Table table = new Table();
		table.setFillParent(true);
		table.bottom();
		
		TextButton quitButton = new TextButton("Quit", skin);
		quitButton.setListener(new ButtonListener() {
			
			@Override
			public void onTap(Button button) {
				table.clear();
				table.remove();
				game.setScreen(new MainMenuScreen(game));
			}
			
			@Override
			public void onLongPress(Button button) {
				
			}
		});
		quitButton.getColor().a = 0f;
		quitButton.addAction( sequence( fadeIn( 0.25f ) ) );
		
		table.add(quitButton).expandX().fillX().left();
		
		TextButton resumeButton = new TextButton("Resume", skin);
		resumeButton.setListener(new ButtonListener() {
			
			@Override
			public void onTap(Button button) {
				gamePauseLabel.remove();
				table.clear();
				table.remove();
				game.resumeGame();
			}
			
			@Override
			public void onLongPress(Button button) {
				
			}
		});
		resumeButton.getColor().a = 0f;
		resumeButton.addAction( sequence( fadeIn( 0.25f ) ) );
		
		table.add(resumeButton).expandX().fillX().right();
		
		super.stage.addActor(table);
		
		super.create();
	}

}
