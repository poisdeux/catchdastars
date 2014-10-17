package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.game.Game;
import com.strategames.engine.game.GameTestClass;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Icecube;
import com.strategames.engine.gameobject.types.Icecube.Part;



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
	public void testAvailabeParts() {
		for( Part part : this.availableParts ) {
			assertNotNull(part.getName());
			assertNotNull(part.getSprite());
		}
	}

	//TODO add test for splitobject
	@Test
	public void testSplitObject() {
		Icecube icecube = (Icecube) getGameObject();
		Game game = new GameTestClass();
		game.setWorld(this.world);
		icecube.setGame(game);
		icecube.setupImage();
		icecube.setupBody();
		ArrayList<Part> parts = icecube.getParts();
		if( parts == null ) {
			fail("icecube has no parts");
		}
		testSplitObjectOnPart(parts.get(0), icecube);
	}

	private void testSplitObjectOnPart(Part part, Icecube icecube) {
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
			assertNotNull(icecube2Part.getName());
			assertNotNull(icecube2Part.getSprite());
		}
	}

	@Test
	public void testHandleCollision() {
		Game game = new GameTestClass();
		game.setWorld(this.world);
		ArrayList<Icecube> result = new ArrayList<Icecube>();
		Icecube icecube = new Icecube();
		icecube.addAllParts();
		icecube.setGame(game);
		icecube.setupImage();
		icecube.setupBody();

		breakObject(icecube, result);

		assertBreakObjectResults(result);
	}

	private void assertBreakObjectResults(ArrayList<Icecube> result) {
		ArrayList<Part> partsNotFound = new ArrayList<Icecube.Part>();
		for( Part availablePart : availableParts ) {
			for(Icecube icecube : result ) {
				ArrayList<Part> parts = icecube.getParts();
				assertTrue("Amount of parts is not 1", parts.size() == 1);
				if( availablePart != parts.get(0) ) {
					partsNotFound.add(availablePart);
				}
			}
		}
		if( partsNotFound.size() == 0 ) {
			StringBuffer partsInfo = new StringBuffer();
			for( Part part : partsNotFound ) {
				partsInfo.append(part.toString() + "\n");
			}
			fail("Parts not found: "+partsInfo);
		}
	}

	@Test
	public void testFixture() {
		int amountOfIcecubes = 10;
		Game game = new GameTestClass();
		game.setWorld(this.world);
		ArrayList<Icecube> icecubes = new ArrayList<Icecube>();
		for(int i = 0; i < amountOfIcecubes; i++) {
			Icecube icecube = new Icecube();
			icecube.addAllParts();
			icecube.setGame(game);
			icecube.setupImage();
			icecube.setupBody();
			icecubes.add(icecube);
		}

		for( Icecube icecube : icecubes ) {
			Body body = icecube.getBody();
			Array<Fixture> fixtures = body.getFixtureList();
			for(Fixture fixture : fixtures) {
				assertTrue("Body of fixture="+fixture+" not equal to body in gameobject", fixture.getBody() == body);
			}
		}
	}

	private void breakObject(Icecube icecube, ArrayList<Icecube> result) {
		MyContact myContact = new MyContact();
		MyContactImpulse impulse = new MyContactImpulse();
		
		while(icecube.getParts().size() > 1) {
			Game game = icecube.getGame();
			assertNotNull(game);
			
			Body body = icecube.getBody();
			assertNotNull(body);

			Array<Fixture> fixtures = body.getFixtureList();
			myContact.setFixtureA(fixtures.get(0));
			Fixture fixture = myContact.getFixtureA();
			assertTrue("Body of fixture="+fixture+" not equal to body in icecube", fixture.getBody() == body);

			Integer fixtureId = (Integer) fixture.getUserData();
			assertNotNull(fixtureId);
			ArrayList<Part> parts = icecube.getParts();
			Part part = parts.get(fixtureId);
			assertNotNull(part);

			icecube.handleCollision(myContact, impulse, null);
			Part breakOffPart = icecube.getBreakOfPart();
			assertTrue("breakOffPart("+breakOffPart+") not equal to part("+part+")", breakOffPart == part);

			Icecube icecube1 = new Icecube();
			Icecube icecube2 = new Icecube();
			icecube.splitObject(breakOffPart, icecube1, icecube2);
			icecube1.setGame(game);
			icecube1.setupImage();
			icecube1.setupBody();
			icecube2.setGame(game);
			icecube2.setupImage();
			icecube2.setupBody();
			
			icecube.getBody().setActive(false);
			icecube.remove();

			this.world.step(1/60f, 2, 6);

			icecube = icecube2;

			result.add(icecube1);
		}
	}

	private class MyContact extends Contact {

		private Fixture fixtureA;
		private Fixture fixtureB;

		public MyContact() {
			super(null, 0);
		}

		public void setFixtureA(Fixture fixtureA) {
			this.fixtureA = fixtureA;
		}

		@Override
		public Fixture getFixtureA() {
			return this.fixtureA;
		}

		public void setFixtureB(Fixture fixtureB) {
			this.fixtureB = fixtureB;
		}

		@Override
		public Fixture getFixtureB() {
			return this.fixtureB;
		}
	}

	private class MyContactImpulse extends ContactImpulse {
		float[] normalImpulses = {300};

		protected MyContactImpulse() {
			super(null, 0);
		}

		@Override
		public float[] getNormalImpulses() {
			return normalImpulses;
		}
	}
}
