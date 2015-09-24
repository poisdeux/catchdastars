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
import com.strategames.engine.gameobject.types.Balloon;
import com.strategames.engine.utils.Textures;


public class BalloonTest extends GameObjectTestAbstractClass {

	@Override
	GameObject createGameObject() {
		BalloonTestClass b = new BalloonTestClass();
		b.setLiftFactor(2.2f);
		return b;
	}

	@Override
	public void assertForEquality(GameObject object1, GameObject object2) {
		Balloon b1 = (Balloon) object1;
		Balloon b2 = (Balloon) object2;
		
		assertTrue("LiftFactor not equal", b1.getLiftFactor() == b2.getLiftFactor());
	}
	
	private class BalloonTestClass extends Balloon {
		
		public BalloonTestClass() {
			super();
		}
		
		@Override
		protected TextureRegion createImage() {
			return Textures.getInstance().balloonBlue;
		}

		@Override
		protected Balloon newInstance() {
			return new BalloonTestClass();
		}
	}
}
