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
    private LinkedHashMap<String, ScoreItem> scoreItems = new LinkedHashMap<String, ScoreItem>();

    public Collection<ScoreItem> getScoreItems() {
        return scoreItems.values();
    }

    public ScoreItem getScoreItem(String name) {
        return scoreItems.get(name);
    }

    public int getTotalScore() {
        int score = 0;
        for( ScoreItem item : scoreItems.values() ) {
            score += item.getScore();
        }

        return score;
    }

    public void setAmount(String name, int amount) {
            ScoreItem item = scoreItems.get(name);
            if( item != null ) {
                item.amount = amount;
            }
    }

    public void addItem(String name, Image image, int multiplier, int amount) {
        scoreItems.put(name, new ScoreItem(image, multiplier, amount));
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
