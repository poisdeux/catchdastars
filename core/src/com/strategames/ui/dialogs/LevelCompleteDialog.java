package com.strategames.ui.dialogs;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.actors.ChalkLine;
import com.strategames.catchdastars.actors.ChalkLine.ChalkLineAnimationListener;
import com.strategames.catchdastars.utils.Sounds;
import com.strategames.catchdastars.utils.Textures;

public class LevelCompleteDialog extends LevelStateDialog implements ChalkLineAnimationListener {
	
	private ArrayList<ScoreItem> scoreItems;
	private ArrayList<ChalkLine> chalkLines;
	private float maxRowHeight;
	private float maxImageHeight;
	private float top;
	private final int padding = 30;
	private int count;
	private int delay = 10;
	private int delayCount;
	private int totalScore;
	private Label totalScoreLabel;
	private Table cashRegister;
	
	private int animationPhase;
	private Vector2 animPosition;

	private float IMAGEHEIGHT = Game.convertWorldToScreen(0.60f);
	
	/**
	 * Shows a scoreboard animation
	 * @param stage the stage to use to show the score
	 * @param game
	 * @param skin
	 * @param currentScore the total score minus the score of the completed level
	 */
	public LevelCompleteDialog(Stage stage, Game game, Skin skin, int currentScore) {
		super("Level complete", States.COMPLETE, stage, skin);
		this.scoreItems = new ArrayList<LevelCompleteDialog.ScoreItem>();
		this.animPosition = new Vector2();
		this.totalScore = currentScore;
		this.chalkLines = new ArrayList<ChalkLine>();
	}

	@Override
	public void show() {
		setVisible(true);
	}
	
	public void add(Image image, int amount, int scorePerGameObject) {
		ScoreItem item = new ScoreItem(image.getDrawable(), amount, scorePerGameObject);
		this.scoreItems.add(item);
		float height = image.getHeight();
		if( height > this.maxRowHeight ) {
			this.maxRowHeight = height;
		}
	}

	public void create() {
		this.animationPhase = -1;
		
		int amountOfItems = this.scoreItems.size();
		float availableScreenHeight = this.stage.getHeight() - (this.padding * (amountOfItems + 2));
		float height = availableScreenHeight / (amountOfItems + 2);
		
		this.maxImageHeight = height < IMAGEHEIGHT ? height : IMAGEHEIGHT;
		
		animationController();
		
		super.create();
	}

	private void animationController() {
		this.animationPhase++;
		switch(this.animationPhase) {
		case 0:
			if( scoreItems.size() > 0 ) {
				this.top = this.stage.getHeight() - (this.maxImageHeight * 2);
				showScoreItem(0);
			}
			break;
		case 1:
			this.animPosition.y -= 2 * padding;
			this.animPosition.x = 50f;
			ChalkLine line = new ChalkLine(this.animPosition.x, 
					this.animPosition.y, 
					350f, 
					this.animPosition.y, 420, LevelCompleteDialog.this);
			stage.addActor(line);
			Sounds.drawChalkLine.play();
			this.chalkLines.add(line);
			break;
		case 2:
			this.animPosition.x = 350f + (this.padding * 2);
			line = new ChalkLine(this.animPosition.x, this.animPosition.y, 
					this.animPosition.x + 50f, this.animPosition.y, 220, this);
			this.stage.addActor(line);
			Sounds.drawChalkLineShort2.play();
			this.chalkLines.add(line);
			break;
		case 3:
			this.animPosition.x += 25f;
			line = new ChalkLine(this.animPosition.x, this.animPosition.y + 25f, 
					this.animPosition.x, this.animPosition.y - 25f, 210, this);
			this.stage.addActor(line);
			Sounds.drawChalkLineShort1.play();
			this.chalkLines.add(line);
			break;
		case 4:
			this.animPosition.x = 100f;
			this.animPosition.y -= (2 * padding ) ;
			showCashRegistry(this.animPosition.x, this.animPosition.y);
			break;
		case 5:
			calculateTotalAnimation(0, this.animPosition.x, this.animPosition.y);
			break;
		case 6:
			showTotalScore();
			break;
		}
	}

