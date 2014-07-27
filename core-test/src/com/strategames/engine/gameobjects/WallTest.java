package com.strategames.engine.gameobjects;

import static org.junit.Assert.*;

import org.junit.Test;


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
		
		assertTrue("Length not equal: "+b1.getLength()+" != "+b2.getLength(), b1.getLength() == b2.getLength());
		assertTrue("Orientation not equal: "+b1.getOrientation().name()+" != "+b2.getOrientation().name(), b1.getOrientation() == b2.getOrientation());
	}
	
	@Override
	@Test
	public void testDraw() {
		super.testDraw();
		GameObject gameObject = getGameObject();
		if( gameObject instanceof Wall ) {
			((Wall) gameObject).setWrap(true);
		} else {
			fail("gameObject not instance of Wall");
		}
	}
}
