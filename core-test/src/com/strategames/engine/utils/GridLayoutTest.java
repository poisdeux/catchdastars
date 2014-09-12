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
			assertTrue("row.size not 1", row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
		try {
			Array<Actor> column = gridLayout.getColumn(0);
			assertTrue("column.size not 1", column.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
	}

	@Test
	public void testAddColumn() {
		gridLayout.addColumn();
		try {
			Array<Actor> row = gridLayout.getRow(0);
			assertTrue("row.size not 1", row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
		try {
			Array<Actor> column = gridLayout.getColumn(0);
			assertTrue("column.size not 1", column.size == 1);
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
			assertTrue("row.size not 2", row.size == 2);
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
			assertTrue("row.size not 1", row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
		try {
			Array<Actor> row = gridLayout.getRow(1);
			assertTrue("row.size not 1", row.size == 1);
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
}
