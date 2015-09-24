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

package com.strategames.catchdastars.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Star;
import com.strategames.engine.sounds.GlassSound;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.utils.Textures;

public class StarYellow extends Star {

	@Override
	protected TextureRegion createImage() {
		return Textures.getInstance().starYellow;
	}

	@Override
	protected GameObject newInstance() {
		return new StarYellow();
	}
	
	@Override
	protected SoundEffect getSoundCollected() {
		return new GlassSound();
	}
}
