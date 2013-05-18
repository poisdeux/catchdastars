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
	
	
	private enum animStates {
		NONE, CHALKLINE_DRAW_BAR, CHALKLINE_CROSS_DRAW_VERTICAL, CHALKLINE_CROSS_DRAW_HORIZONTAL
	}
	
	private animStates animState;
	
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
		this.animState = animStates.NONE;
		this.totalScore = currentScore;
		this.chalkLines = new ArrayList<ChalkLine>();
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

		if( scoreItems.size() > 0 ) {
			this.top = Gdx.graphics.getHeight() - (int) ( this.rowHeight );
			showScoreItem(0);
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

		scoreItemTable.setPosition(100f, -this.rowHeight);
		final float finalYPosition = this.top - (this.rowHeight * number);
				
		this.count = 0;
		this.delayCount = 0;
		
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
								showScoreItem(number + 1);
							} else {
								float y = finalYPosition - (2 * padding);
								ChalkLine line = ChalkLine.create(50f, 
										y, 
										350f, 
										y, 420, LevelCompleteDialog.this);
								stage.addActor(line);
								Sounds.drawChalkLine.play();
								animState = animStates.CHALKLINE_DRAW_BAR;
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
		final Label label = new Label(String.valueOf(this.totalScore), skin);
		table.add(label).width(50);

		table.setPosition(x, -this.rowHeight);
		
		table.addAction(sequence(
				moveTo(x, y, 1f, Interpolation.circleOut),
				new Action() {
					@Override
					public boolean act(float delta) {
						calculateTotalAnimation(x, y, label);
						return true;
					}
				}));

		stage.addActor(table);
	}
	
	private void calculateTotalAnimation(float x, float y, final Label label) {
		int size = this.scoreItems.size();
		for(int i = 0; i < size; i++) {
			final ScoreItem scoreItem = this.scoreItems.get(i);
			final Actor actor = scoreItem.getActor();
			actor.addAction(sequence(moveTo(actor.getX(), y, 1f - (0.1f * i), Interpolation.circleIn),
					new Action() {

						@Override
						public boolean act(float delta) {
							final int amount = scoreItem.getAmount() * scoreItem.getScorePerGameObject();
							if( amount > 0 ) {
								Sounds.getSoundForIncrement(amount).play();
								totalScore += amount;
								label.setText(String.valueOf(totalScore));
							}
							actor.remove();
							return true;
						}
				
			}));
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
		if( this.animState == animStates.CHALKLINE_DRAW_BAR ) {
			Vector2 v = line.getEnd();
			float x = v.x + (this.padding * 2);
			float y = v.y;
			line = ChalkLine.create(x, y, x + 50f, y, 220, this);
			this.stage.addActor(line);
			Sounds.drawChalkLineShort2.play();
			animState = animStates.CHALKLINE_CROSS_DRAW_HORIZONTAL;
		} else if ( this.animState == animStates.CHALKLINE_CROSS_DRAW_HORIZONTAL ) {
			Vector2 v = line.getEnd();
			float x = v.x - 25f;
			float y = v.y + (line.getLength()/2f);
			line = ChalkLine.create(x, y, x, y - 50f, 210, this);
			this.stage.addActor(line);
			Sounds.drawChalkLineShort1.play();
			animState = animStates.CHALKLINE_CROSS_DRAW_VERTICAL;
		} else if ( this.animState == animStates.CHALKLINE_CROSS_DRAW_VERTICAL ) {
			Vector2 v = line.getEnd();
			showCashRegistry(100f, v.y - (this.padding * 2));
		}
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
