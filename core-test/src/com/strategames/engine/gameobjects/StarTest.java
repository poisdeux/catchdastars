package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertTrue;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
		protected TextureRegionDrawable createTexture() {
			return Textures.getInstance().starBlue;
		}

		@Override
		protected Star newInstance() {
			return new StarTestClass();
		}
	}
}
