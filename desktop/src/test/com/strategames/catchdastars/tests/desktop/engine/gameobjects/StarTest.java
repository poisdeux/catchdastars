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

package com.strategames.catchdastars.tests.desktop.engine.gameobjects;

import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Star;
import com.strategames.engine.sounds.SoundEffect;
import com.strategames.engine.utils.Textures;


public class StarTest extends GameObjectTestAbstractClass {

	@Override
	GameObject createGameObject() {
		StarTestClass o = new StarTestClass();
		o.setRotationSpeed(0.2f);
		return o;
	}

	@Override
	public void assertForEquality(GameObject object1, GameObject object2) {
		Star b1 = (Star) object1;
		Star b2 = (Star) object2;
		
		assertTrue("RotationSpeed not equal", b1.getRotationSpeed() == b2.getRotationSpeed());
	}
	
	private class StarTestClass extends Star {
		
		public StarTestClass() {
			super();
		}
		
		@Override
		protected TextureRegion createImage() {
			return Textures.getInstance().starBlue;
		}

		@Override
		protected Star newInstance() {
			return new StarTestClass();
		}

		@Override
		protected SoundEffect getSoundCollected() {
			return null;
		}
	}
}
