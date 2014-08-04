package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertArrayEquals;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.strategames.engine.gameobjects.Icecube.Part;



public class IcecubeTest extends GameObjectTestAbstractClass {

	@Override
	GameObject createGameObject() {
		Icecube o = new Icecube();
		new World(new Vector2(0f, -1f), true); // needed to make sure box2d libraries are loaded
		o.addAllParts();
		return o;
	}

	@Override
	public void assertForEquality(GameObject object1, GameObject object2) {
		Icecube icecube1 = (Icecube) object1;
		Icecube icecube2 = (Icecube) object2;
		ArrayList<Part> partsIcecube1 = icecube1.getParts();
		ArrayList<Part> partsIcecube2 = icecube2.getParts();
		assertArrayEquals("Parts not equal", partsIcecube1.toArray(new Part[partsIcecube1.size()]), partsIcecube2.toArray(new Part[partsIcecube2.size()]));
	}
	
	//TODO add test for splitobject
	public void splitObjectTest() {
		
	}
}
