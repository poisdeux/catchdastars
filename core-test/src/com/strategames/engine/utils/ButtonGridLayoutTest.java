package com.strategames.engine.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;

public class ButtonGridLayoutTest {

	private ButtonGridLayout buttonGridLayout;

	@Before
	public void setUp() throws Exception {
		buttonGridLayout = new ButtonGridLayout();
	}

	@Test
	public void testAddRow() {
		buttonGridLayout.addRow();
		try {
			Array<Button> row = buttonGridLayout.getRow(0);
			assertTrue("row.size not 1", row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
		try {
			Array<Button> column = buttonGridLayout.getColumn(0);
			assertTrue("column.size not 1", column.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
	}

	@Test
	public void testAddColumn() {
		buttonGridLayout.addColumn();
		try {
			Array<Button> row = buttonGridLayout.getRow(0);
			assertTrue("row.size not 1", row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
		try {
			Array<Button> column = buttonGridLayout.getColumn(0);
			assertTrue("column.size not 1", column.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
	}

	@Test
	public void testAddColumnAndRows() {
		buttonGridLayout.addRow();
		buttonGridLayout.addColumn();
		try {
			Array<Button> row = buttonGridLayout.getRow(0);
			assertTrue("row.size not 2", row.size == 2);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
	}

	@Test
	public void testAddRowsAndColumn() {
		buttonGridLayout.addColumn();
		buttonGridLayout.addRow();
		try {
			Array<Button> row = buttonGridLayout.getRow(0);
			assertTrue("row.size not 1", row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
		try {
			Array<Button> row = buttonGridLayout.getRow(1);
			assertTrue("row.size not 1", row.size == 1);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Array index out of bounds exception thrown");
		}
	}
	
	@Test
	public void testGetSize() {
		buttonGridLayout.addColumn();
		assertTrue("buttonGridLayout.getSize().x != 1", buttonGridLayout.getSize().x == 1);
		assertTrue("buttonGridLayout.getSize().y != 1", buttonGridLayout.getSize().y == 1);
		
		buttonGridLayout.addRow();
		assertTrue("buttonGridLayout.getSize().x != 1", buttonGridLayout.getSize().x == 1);
		assertTrue("buttonGridLayout.getSize().y != 2", buttonGridLayout.getSize().y == 2);
		
		buttonGridLayout.addColumn();
		assertTrue("buttonGridLayout.getSize().x != 2", buttonGridLayout.getSize().x == 2);
		assertTrue("buttonGridLayout.getSize().y != 2", buttonGridLayout.getSize().y == 2);
		
		buttonGridLayout.addRow();
		assertTrue("buttonGridLayout.getSize().x != 2", buttonGridLayout.getSize().x == 2);
		assertTrue("buttonGridLayout.getSize().y != 3", buttonGridLayout.getSize().y == 3);
		
		buttonGridLayout.addRow();
		assertTrue("buttonGridLayout.getSize().x != 2", buttonGridLayout.getSize().x == 2);
		assertTrue("buttonGridLayout.getSize().y != 4", buttonGridLayout.getSize().y == 4);
		
		buttonGridLayout.addColumn();
		assertTrue("buttonGridLayout.getSize().x != 3", buttonGridLayout.getSize().x == 3);
		assertTrue("buttonGridLayout.getSize().y != 4", buttonGridLayout.getSize().y == 4);
	}

	@Test
	public void testRemoveRow() {
		buttonGridLayout.addRow();
		buttonGridLayout.addColumn();
		buttonGridLayout.addRow();
		buttonGridLayout.addRow();
		buttonGridLayout.deleteRow(1);
		assertTrue("buttonGridLayout.getSize().x != 2", buttonGridLayout.getSize().x == 2);
		assertTrue("buttonGridLayout.getSize().y != 2", buttonGridLayout.getSize().y == 2);
	}

	@Test
	public void testRemoveColumn() {
		buttonGridLayout.addRow();
		buttonGridLayout.addColumn();
		buttonGridLayout.addRow();
		buttonGridLayout.addRow();
		buttonGridLayout.addColumn();
		buttonGridLayout.deleteColumn(1);
		assertTrue("buttonGridLayout.getSize().x != 2", buttonGridLayout.getSize().x == 2);
		assertTrue("buttonGridLayout.getSize().y != 3", buttonGridLayout.getSize().y == 3);
	}
}
