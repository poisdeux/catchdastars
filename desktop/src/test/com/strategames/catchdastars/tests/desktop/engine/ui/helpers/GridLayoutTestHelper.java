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

package com.strategames.catchdastars.tests.desktop.engine.ui.helpers;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.strategames.engine.scenes.scene2d.ui.GridLayout;

public class GridLayoutTestHelper {

	private GridLayout gridLayout;

	@Before
	public void setUp() throws Exception {
		gridLayout = new GridLayout();
	}

	@Test
	public void testSet() {
		int rows = 3;
		int columns = 3;
		
		Array<Actor> actors = new Array<Actor>();
		int amount = rows * columns;
		for(int i = 0; i < amount; i++) {
			actors.add(new Actor());
		}
		
		int index = 0;
		Actor actor2 = new Actor();
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				gridLayout.set(j, i, actors.get(index++));
			}
		}
		
		gridLayout.set(9, 13, actor2);
		assertTrue("+gridLayout.getElements().size="+gridLayout.getElements().size+" should be 10", +gridLayout.getElements().size == 10);
		assertTrue("Actor at 13,9 not equal to actor originally set in grid", gridLayout.get(9, 13) == actor2);

		index=0;
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				assertTrue("Actor at ("+j+","+i+" not equal to actor originally set in grid", gridLayout.get(j, i) == actors.get(index++));
			}
		}
		
		//Test overwriting
		Actor actor = new Actor();
		gridLayout.set(1, 1, actor);
		assertTrue("Actor at (1,1) not equal to actor originally set in grid", gridLayout.get(1, 1) == actor);
	}
	
	@Test
	public void testGetPosition() {
		Actor actor1 = new Actor();
		gridLayout.set(0, 0, actor1);
		
		Actor actor2 = new Actor();
		gridLayout.set(9, 13, actor2);
		
		int[] position = gridLayout.getPosition(actor1);
		assertTrue("getPosition="+position[0]+","+position[1]+" not equal to 0,0", (position[0] == 0) && (position[1] == 0));
		position = gridLayout.getPosition(actor2);
		assertTrue("getPosition="+position[0]+","+position[1]+" not equal to 0,0", (position[0] == 9) && (position[1] == 13));
	}
	
	@Test
	public void testGet() {
		Actor actor1 = new Actor();
		gridLayout.set(0, 0, actor1);
		
		Actor actor2 = new Actor();
		gridLayout.set(1, 0, actor2);
		
		Actor actor3 = new Actor();
		gridLayout.set(0, 1, actor3);
		
		Actor actor4 = new Actor();
		gridLayout.set(0, 2, actor4);
		
		Actor actor5 = new Actor();
		gridLayout.set(0, 1, actor5);
		
		assertTrue("actor not found at 0,0", gridLayout.get(0,0) == actor1);
		assertTrue("actor not found at 1,0", gridLayout.get(1, 0) == actor2);
		assertTrue("Wrong actor found at 0,1", gridLayout.get(0,1) != actor3);
		assertTrue("actor not found at 0,2", gridLayout.get(0,2) == actor4);
		assertTrue("actor not found at 0,1", gridLayout.get(0,1) == actor5);
		assertNull("actor found at 1,1", gridLayout.get(1,1));
	}
	
	@Test
	public void testLayout() {
		Actor actor = new Actor();
		gridLayout.set(2, 2, actor);
		
		gridLayout.layout();
	}
}
