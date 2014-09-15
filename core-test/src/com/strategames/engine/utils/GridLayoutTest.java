package com.strategames.engine.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class GridLayoutTest {

	private GridLayout gridLayout;

	@Before
	public void setUp() throws Exception {
		gridLayout = new GridLayout();
	}

	@Test
	public void testAddRow() {
		gridLayout.addRow();
		try {
			Array<Actor> row = gridLayout.getRow(0);
			assertTrue("row.size not 1 but "+row.size, row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
		try {
			Array<Actor> column = gridLayout.getColumn(0);
			assertTrue("column.size not 1 but "+column.size, column.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
	}

	@Test
	public void testAddColumn() {
		gridLayout.addColumn();
		try {
			Array<Actor> row = gridLayout.getRow(0);
			assertTrue("row.size not 1 but "+row.size, row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
		try {
			Array<Actor> column = gridLayout.getColumn(0);
			assertTrue("column.size not 1 but "+column.size, column.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
	}

	@Test
	public void testAddColumnAndRows() {
		gridLayout.addRow();
		gridLayout.addColumn();
		try {
			Array<Actor> row = gridLayout.getRow(0);
			assertTrue("row.size not 2 but "+row.size, row.size == 2);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
	}

	@Test
	public void testAddRowsAndColumn() {
		gridLayout.addColumn();
		gridLayout.addRow();
		try {
			Array<Actor> row = gridLayout.getRow(0);
			assertTrue("row.size not 1 but "+row.size, row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
		try {
			Array<Actor> row = gridLayout.getRow(1);
			assertTrue("row.size not 1 but "+row.size, row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
	}
	
	@Test
	public void testGetSize() {
		gridLayout.addColumn();
		assertTrue("gridLayout.getSize().x != 1", gridLayout.getSize()[0] == 1);
		assertTrue("gridLayout.getSize().y != 1", gridLayout.getSize()[1] == 1);
		
		gridLayout.addRow();
		assertTrue("gridLayout.getSize().x != 1", gridLayout.getSize()[0] == 1);
		assertTrue("gridLayout.getSize().y != 2", gridLayout.getSize()[1] == 2);
		
		gridLayout.addColumn();
		assertTrue("gridLayout.getSize().x != 2", gridLayout.getSize()[0] == 2);
		assertTrue("gridLayout.getSize().y != 2", gridLayout.getSize()[1] == 2);
		
		gridLayout.addRow();
		assertTrue("gridLayout.getSize().x != 2", gridLayout.getSize()[0] == 2);
		assertTrue("gridLayout.getSize().y != 3", gridLayout.getSize()[1] == 3);
		
		gridLayout.addRow();
		assertTrue("gridLayout.getSize().x != 2", gridLayout.getSize()[0] == 2);
		assertTrue("gridLayout.getSize().y != 4", gridLayout.getSize()[1] == 4);
		
		gridLayout.addColumn();
		assertTrue("gridLayout.getSize().x != 3", gridLayout.getSize()[0] == 3);
		assertTrue("gridLayout.getSize().y != 4", gridLayout.getSize()[1] == 4);
	}

	@Test
	public void testRemoveRow() {
		gridLayout.addRow();
		gridLayout.addColumn();
		gridLayout.addRow();
		gridLayout.addRow();
		gridLayout.deleteRow(1);
		assertTrue("gridLayout.getSize().x != 2", gridLayout.getSize()[0] == 2);
		assertTrue("gridLayout.getSize().y != 2", gridLayout.getSize()[1] == 2);
	}

	@Test
	public void testRemoveColumn() {
		gridLayout.addRow();
		gridLayout.addColumn();
		gridLayout.addRow();
		gridLayout.addRow();
		gridLayout.addColumn();
		gridLayout.deleteColumn(1);
		assertTrue("gridLayout.getSize().x != 2", gridLayout.getSize()[0] == 2);
		assertTrue("gridLayout.getSize().y != 3", gridLayout.getSize()[1] == 3);
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
		assertTrue("gridLayout.getSize().x="+gridLayout.getSize()[0]+" should be 10", gridLayout.getSize()[0] == 10);
		assertTrue("gridLayout.getSize().y="+gridLayout.getSize()[1]+" should be 14", gridLayout.getSize()[1] == 14);
		assertTrue("Actor at 13,9 not equal to actor originally set in grid", gridLayout.get(9, 13) == actor2);

		index=0;
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				assertTrue("Actor at ("+j+","+i+" not equal to actor originally set in grid", gridLayout.get(j, i) == actors.get(index++));
				
			}
		}
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
}
