package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

		@Override
		public void applyForce() {
			
		}
	}
}
