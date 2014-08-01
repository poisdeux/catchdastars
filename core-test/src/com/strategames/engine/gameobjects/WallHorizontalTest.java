package com.strategames.engine.gameobjects;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.game.GameTestClass;


public class WallHorizontalTest extends GameObjectTestAbstractClass {

	@Override
	GameObject createGameObject() {
		Wall wall = new WallHorizontal();
		return wall;
	}

	@Override
	public void assertForEquality(GameObject object1, GameObject object2) {
		Wall b1 = (Wall) object1;
		Wall b2 = (Wall) object2;
		
		assertTrue("Length not equal: "+b1.getLength()+" != "+b2.getLength(), b1.getLength() == b2.getLength());
	}
	
	@Test
	public void testSetLength() {
		Wall wall = (Wall) getGameObject();
		wall.setLength(4.2f);
		assertTrue("Width not equal to length: "+wall.getWidth() +" != "+ wall.getLength(), wall.getWidth() == wall.getLength());
		assertTrue("Height not equal to HEIGHT constant: "+wall.getHeight() + " != "+ Wall.WIDTH, wall.getHeight() == Wall.WIDTH);
	}
	
	@Test
	public void testBodySize() {
		Wall wall = (Wall) getGameObject();
		GameTestClass game = new GameTestClass();
		game.setWorld(new World(new Vector2(0,1), true));
		wall.setLength(5f);
		wall.setGame(game);
		wall.setup();
		Array<Fixture> fixtures = wall.getBody().getFixtureList();
		if( fixtures == null ) {
			fail("fixtures is null");
		}
		assertTrue("Size of fixtures is not equal to 1", fixtures.size == 1);
		Shape shape = fixtures.get(0).getShape();
		if( ! (shape instanceof PolygonShape ) ) {
		 fail("Fixture not of shape PolygonShape");
		}
		PolygonShape polygonShape = (PolygonShape) shape;
		if( polygonShape.getVertexCount() != 4 ) {
			fail("Amount of vertices not equal to 4");
		}
		Vector2 vertex = new Vector2();  
		polygonShape.getVertex(2, vertex);
		assertTrue("Body length not equal to wall length: " + wall.getLength() +" != " + vertex.x, wall.getLength() == vertex.x);
		assertTrue("Body height not equal to wall height: " + wall.getHeight() +" != " + vertex.y, wall.getHeight() == vertex.y);
	}
}
