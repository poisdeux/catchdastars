/**
 * 
 * Copyright 2013 Martijn Brekhof
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

package com.strategames.catchdastars.dialogs;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.types.ChalkLine;
import com.strategames.engine.gameobject.types.ChalkLine.ChalkLineAnimationListener;
import com.strategames.engine.sounds.CashRegisterOpenSound;
import com.strategames.engine.sounds.DrawChalkLineShort1Sound;
import com.strategames.engine.sounds.DrawChalkLineShort2Sound;
import com.strategames.engine.sounds.DrawChalkLineSound;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.utils.Score;
import com.strategames.engine.utils.Textures;
import com.strategames.ui.dialogs.Dialog;
import com.strategames.ui.dialogs.GameStateDialog;

public class LevelCompleteDialog extends GameStateDialog implements ChalkLineAnimationListener {
	
	public final static int BUTTON_QUIT_CLICKED = BUTTON_NEGATIVE;
	public final static int BUTTON_NEXT_CLICKED = BUTTON_POSITIVE;

    private Score score;
    private Iterator<Score.ScoreItem> scoreItemIterator;

	private ArrayList<ScoreActor> scoreActors;
	private ArrayList<ChalkLine> chalkLines;
	private float maxRowHeight;
	private float maxImageHeight;
	private float top;
	private final int padding = 30;
	private int count;
	private int delay = 10;
	private int delayCount;
	private Label totalScoreLabel;
	private Table cashRegister;

    private int totalScore;

	private int animationPhase;
	private Vector2 animPosition;

	private float IMAGEHEIGHT = GameEngine.convertWorldToScreen(0.60f);

	private DrawChalkLineShort1Sound drawChalkLineShort1Sound = new DrawChalkLineShort1Sound();
	private DrawChalkLineShort2Sound drawChalkLineShort2Sound = new DrawChalkLineShort2Sound();
	private DrawChalkLineSound drawChalkLineSound = new DrawChalkLineSound();
	private CashRegisterOpenSound cashRegisterOpenSound = new CashRegisterOpenSound();
	
	/**
	 * Shows a scoreboard animation
	 * @param stage the stage to use to show the score
	 * @param skin
	 */
	public LevelCompleteDialog(Stage stage, Skin skin, Score score) {
		super("Level complete", stage, skin);
		this.animPosition = new Vector2();
		this.chalkLines = new ArrayList<ChalkLine>();
		this.score = score;

		setNegativeButton("Quit");
		setPositiveButton("Next level");
	}

	public Dialog create() {
		super.create();

        this.scoreActors = new ArrayList<ScoreActor>();

		this.animationPhase = -1;

        Collection<Score.ScoreItem> items = score.getScoreItems();

        this.scoreItemIterator = items.iterator();

		int amountOfItems = items.size();
		float availableScreenHeight = super.stage.getHeight() - (this.padding * (amountOfItems + 2));
		float height = availableScreenHeight / (amountOfItems + 2);
		
		this.maxImageHeight = height < IMAGEHEIGHT ? height : IMAGEHEIGHT;
		
		return this;
	}
	
	@Override
	public void show() {
		super.show();
		animationController();
	}
	
	private void animationController() {
		this.animationPhase++;
		switch(this.animationPhase) {
		case 0:
			this.top = super.stage.getHeight() - (this.maxImageHeight * 2);
			showScoreActor();
			break;
		case 1:
			this.animPosition.y -= 2 * padding;
			this.animPosition.x = 50f;
			ChalkLine line = new ChalkLine(this.animPosition.x, 
					this.animPosition.y, 
					350f, 
					this.animPosition.y, 420, LevelCompleteDialog.this);
			super.stage.addActor(line);
			drawChalkLineSound.play();
			this.chalkLines.add(line);
			break;
		case 2:
			this.animPosition.x = 350f + (this.padding * 2);
			line = new ChalkLine(this.animPosition.x, this.animPosition.y, 
					this.animPosition.x + 50f, this.animPosition.y, 220, this);
			super.stage.addActor(line);
			drawChalkLineShort2Sound.play();
			this.chalkLines.add(line);
			break;
		case 3:
			this.animPosition.x += 25f;
			line = new ChalkLine(this.animPosition.x, this.animPosition.y + 25f, 
					this.animPosition.x, this.animPosition.y - 25f, 210, this);
			super.stage.addActor(line);
			drawChalkLineShort1Sound.play();
			this.chalkLines.add(line);
			break;
		case 4:
			this.animPosition.x = 100f;
			this.animPosition.y -= (2 * padding ) ;
			showCashRegistry(this.animPosition.x, this.animPosition.y);
			break;
		case 5:
            totalScore = score.getCumulatedScore() - score.getTotalScoreFromScoreItems();
            for(int i = 0; i < this.scoreActors.size(); i++) {
                calculateTotalAnimation(i, this.scoreActors.get(i), this.animPosition.y);
            }
			break;
		case 6:
			showTotalScore();
			break;
		}
	}

	private void showScoreActor() {

        if( ! scoreItemIterator.hasNext() ) { //start next animation phase
            animationController();
            return;
        }

        Score.ScoreItem scoreItem = scoreItemIterator.next();

		final int increment = scoreItem.getMultiplier();
		final int amount = scoreItem.getScore();
		final SoundEffect incrementSound = SoundEffect.getSoundForIncrement(increment);

		Image image = scoreItem.getImage();
		image.setScaling(Scaling.stretch);
		double scaleFactor = this.maxImageHeight / (double) image.getHeight() ;
		float height = this.maxImageHeight;
		float width = (float) (image.getWidth() * scaleFactor);

		Table scoreItemTable = new Table();
		scoreItemTable.add(image).padRight(20f).width(width).height(height);

		final Label label = new Label("", getSkin());
		scoreItemTable.add(label).width(50f);

		this.animPosition.x = 100f;
		this.animPosition.y = this.top - (( this.maxImageHeight + this.padding )  * scoreActors.size());

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
									incrementSound.play();
								}
								count += increment;
								delayCount = delay;
							}
							delayCount--;
							return false;
						} else {
							showScoreActor();
							return true;
						}
					}

				}));

		scoreActors.add(new ScoreActor(scoreItemTable, scoreItem));
		super.stage.addActor(scoreItemTable);
	}

	private void showCashRegistry(final float x, final float y) {
		this.cashRegister = new Table();
		final float scale = 1.2f;
		Image image = new Image(Textures.getInstance().cashRegister);
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
		this.totalScoreLabel = new Label(String.valueOf(this.score.getCumulatedScore() - this.score.getTotalScoreFromScoreItems()), getSkin());
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

		super.stage.addActor(this.cashRegister);
		
		cashRegisterOpenSound.play();
	}

	private void calculateTotalAnimation(final int number, final ScoreActor scoreActor, final float y) {
        Action actionCountScore = new Action() {

			@Override
			public boolean act(float delta) {
                int actorScore = scoreActor.getScoreItem().getScore();
				if( actorScore > 0 ) {
                    totalScore += actorScore;
					SoundEffect.getSoundForIncrement(actorScore).play();
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

        Actor actor = scoreActor.getActor();
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
	}

	private void showTotalScore() {
		float x = (super.stage.getWidth() / 2f) - (this.cashRegister.getWidth() / 2f);
		float y = (super.stage.getHeight() / 2f) - (this.cashRegister.getHeight() / 2f);
		
		int size = this.chalkLines.size();
		for(int i = 0; i < size; i++) {
			ChalkLine line = this.chalkLines.get(i);
			line.addAction(fadeOut(1f, Interpolation.circleOut));
		}
		
		getMessageLabel().addAction(fadeOut(1f, Interpolation.circleOut));
		
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


	private class ScoreActor {
		private Score.ScoreItem scoreItem;
		private Actor actor;

        private ScoreActor() {}

		public ScoreActor(Actor actor, Score.ScoreItem scoreItem) {
			this.scoreItem = scoreItem;
            this.actor = actor;
		}

		public Actor getActor() {
			return actor;
		}

        public Score.ScoreItem getScoreItem() {
            return scoreItem;
        }
    }
}
