package com.strategames.engine.gameobjects;

import static org.junit.Assert.*;


public class WallTest extends GameObjectTestAbstractClass {

	@Override
	GameObject createGameObject() {
		Wall wall = new Wall();
		return wall;
	}

	@Override
	public void assertForEquality(GameObject object1, GameObject object2) {
		Wall b1 = (Wall) object1;
		Wall b2 = (Wall) object2;
		
		assertTrue("Length not equal", b1.getLength() == b2.getLength());
		assertTrue("Orientation not equal", b1.getOrientation() == b2.getOrientation());
	}
}
