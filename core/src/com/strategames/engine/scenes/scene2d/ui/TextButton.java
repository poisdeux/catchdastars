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

package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;

public class TextButton extends com.badlogic.gdx.scenes.scene2d.ui.TextButton {
	
	private EventHandler eventHandler = new EventHandler(this);
	private Object tag;
	
	public TextButton(String text, Skin skin) {
		super(text, skin);
		addListener(this.eventHandler);
	}

	
	public void setListener(ActorListener listener) {
			this.eventHandler.setListener(listener);
	}
	
	public void setTag(Object tag) {
		this.tag = tag;
	}
	
	public Object getTag() {
		return this.tag;
	}
}
