package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Test;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.strategames.engine.utils.Textures;


public class BalloonTest extends GameObjectTestAbstractClass {

	@Override
	GameObject createGameObject() {
		Balloon1TestClass b = new Balloon1TestClass();
		b.setLiftFactor(2.2f);
		return b;
	}

	@Override
	public void assertForEquality(GameObject object1, GameObject object2) {
		Balloon b1 = (Balloon) object1;
		Balloon b2 = (Balloon) object2;
		
		assertTrue("LiftFactor not equal", b1.getLiftFactor() == b2.getLiftFactor());
	}
	
	@Test
	public void testCreateTextureRegion() {
		Balloon b1 = new Balloon1TestClass();
		Balloon b2 = new Balloon2TestClass();
		b1.setup();
		b2.setup();
		assertTrue("TextureRegionDrawables of b1 and b2 are equal: "+b2.getTextureRegionDrawable() +" == "+ b1.getTextureRegionDrawable(), b2.getTextureRegionDrawable() != b1.getTextureRegionDrawable());
		
	}
	
	private class Balloon1TestClass extends Balloon {
		
		public Balloon1TestClass() {
			super();
		}
		
		@Override
		protected TextureRegion createTextureRegion() {
			return Textures.getInstance().balloonBlue;
		}

		@Override
		protected Balloon newInstance() {
			return new Balloon1TestClass();
		}
	}
	
	private class Balloon2TestClass extends Balloon {
		
		public Balloon2TestClass() {
			super();
		}
		
		@Override
		protected TextureRegion createTextureRegion() {
			return Textures.getInstance().balloonRed;
		}

		@Override
		protected Balloon newInstance() {
			return new Balloon1TestClass();
		}
	}
}
