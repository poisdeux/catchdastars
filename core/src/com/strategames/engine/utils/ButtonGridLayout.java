package com.strategames.engine.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;

public class ButtonGridLayout {

	private Array<Array<Button>> rows;

	public ButtonGridLayout() {
		rows = new Array<Array<Button>>();
	}

	/**
	 * Adds a new row to the grid. If this is the
	 * first row added a column will be added as well.
	 */
	public void addRow() {
		int columns = 1;
		if( rows.size > 0 ) { 
			Array<Button> column = rows.get(0);
			columns = column.size;
		}
		
		Array<Button> row = new Array<Button>();

		for(int i = 0; i < columns; i++) {
			Button button = new Button();
			row.add(button);
		}

		rows.add(row);
	}

	/**
	 * Adds a new column to the grid. If this is the
	 * first column and now rows have been added yet a
	 * row will be added as well.
	 */
	public void addColumn() {
		if( rows.size == 0 ) {
			rows.add(new Array<Button>());
		}

		for(int i = 0; i < rows.size; i++) {
			Button button = new Button();
			rows.get(i).add(button);
		}
	}

	/**
	 * Removes and returns a row from the grid 
	 * @param row index of row to remove
	 * @return Array<Button>
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public Array<Button> deleteRow(int row) throws ArrayIndexOutOfBoundsException {
		if( ( row >= 0 ) && ( row < rows.size ) ) {
			return rows.removeIndex(row);
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	/**
	 * Removes and returns a column from the grid
	 * @param column index of column to remove
	 * @return Array<Button>
	 * @throws ArrayIndexOutOfBoundsException 
	 */
	public Array<Button> deleteColumn(int column) throws ArrayIndexOutOfBoundsException {
		Array<Button> buttons = new Array<Button>();
		for(int i = 0; i < rows.size; i++) {
			Array<Button> row = rows.get(i);
			if( column < row.size ) { 
				buttons.add(row.removeIndex(column));
			} else {
				throw new ArrayIndexOutOfBoundsException("Row("+i+").size="+row.size+" < column="+column);
			}
		}
		return buttons;
	}
	
	public Array<Button> getRow(int row) throws ArrayIndexOutOfBoundsException {	
		if( ( row >= 0 ) && ( row < rows.size ) ) {
			return rows.get(row);
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	public Array<Button> getColumn(int column) throws ArrayIndexOutOfBoundsException {
		Array<Button> buttons = new Array<Button>();
		for(int i = 0; i < rows.size; i++) {
			Array<Button> row = rows.get(i);
			if( column < row.size ) { 
				buttons.add(row.get(column));
			} else {
				throw new ArrayIndexOutOfBoundsException("Row("+i+").size="+row.size+" < column="+column);
			}
		}
		return buttons;
	}

	/**
	 * Returns the amount of rows and columns
	 * @return Vector2 where x is the amount of columns, y the amount of rows.
	 */
	public Vector2 getSize() {
		if( rows.size == 0 ) {
			return new Vector2(0,0);
		}
		return new Vector2(rows.get(0).size, rows.size);
	}
}
