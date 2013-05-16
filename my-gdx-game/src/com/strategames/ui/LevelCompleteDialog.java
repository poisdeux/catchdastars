package com.strategames.ui;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;

import sun.font.CreatedFontTracker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.ChalkLine;
import com.strategames.catchdastars.screens.MainMenuScreen;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.ui.TextButton.TextButtonListener;

public class LevelCompleteDialog {
	private Skin skin;
	private Game game;
	private ArrayList<ScoreItem> scoreItems;
	private float maxRowHeight;
	private float rowHeight;
	private int top;
	private final int padding = 10;
	private int count;
	private int delay;
	private int delayCount;
	
	public LevelCompleteDialog(Game game, Skin skin) {
		this.skin = skin;
		this.game = game;
		this.scoreItems = new ArrayList<LevelCompleteDialog.ScoreItem>();
	}

	public void add(Image image, int amount, int scorePerGameObject) {
		ScoreItem item = new ScoreItem(new ImageButton(image.getDrawable()), amount, scorePerGameObject);
		this.scoreItems.add(item);
		float height = image.getHeight();
		if( height > this.maxRowHeight ) {
			this.maxRowHeight = height;
		}
	}

	public void show(Stage stage) {

		this.rowHeight = this.maxRowHeight + this.padding;

		if( scoreItems.size() > 0 ) {
			this.top = Gdx.graphics.getHeight() - (int) ( this.rowHeight );
			showScoreItem(0, stage);
		}

		final Table table = new Table();
		table.setFillParent(true);
		table.bottom();

		TextButton mainMenuButton = new TextButton("Main menu", skin);
		mainMenuButton.setListener(new TextButtonListener() {

			@Override
			public void onTap(TextButton button) {
				game.setScreen(new MainMenuScreen(game));
			}

			@Override
			public void onLongPress(TextButton button) {

			}
		});
		mainMenuButton.getColor().a = 0f;
		mainMenuButton.addAction( sequence( fadeIn( 0.25f ) ) );
		table.add(mainMenuButton).expandX().fillX().left();

		TextButton nextLevelButton = new TextButton("Next level", skin);
		nextLevelButton.setListener(new TextButtonListener() {

			@Override
			public void onTap(TextButton button) {

			}

			@Override
			public void onLongPress(TextButton button) {

			}
		});
		nextLevelButton.getColor().a = 0f;
		nextLevelButton.addAction( sequence( fadeIn( 0.25f ) ) );

		table.add(nextLevelButton).expandX().fillX().right();

		stage.addActor(table);
	}

	private void showScoreItem(final int number, final Stage stage) {
		final ScoreItem scoreItem = this.scoreItems.get(number);

		final int increment = scoreItem.getScorePerGameObject();
		final int amount = scoreItem.getAmount() * increment * 5;
		final Sound sound = Sounds.getSoundForIncrement(increment);
		
		Table scoreItemTable = new Table();
		scoreItemTable.setHeight(this.maxRowHeight);
		scoreItemTable.add(scoreItem.getImageButton());

		final Label label = new Label("", skin);
		scoreItemTable.add(label).width(50);

		scoreItemTable.setPosition(100f, -this.rowHeight);
		final float finalYPosition = this.top - (this.rowHeight * number);
				
		this.count = 0;
		this.delayCount = 0;
		
		if( ( amount != 0 ) && ( increment != 0 ) ) {
			this.delay = (int) (1/Game.UPDATE_FREQUENCY_SECONDS) / (amount/increment);
		} else {
			this.delay = 0;
		}
		
		scoreItemTable.addAction(sequence(
				moveTo(100f, finalYPosition, 1f, Interpolation.circleOut),
				new Action() {
					@Override
					public boolean act(float delta) {
						if( count <= amount ) {
							if( delayCount < 1 ) {
								label.setText(String.valueOf(count));
								if( count > 0 ) {
									sound.play();
								}
								count += increment;
								delayCount = delay;
							}
							delayCount--;
							return false;
						} else {
							if( number < (scoreItems.size() - 1)  ) {
								showScoreItem(number + 1, stage);
							} else {
								drawLine(50f, finalYPosition - (2 * padding), stage);
							}
							return true;
						}
					}
				
				}));

		stage.addActor(scoreItemTable);
	}

	private void drawLine(float x, float y,  final Stage stage) {
		ChalkLine line = ChalkLine.create(x, y, 200);
		line.setDuration(420);
		stage.addActor(line);
		Sounds.drawChalkLine.play();
	}

	private class ScoreItem {
		private int amount;
		private int scorePerGameObject;
		private ImageButton button;

		public ScoreItem(ImageButton button, int amount, int scorePerGameObject) {
			this.amount = amount;
			this.scorePerGameObject = scorePerGameObject;
			this.button = button;
			this.button.pad(10);
		}

		public int getAmount() {
			return amount;
		}

		public int getScorePerGameObject() {
			return scorePerGameObject;
		}

		public ImageButton getImageButton() {
			return button;
		}
	}
}
