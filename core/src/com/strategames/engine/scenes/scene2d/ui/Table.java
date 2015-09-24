/**
 * 
 * Copyright 2014 Martijn Brekhof
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

package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Test class to determine why Table is correctly positioned in a ScrollPan
 * and GridLayout is not
 * @author martijn
 *
 */
public class Table extends com.badlogic.gdx.scenes.scene2d.ui.Table implements EventListener {

//		@Override
//		public void setPosition(float x, float y) {
//			Gdx.app.log("Table", "setPosition: x="+x+", y="+y);
//			super.setPosition(x, y);
//		}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height);
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
	}
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
	}

	@Override
	public boolean handle(Event e) {
		if (!(e instanceof InputEvent)) return false;
		InputEvent event = (InputEvent)e;
		Actor actor;
//		Gdx.app.log("GridLayout", "handle: event="+event.getType().name());
		switch (event.getType()) {
		case touchDown:
			actor = hit(event.getStageX(), event.getStageY(), false);
			if( actor == null ) {
				return false;
			}

			if( actor instanceof Label ) {
				Label label = (Label) actor;
			}
			return true;
		case touchUp:
			actor = hit(event.getStageX(), event.getStageY(), false);
			if( actor == null ) {
				return false;
			}

			if( actor instanceof Label ) {
				Label label = (Label) actor;
			}
			return true;
		default:
			return false;
		}
	}
	
	
}
