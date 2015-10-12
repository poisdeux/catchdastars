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

package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Door;
import com.strategames.engine.gameobject.types.Wall;
import com.strategames.engine.gameobject.types.WallHorizontal;
import com.strategames.engine.gameobject.types.WallVertical;
import com.strategames.engine.tweens.GameObjectAccessor;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class AnimatedDoor extends Door {

    private Wall top;
    private Wall right;

    private GameEngine gameEngine;

    private Action openAction;

    @Override
    protected GameObject newInstance() {
        return new AnimatedDoor();
    }

    public void open(GameEngine gameEngine) {
        super.open();

        this.gameEngine = gameEngine;

        Wall w = getWall();
        if(w instanceof WallVertical) {
            openVerticalWall(w);
        } else {
            openHorizontalWall(w);
        }
        w.setCanBeRemoved(true);
        gameEngine.deleteGameObject(w);
    }

    public void close() {
        super.close();
        if( top != null ) {
            closeVertical();
        } else {
            closeHorizontal();
        }
    }

    private void openVerticalWall(Wall w) {
        float x = getX();
        float y = getY();

        Wall bottom = new WallVertical();
        bottom.setPosition(w.getX(), w.getY());
        bottom.setLength(y - w.getY());
        gameEngine.addGameObject(bottom, getStage());

        top = new WallVertical();
        top.setPosition(x, y);
        top.setLength(w.getLength() - bottom.getLength());
        gameEngine.addGameObject(top, getStage());

        openAction = Actions.moveBy(0, getWidth(), 1f, Interpolation.linear);
        top.addAction(openAction);
    }

    private void openHorizontalWall(Wall w) {
        float x = getX();
        float y = getY();

        Wall left = new WallHorizontal();
        left.setPosition(w.getX(), w.getY());
        left.setLength(x - w.getX());
        gameEngine.addGameObject(left, getStage());

        right = new WallHorizontal();
        right.setPosition(x, y);
        right.setLength(w.getLength() - left.getLength());
        gameEngine.addGameObject(right, getStage());

        gameEngine.getWorldThread().startTimeline(Tween.to(right, GameObjectAccessor.POSITION_X, 1f)
                .target(x + getWidth()));
    }

    private void closeHorizontal() {
        gameEngine.getWorldThread().removeTargetAnimation(right);
        gameEngine.getWorldThread().startTimeline(Tween.to(right, GameObjectAccessor.POSITION_X, 1f)
                .target(getX()));
    }

    private void closeVertical() {
        top.removeAction(openAction);

        openAction = Actions.moveBy(0, -getWidth(), 1f, Interpolation.linear);
        top.addAction(openAction);
    }
}
