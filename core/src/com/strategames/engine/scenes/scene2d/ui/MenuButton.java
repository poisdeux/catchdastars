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

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;
import com.strategames.engine.utils.Textures;

public class MenuButton extends com.badlogic.gdx.scenes.scene2d.ui.Button {
	private float IMAGEWIDTH = GameEngine.convertWorldToScreen(0.30f);
	private static Drawable drawable = new TextureRegionDrawable(Textures.getInstance().menu);
	private EventHandler eventHandler = new EventHandler(this);

	public MenuButton() {
		super(drawable);
		setSize(IMAGEWIDTH, IMAGEWIDTH);
		addListener(this.eventHandler);
	}

	public void setListener(ActorListener listener) {
		this.eventHandler.setListener(listener);
	}
}
