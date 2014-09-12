package com.strategames.engine.utils;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class GridLayout extends Actor {
	
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
		updateElementPositions();
	}
	
	public Vector2 getElementSize() {
		return elementSize;
	}
	
	public void deleteRow(int row) {
		this.rows.removeIndex(row);
		updateElementPositions();
	}
	
	public void deleteColumn(int column) {
		for(int i = 0; i < rows.size; i++) {
			Array<Actor> row = rows.get(i);
			row.removeIndex(column);
		}
		updateElementPositions();
	}
	
	public void addRow() {
		int columns = 1;
		if( rows.size > 0 ) { 
			Array<Actor> column = rows.get(0);
			columns = column.size;
		}
		
		Array<Actor> row = new Array<Actor>();
		row.ensureCapacity(columns);
		
		rows.add(row);
	}
	
	public void addColumn() {
		if( rows.size == 0 ) {
			rows.add(new Array<Actor>());
		}

		for(int i = 0; i < rows.size; i++) {
			Array<Actor> row = rows.get(i);
			row.ensureCapacity(1);
		}
	}
	
	public void set(int rowIndex, int columnIndex, Actor actor) throws ArrayIndexOutOfBoundsException {
		if( rowIndex < rows.size) {
			Array<Actor> row = rows.get(rowIndex);
			if( columnIndex < row.size ) {
				row.set(columnIndex, actor);
			}
		}
		throw new ArrayIndexOutOfBoundsException();
	}
	
	/**
	 * Returns the element at the given grid position
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public Actor get(int rowIndex, int columnIndex) throws ArrayIndexOutOfBoundsException {
		if( rowIndex < rows.size) {
			Array<Actor> row = rows.get(rowIndex);
			if( columnIndex < row.size ) {
				return row.get(columnIndex);
			}
		}
		throw new ArrayIndexOutOfBoundsException();
	}
	
	/**
	 * Returns grid position of given element
	 * @param actor
	 * @return position or null if not found
	 */
	public int[] getPosition(Actor actor) {
		for(int i = 0; i < rows.size; i++) {
			Array<Actor> row = rows.get(i);
			for(int j = 0; j < row.size; j++) {
				if( row.get(j) == actor ) {
					int[] position = new int[2];
					position[0] = i;
					position[1] = j;
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
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		for(int i = 0; i < rows.size; i++) {
			Array<Actor> row = rows.get(i);
			for(int j = 0; j < row.size; j++) {
				row.get(j).draw(batch, parentAlpha);
			}
		}
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
	
	private void updateElementPositions() {
		for(int i = 0; i < rows.size; i++) {
			float y = this.elementSize.y * i;
			Array<Actor> row = rows.get(i);
			for(int j = 0; j < row.size; j++) {
				float x = this.elementSize.x * j;
				row.get(j).setPosition(x, y);
			}
		}
	}
}
