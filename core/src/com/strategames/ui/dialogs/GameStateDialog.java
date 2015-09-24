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

package com.strategames.ui.dialogs;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;
import com.strategames.engine.scenes.scene2d.ui.TextButton;
import com.strategames.ui.helpers.FilledRectangleImage;
import com.strategames.ui.helpers.Screen;

/**
 *
 * @author mbrekhof
 *
 */
abstract public class GameStateDialog extends Dialog {
    public final static int BUTTON_LEFT_CLICKED = 0;
    public final static int BUTTON_RIGHT_CLICKED = 1;

    public static enum States {
        PAUSED, FAILED, COMPLETE
    }

    private String message;
    private Label messageLabel;
    private TextButton buttonLeft;
    private TextButton buttonRight;

    private FilledRectangleImage filter;

    protected Stage stage;

    protected GameStateDialog(String message, Stage stage, Skin skin) {
        super(stage, skin);
        this.message = message;
        this.stage = stage;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    public void setLeftButton(String text) {
        this.buttonLeft = new TextButton(text, getSkin());
        this.buttonLeft.setListener(new ActorListener() {

            @Override
            public void onTap(Actor actor) {
                notifyListener(BUTTON_LEFT_CLICKED);
            }

            @Override
            public void onLongPress(Actor actor) {

            }
        });
        this.buttonLeft.getColor().a = 0f;
        this.buttonLeft.addAction( sequence( fadeIn( 0.25f ) ) );
    }


    public void setRightButton(String text) {
        this.buttonRight = new TextButton(text, getSkin());
        this.buttonRight.setListener(new ActorListener() {

            @Override
            public void onTap(Actor actor) {
                notifyListener(BUTTON_RIGHT_CLICKED);
            }

            @Override
            public void onLongPress(Actor actor) {

            }
        });
        this.buttonRight.getColor().a = 0f;
        this.buttonRight.addAction( sequence( fadeIn( 0.25f ) ) );
    }

    @Override
    public Dialog create() {
        Vector2 start = new Vector2();
        Vector2 end = new Vector2();
        Screen.getFullScreenCoordinates(this.stage, start, end);

        this.filter = new FilledRectangleImage(this.stage);
        this.filter.setPosition(start.x, start.y);
        this.filter.setWidth(end.x);
        this.filter.setHeight(end.y);
        this.filter.setColor(0f, 0f, 0f, 0.4f);

        this.messageLabel = new Label(this.message, getSkin());
        float xMiddle = (super.stage.getWidth() / 2) - (this.messageLabel.getWidth() / 2);
        this.messageLabel.setPosition(xMiddle, super.stage.getHeight() / 2);
        this.messageLabel.addAction( fadeIn( 0.25f ) );
        this.messageLabel.getColor().a = 0f;
        addActor(this.messageLabel);

        setFillParent(true);

        return super.create();
    }

    @Override
    public void createButtons() {
        Table table = new Table(getSkin());
        table.setFillParent(true);
        table.bottom();

        TextButton button = getNegativeButton();
        if( button != null ) {
            table.add(button).expandX().fillX();
        }
        button = getNeutralButton();
        if( button != null ) {
            table.add(button).expandX().fillX();
        }
        button = getPositiveButton();
        if( button != null ) {
            table.add(button).expandX().fillX();
        }

        addActor(table);
    }

    @Override
    public void show() {
        if( ! this.stage.getActors().contains(this.filter, true)) {
            this.stage.addActor(this.filter);
        }
        this.filter.setVisible(true);
        super.show();
    }

    @Override
    public void hide() {
//		this.filter.remove();
        this.filter.setVisible(false);
        super.hide();
    }

    @Override
    public boolean remove() {
        this.filter.remove();
        return super.remove();
    }

    @Override
    public void setStyle(Style style) {
        //Make sure background is not set by Dialog class
    }
}
