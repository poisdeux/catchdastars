/**
 * 
 * Copyright 2014 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.catchdastars.tests.desktop.engine.gameobjects;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.strategames.catchdastars.tests.desktop.libgdx.junit.GameTestClass;
import com.strategames.engine.gameobject.GameObject;
import com.strategames.engine.gameobject.types.Wall;
import com.strategames.engine.gameobject.types.WallHorizontal;


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
		wall.setupImage();
		wall.setupBody();
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
