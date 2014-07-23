package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertTrue;

import com.strategames.catchdastars.gameobjects.BalloonBlue;


public class BalloonBlueTest extends GameObjectTestAbstractClass {

	@Override
	GameObject createGameObject() {
		return new BalloonBlue();
	}

	@Override
	public void assertForEquality(GameObject object1, GameObject object2) {
		Balloon b1 = (Balloon) object1;
		Balloon b2 = (Balloon) object2;
		
		assertTrue("LiftFactor not equal", b1.getLiftFactor() == b2.getLiftFactor());
	}
	

}
