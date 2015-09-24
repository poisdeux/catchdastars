/**
 * 
 * Copyright 2015 Martijn Brekhof
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

package com.strategames.engine.utils;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by martijn on 1-4-15.
 */
public class Score {
    private int cumulatedScore;

    private LinkedHashMap<String, ScoreItem> scoreItems = new LinkedHashMap<String, ScoreItem>();

    public Collection<ScoreItem> getScoreItems() {
        return scoreItems.values();
    }

    public ScoreItem getScoreItem(String name) {
        return scoreItems.get(name);
    }

    /**
     * Adds all scores from the current score items
     * @return total score for current score items
     */
    public int getTotalScoreFromScoreItems() {
        int score = 0;
        for( ScoreItem item : scoreItems.values() ) {
            score += item.getScore();
        }

        return score;
    }


    public int getCumulatedScore() {
        return cumulatedScore;
    }

    /**
     * Sets the cumulated score to the value provided by cumulatedScore
     * @param cumulatedScore
     */
    public void setCumulatedScore(int cumulatedScore) {
        this.cumulatedScore = cumulatedScore;
    }

    /**
     * Sets the scored amount for score item with name.
     * Each time you set the score it also updates the cumulated score
     * which you can retrieve using {@link #getCumulatedScore()}
     * @param name
     * @param amount
     */
    public void setAmount(String name, int amount) {
            ScoreItem item = scoreItems.get(name);
            if( item != null ) {
                item.amount = amount;
                cumulatedScore += item.getScore();
            }
    }

    public void addItem(String name, Image image, int multiplier) {
        scoreItems.put(name, new ScoreItem(image, multiplier, 0));
    }

    public class ScoreItem {
        private Image image;
        private int multiplier;
        private int amount;

        ScoreItem(Image image, int multiplier, int amount) {
            this.image = image;
            this.multiplier = multiplier;
            this.amount = amount;
        }

        public int getScore() {
            return amount * multiplier;
        }

        public Image getImage() {
            return image;
        }

        public int getMultiplier() {
            return multiplier;
        }
    }
}
