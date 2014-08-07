package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
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

		if(icecube2Parts.size() > 1) {
			testSplitObjectOnPart(icecube2Parts.get(0), icecube2);
		}
	}

	@Test
	public void testForThreadSafetyHandleCollision() {
		int amountOfIcecubes = 10;
		Game game = new GameTestClass();
		game.setWorld(this.world);
		ArrayList<ArrayList<Icecube>> results = new ArrayList<ArrayList<Icecube>>();
		ArrayList<Icecube> icecubes = new ArrayList<Icecube>();
		for(int i = 0; i < amountOfIcecubes; i++) {
			Icecube icecube = new Icecube();
			icecube.addAllParts();
			icecube.setGame(game);
			icecube.setup();
			icecubes.add(icecube);

			results.add(new ArrayList<Icecube>());
		}
		
		CountDownLatch startLatch = new CountDownLatch(amountOfIcecubes);
		CountDownLatch stopLatch = new CountDownLatch(amountOfIcecubes);

		for(int i = 0; i < amountOfIcecubes; i++) {
			final Icecube icecube = icecubes.get(i);
			final ArrayList<Icecube> result = results.get(i);
			breakObject(icecube, result);
			
//			SynchronizedThread thread = new SynchronizedThread(new Runnable() {
//
//				@Override
//				public void run() {
//					breakObject(icecube, result);
//				}
//			}, startLatch, stopLatch);
//			thread.start();
		}
		
//		try {
//			stopLatch.await(2, TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			fail("Threads did not finish within timeout");
//		}
		
		assertBreakObjectResults(results);
	}

	private void assertBreakObjectResults(ArrayList<ArrayList<Icecube>> results) {
		for( int resultIndex = 0; resultIndex < results.size(); resultIndex++ ) {
			ArrayList<Icecube> result = results.get(resultIndex);
			ArrayList<Part> partsNotFound = new ArrayList<Icecube.Part>();
			for( Part availablePart : availableParts ) {
				for(Icecube icecube : result ) {
					ArrayList<Part> parts = icecube.getParts();
					assertTrue("Result "+resultIndex+": List of parts is not size 1", parts.size() == 1);
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
				fail("result "+resultIndex+" parts not found: "+partsInfo);
			}
			
		}
	}
	
	private void breakObject(Icecube icecube, ArrayList<Icecube> result) {
		MyContact myContact = new MyContact();
		MyContactImpulse impulse = new MyContactImpulse();

		while(icecube.getParts().size() > 0) {
			myContact.setFixtureA(icecube.getBody().getFixtureList().get(0));
			icecube.handleCollision(myContact, impulse, null);
			Icecube icecube1 = new Icecube();
			Icecube icecube2 = new Icecube();
			Part breakOffPart = icecube.getBreakOfPart();
			assertNotNull("breakOffPart is null for "+icecube, breakOffPart);
			icecube.splitObject(breakOffPart, icecube1, icecube2);

			icecube.deleteBody();
			icecube.remove();

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
		float[] normalImpulses = {30};

		protected MyContactImpulse() {
			super(null, 0);
		}

		@Override
		public float[] getNormalImpulses() {
			return normalImpulses;
		}
	}

	private class SynchronizedThread extends Thread {
		private CountDownLatch startLatch;
		private CountDownLatch stopLatch;

		public SynchronizedThread(Runnable runnable, CountDownLatch startLatch, CountDownLatch stopLatch) {
			super(runnable);
			this.startLatch = startLatch;
			this.stopLatch = stopLatch;
		}

		@Override
		public void run() {
			try { 
				startLatch.await(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			super.run();
			stopLatch.countDown();
		}

		@Override
		public synchronized void start() {
			startLatch.countDown();
			super.start();
		}
	}
}