	private void showScoreItem(final int number) {
		final ScoreItem scoreItem = this.scoreItems.get(number);

		final int increment = scoreItem.getScorePerGameObject();
		final int amount = scoreItem.getAmount() * increment;
		final Sound sound = Sounds.getSoundForIncrement(increment);

		Drawable drawable = this.scoreItems.get(number).getDrawable();
		Image image = new Image(drawable);
		image.setScaling(Scaling.stretch);
		double scaleFactor = this.maxImageHeight / (double) image.getHeight() ;
		float height = this.maxImageHeight;
		float width = (float) (image.getWidth() * scaleFactor);
		
		Table scoreItemTable = new Table();
		scoreItemTable.setHeight(height);
		scoreItemTable.add(image).padRight(20f).width(width);

		final Label label = new Label("", skin);
		scoreItemTable.add(label).width(50f);

		this.animPosition.x = 100f;
		this.animPosition.y = this.top - (( this.maxImageHeight + this.padding )  * number);

		scoreItemTable.setPosition(animPosition.x, -height);

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
		this.cashRegister = new Table();
		final float scale = 1.2f;
		Image image = new Image(Textures.cashRegister);
		image.setScaling(Scaling.stretch);
		double scaleFactor = this.maxImageHeight / (double) image.getHeight() ;
		float height = this.maxImageHeight;
		float width = (float) (image.getWidth() * scaleFactor);
		float xPosition = x + width;
		this.cashRegister.add(image).padRight(20f).width(width);
		this.cashRegister.setTransform(true);
		this.cashRegister.setHeight(height);
		this.cashRegister.setPosition(xPosition, -height);
		this.cashRegister.scaleBy(scale);
		this.totalScoreLabel = new Label(String.valueOf(this.totalScore), skin);
		this.totalScoreLabel.scaleBy(scale);
		this.cashRegister.add(totalScoreLabel).width(50);
		
		float finalYPosition = y - height - this.padding;
		this.cashRegister.addAction(sequence(
				moveTo(xPosition, finalYPosition, 1f, Interpolation.circleOut),
				new Action() {
					@Override
					public boolean act(float delta) {
						animationController();
						return true;
					}
				}));

		stage.addActor(this.cashRegister);
		
		Sounds.cashRegisterOpen.play();
	}

	private void calculateTotalAnimation(final int number, final float x, final float y) {
		int size = this.scoreItems.size();
		final ScoreItem scoreItem = this.scoreItems.get(number);
		final Actor actor = scoreItem.getActor();

		Action actionCountScore = new Action() {

			@Override
			public boolean act(float delta) {
				int amount = scoreItem.getAmount() * scoreItem.getScorePerGameObject();
				Gdx.app.log("LevelCompleteDialog", "calculateTotalAnimation: number="+number+
						", amount="+amount);
				if( amount > 0 ) {
					Sounds.getSoundForIncrement(amount).play();
					totalScore += amount;
					totalScoreLabel.setText(String.valueOf(totalScore));
				}
				return true;
			}
		};

		Action actionRemoveActor = new Action() {

			@Override
			public boolean act(float delta) {
				actor.remove();
				return true;
			}
		};
		
		if( number == 0 ) {
			actor.addAction(sequence(moveTo(actor.getX(), y, 1f - (0.1f * number), Interpolation.circleIn), 
					actionCountScore, new Action() {
						
						@Override
						public boolean act(float delta) {
							animationController();
							return true;
						}

					}, actionRemoveActor));
		} else {
			actor.addAction(sequence(moveTo(actor.getX(), y, 1f - (0.1f * number), Interpolation.circleIn), 
					actionCountScore, actionRemoveActor));
		}
		
		if( number < (size - 1)) {
			calculateTotalAnimation(number + 1, x, y);
		}
	}

	private void showTotalScore() {
		float x = (this.stage.getWidth() / 2f) - (this.cashRegister.getWidth() / 2f);
		float y = (this.stage.getHeight() / 2f) - (this.cashRegister.getHeight() / 2f);
		
		int size = this.chalkLines.size();
		for(int i = 0; i < size; i++) {
			ChalkLine line = this.chalkLines.get(i);
			line.addAction(fadeOut(1f, Interpolation.circleOut));
		}
		
		this.cashRegister.addAction(sequence(
				parallel(com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleBy(0.5f, 0.5f, 1f, Interpolation.circle),
				moveTo(x, y, 1f, Interpolation.circle)),
				new Action() {

					@Override
					public boolean act(float delta) {
						cashRegister.remove();
						stage.addActor(cashRegister);
						return true;
					}
			
		}));
	}

	@Override
	public void onLineDrawEnd(ChalkLine line) {
		animationController();
	}


	private class ScoreItem {
		private int amount;
		private int scorePerGameObject;
		private Actor actor;
		private Drawable drawable;
		
		public ScoreItem(Drawable drawable, int amount, int scorePerGameObject) {
			this.amount = amount;
			this.scorePerGameObject = scorePerGameObject;
			this.drawable = drawable;
		}

		public int getAmount() {
			return amount;
		}

		public int getScorePerGameObject() {
			return scorePerGameObject;
		}

		public Drawable getDrawable() {
			return drawable;
		}
		
		public Actor getActor() {
			return actor;
		}

		public void setActor(Actor actor) {
			this.actor = actor;
		}
	}
}
