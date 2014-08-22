package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
