package com.strategames.engine.gameobjects;

import static org.junit.Assert.*;


public class BalloonTest extends GameObjectTestAbstractClass {

	@Override
	GameObject createGameObject() {
		Balloon balloon = new Balloon();
		return balloon;
	}

	@Override
	public void assertForEquality(GameObject object1, GameObject object2) {
		Balloon b1 = (Balloon) object1;
		Balloon b2 = (Balloon) object2;
		
		assertTrue("ColorType not equal", b1.getColorType() == b2.getColorType());
		assertTrue("LiftFactor not equal", b1.getLiftFactor() == b2.getLiftFactor());
	}
	

}
