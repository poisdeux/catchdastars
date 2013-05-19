package com.strategames.ui;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.ChalkLine;
import com.strategames.catchdastars.actors.ChalkLine.ChalkLineAnimationListener;
import com.strategames.catchdastars.screens.MainMenuScreen;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;
import com.strategames.ui.TextButton.TextButtonListener;

public class LevelCompleteDialog implements ChalkLineAnimationListener {
	private Skin skin;
	private Game game;
	private ArrayList<ScoreItem> scoreItems;
	private ArrayList<ChalkLine> chalkLines;
	private float maxRowHeight;
	private float rowHeight;
	private int top;
	private final int padding = 10;
	private int count;
	private int delay = 10;
	private int delayCount;
	private Stage stage;
	private int totalScore;
	private Label totalScoreLabel;
	
	private int animationPhase;
	private Vector2 animPosition;

	/**
	 * Shows a scoreboard animation
	 * @param game
	 * @param skin
	 * @param currentScore the total score minus the score of the completed level
	 */
	public LevelCompleteDialog(Game game, Skin skin, int currentScore) {
		this.skin = skin;
		this.game = game;
		this.scoreItems = new ArrayList<LevelCompleteDialog.ScoreItem>();
		this.animPosition = new Vector2();
		this.totalScore = currentScore;
		this.chalkLines = new ArrayList<ChalkLine>();
		this.totalScoreLabel = new Label(String.valueOf(this.totalScore), skin);
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
		this.stage = stage;
		this.rowHeight = this.maxRowHeight + this.padding;
		this.animationPhase = -1;
		setupUI();
		animationController();
	}

	private void setupUI() {
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

	private void animationController() {
		this.animationPhase++;
		switch(this.animationPhase) {
		case 0:
			if( scoreItems.size() > 0 ) {
				this.top = Gdx.graphics.getHeight() - (int) ( this.rowHeight );
				showScoreItem(0);
			}
			break;
		case 1:
			this.animPosition.y -= 2 * padding;
			this.animPosition.x = 50f;
			ChalkLine line = ChalkLine.create(this.animPosition.x, 
					this.animPosition.y, 
					350f, 
					this.animPosition.y, 420, LevelCompleteDialog.this);
			stage.addActor(line);
			Sounds.drawChalkLine.play();
			break;
		case 2:
			this.animPosition.x = 350f + (this.padding * 2);
			line = ChalkLine.create(this.animPosition.x, this.animPosition.y, 
					this.animPosition.x + 50f, this.animPosition.y, 220, this);
			this.stage.addActor(line);
			Sounds.drawChalkLineShort2.play();
			break;
		case 3:
			this.animPosition.x += 25f;
			line = ChalkLine.create(this.animPosition.x, this.animPosition.y + 25f, 
					this.animPosition.x, this.animPosition.y - 25f, 210, this);
			this.stage.addActor(line);
			Sounds.drawChalkLineShort1.play();
			break;
		case 4:
			this.animPosition.x = 100f;
			this.animPosition.y -= this.rowHeight;
			showCashRegistry(this.animPosition.x, this.animPosition.y);
			break;
		case 5:
			calculateTotalAnimation(0, this.animPosition.x, this.animPosition.y);
			break;
		}
	}

	private void showScoreItem(final int number) {
		final ScoreItem scoreItem = this.scoreItems.get(number);

		final int increment = scoreItem.getScorePerGameObject();
		final int amount = scoreItem.getAmount() * increment;
		final Sound sound = Sounds.getSoundForIncrement(increment);

		Table scoreItemTable = new Table();
		scoreItemTable.setHeight(this.maxRowHeight);
		scoreItemTable.add(scoreItem.getImageButton());

		final Label label = new Label("", skin);
		scoreItemTable.add(label).width(50);

		this.animPosition.x = 100f;
		this.animPosition.y = this.top - (this.rowHeight * number);

		scoreItemTable.setPosition(animPosition.x, -this.rowHeight);

		this.count = 0;
		this.delayCount = 0;

		scoreItemTable.addAction(sequence(
				moveTo(animPosition.x, animPosition.y, 1f, Interpolation.circleOut),
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
								showScoreItem(number + 1);
							} else {
								animationController();
							}
							return true;
						}
					}

				}));

		scoreItem.setActor(scoreItemTable);
		stage.addActor(scoreItemTable);
	}

	private void showCashRegistry(final float x, final float y) {
		Table table = new Table();
		table.setHeight(this.maxRowHeight);
		table.add(new ImageButton(new Image(Textures.bricksVertical).getDrawable()));
		table.add(totalScoreLabel).width(50);

		table.setPosition(x, -this.rowHeight);

		table.addAction(sequence(
				moveTo(x, y, 1f, Interpolation.circleOut),
				new Action() {
					@Override
					public boolean act(float delta) {
						animationController();
						return true;
					}
				}));

		stage.addActor(table);
	}

	private void calculateTotalAnimation(final int number, final float x, final float y) {
		ScoreItem scoreItem = this.scoreItems.get(number);
		final int amount = scoreItem.getAmount() * scoreItem.getAmount() * scoreItem.getScorePerGameObject();
		final Actor actor = scoreItem.getActor();
		actor.addAction(sequence(moveTo(actor.getX(), y, 1f - (0.1f * number), Interpolation.circleIn),
				new Action() {

			@Override
			public boolean act(float delta) {
				if( amount > 0 ) {
					Sounds.getSoundForIncrement(amount).play();
					totalScore += amount;
					totalScoreLabel.setText(String.valueOf(totalScore));
				}
				actor.remove();
				if( number >= scoreItems.size() ) {
					animationController();
				}
				return true;
			}

		}));
		
		if( number < (this.scoreItems.size() - 1) ) {
			calculateTotalAnimation(number + 1, x, y);
		}
	}

	private void showTotalScore() {
		int size = this.chalkLines.size();
		for(int i = 0; i < size; i++) {
			this.chalkLines.get(i).addAction(fadeOut(1f));
		}

	}

	@Override
	public void onLineDrawEnd(ChalkLine line) {
		this.chalkLines.add(line);
		animationController();
	}


	private class ScoreItem {
		private int amount;
		private int scorePerGameObject;
		private ImageButton button;
		private Actor actor;

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

		public Actor getActor() {
			return actor;
		}

		public void setActor(Actor actor) {
			this.actor = actor;
		}
	}
}
