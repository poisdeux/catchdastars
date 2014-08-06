package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.strategames.engine.game.Game;
import com.strategames.engine.game.GameTestClass;
import com.strategames.engine.gameobjects.Icecube.Part;



public class IcecubeTest extends GameObjectTestAbstractClass {

	private World world;
	private ArrayList<Part> availableParts;
	@Override
	GameObject createGameObject() {
		Icecube o = new Icecube();
		this.world = new World(new Vector2(0f, -1f), true); // needed to make sure box2d libraries are loaded
		o.addAllParts();
		this.availableParts = Icecube.getAvailableParts();
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
	
	@Test
	public void availablePartsTest() {
		for( Part part : this.availableParts ) {
			assertNotNull(part.getName());
			assertNotNull(part.getSprite());
		}
	}
	
	//TODO add test for splitobject
	@Test
	public void splitObjectTest() {
		Icecube icecube = (Icecube) getGameObject();
		Game game = new GameTestClass();
		game.setWorld(this.world);
		icecube.setGame(game);
		icecube.setup();
		ArrayList<Part> parts = icecube.getParts();
		if( parts == null ) {
			fail("icecube has no parts");
		}
		testPart(parts.get(0), icecube);
	}
	
	private void testPart(Part part, Icecube icecube) {
		if( part == null ) {
			return;
		}
		Icecube icecube1 = new Icecube();
		Icecube icecube2 = new Icecube();
		icecube.splitObject(part, icecube1, icecube2);
		ArrayList<Part> icecube1Parts = icecube1.getParts();
		if( icecube1Parts == null ) {
			fail("icecube1 has no parts");
		}
		Part icecube1Part = icecube1Parts.get(0);
		assertTrue("Icecube1 should contain a single part", icecube1Parts.size() == 1);
		assertTrue("Icecube1 should contain part="+part+" but contains part="+icecube1Part, icecube1Part == part);
		assertNotNull(icecube1Part.getName());
		assertNotNull(icecube1Part.getSprite());
		
		ArrayList<Part> icecube2Parts = icecube2.getParts();
		if( icecube2Parts == null ) {
			fail("icecube2 has no parts");
		}
		assertTrue("Icecube2 should contain remaining parts", icecube2Parts.size() == (icecube.getParts().size() - 1));
		
		int size = icecube2Parts.size();
		for(int i = 0; i < size; i++) {
			Part icecube2Part = icecube2Parts.get(i);
			assertTrue("availablePart does not contain part: "+icecube2Part, this.availableParts.contains(icecube2Part));
			assertNotNull(part.getName());
			assertNotNull(part.getSprite());
		}
		
		if(icecube2Parts.size() > 1) {
			testPart(icecube2Parts.get(0), icecube2);
		}
	}
}
