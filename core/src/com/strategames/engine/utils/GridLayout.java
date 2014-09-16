package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

public class GridLayout extends WidgetGroup {
	
	private Vector2 elementSize = new Vector2(10, 10);
	
	public interface GridElement<T> {
		public T newInstance();
	}
	
	private Array<Array<Actor>> rows = new Array<Array<Actor>>();

	public void setGrid(Array<Array<Actor>> elements) {
		this.rows = elements;
	}
	
	public Array<Array<Actor>> getElements() {
		return rows;
	}
	
	public void setElementSize(float width, float height) {
		this.elementSize = new Vector2(width, height);
	}
	
	public Vector2 getElementSize() {
		return elementSize;
	}
	
	public void deleteRow(int row) {
		this.rows.removeIndex(row);
	}
	
	public void deleteColumn(int column) {
		for(int i = 0; i < rows.size; i++) {
			Array<Actor> row = rows.get(i);
			row.removeIndex(column);
		}
	}
	
	/**
	 * Adds a row to the grid. If the grid is empty
	 * a row and a column will be created.
	 */
	public void addRow() {
		int columns = 1;
		if( rows.size > 0 ) { 
			Array<Actor> column = rows.get(0);
			columns = column.size;
		}
		
		Array<Actor> row = new Array<Actor>();
		for(int i = 0; i < columns; i++) {
			row.add(null);
		}
		
		rows.add(row);
	}
	
	/**
	 * Adds a column to the grid. If the grid is empty
	 * a row and a column will be created.
	 */
	public void addColumn() {
		if( rows.size == 0 ) {
			rows.add(new Array<Actor>());
		}

		for(int i = 0; i < rows.size; i++) {
			Array<Actor> row = rows.get(i);
			row.add(null);
		}
	}
	
	/**
	 * Sets element at given index. If grid does not contain the
	 * given index yet the grid size is increased to include the index
	 * @param columnIndex column number (starting at 0)
	 * @param rowIndex row number (starting at 0)
	 * @param actor element to set at position rowIndex, columnIndex
	 */
	public void set(int columnIndex, int rowIndex, Actor actor) {
		if( rowIndex >= rows.size) {
			for( int i = rows.size - 1; i < rowIndex; i++ ) {
				addRow();
			}
		}
		
		Array<Actor> row = rows.get(rowIndex);
		if( columnIndex >= row.size ) {
			for( int i = row.size - 1; i < columnIndex; i++ ) {
				addColumn();
			}
		}
		
		Actor curActor = row.get(columnIndex);
		
		//Make sure any existing actor is removed from the group
		if( curActor != null ) {
			row.get(columnIndex).remove();
		}
		
		row.set(columnIndex, actor);
		addActor(actor);
	}
	
	/**
	 * Returns the element at the given grid position
	 * @param columnIndex
	 * @param rowIndex
	 * @return Actor or null if not found
	 */
	public Actor get(int columnIndex, int rowIndex) {
		if( rowIndex < rows.size) {
			Array<Actor> row = rows.get(rowIndex);
			if( columnIndex < row.size ) {
				return row.get(columnIndex);
			}
		}
		return null;
	}
	
	/**
	 * Returns grid position of given element
	 * @param actor
	 * @return position (int[0] is columnIndex and int[1] is rowIndex) or null if not found
	 */
	public int[] getPosition(Actor actor) {
		for(int i = 0; i < rows.size; i++) {
			Array<Actor> row = rows.get(i);
			for(int j = 0; j < row.size; j++) {
				if( row.get(j) == actor ) {
					int[] position = new int[2];
					position[0] = j;
					position[1] = i;
					return position;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the amount of columns and rows of the grid
	 * @return int[0] = columns, int[1] = rows.
	 */
	public int[] getSize() {
		int[] size = new int[2];
		if( rows.size == 0 ) {
			size[0] = 0;
			size[1] = 0;
		} else {
			size[0] = rows.get(0).size;
			size[1] = rows.size;
		}
		return size;
	}
	
	public Array<Actor> getRow(int row) throws ArrayIndexOutOfBoundsException {	
		if( ( row >= 0 ) && ( row < rows.size ) ) {
			return rows.get(row);
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	public Array<Actor> getColumn(int column) throws ArrayIndexOutOfBoundsException {
		Array<Actor> elements = new Array<Actor>();
		for(int i = 0; i < rows.size; i++) {
			Array<Actor> row = rows.get(i);
			if( column < row.size ) { 
				elements.add(row.get(column));
			} else {
				throw new ArrayIndexOutOfBoundsException("Row("+i+").size="+row.size+" < column="+column);
			}
		}
		return elements;
	}
	
	public void layout() {
		setElementPositions();
	}
	
	private void setElementPositions() {
		for(int i = 0; i < rows.size; i++) {
			float y = this.elementSize.y * i;
			Array<Actor> row = rows.get(i);
			for(int j = 0; j < row.size; j++) {
				float x = this.elementSize.x * j;
				Actor actor = row.get(j);
				if( actor != null ) {
					actor.setPosition(x, y);
				}
			}
		}
	}
}
