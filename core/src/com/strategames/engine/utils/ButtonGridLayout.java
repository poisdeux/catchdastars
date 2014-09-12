package com.strategames.engine.utils;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

public class ButtonGridLayout extends Actor {

	private Array<Array<TextButton>> rows;
	private Skin skin;
	private float paddingRight = 10f;
	private float paddingBottom = 10f;
	private int buttonNumber = 0;
	private Vector2 buttonSize;
	
	public ButtonGridLayout(Skin skin) {
		rows = new Array<Array<TextButton>>();
		this.skin = skin;
		
		TextButton button = new TextButton("1234", skin);
		buttonSize = new Vector2(button.getWidth(), button.getHeight());
	}

	/**
	 * Adds a new row to the grid. If this is the
	 * first row added a column will be added as well.
	 */
	public void addRow() {
		int columns = 1;
		if( rows.size > 0 ) { 
			Array<TextButton> column = rows.get(0);
			columns = column.size;
		}
		
		Array<TextButton> row = new Array<TextButton>();

		float y = ( buttonSize.y + paddingBottom ) * rows.size;
		for(int i = 0; i < columns; i++) {
			float x = ( buttonSize.x + paddingRight ) * i;
			TextButton button = createButton();
			button.setPosition(x, y);
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
			rows.add(new Array<TextButton>());
		}

		Array<TextButton> column = rows.get(0);
		float x = ( buttonSize.x + paddingRight ) * column.size;
		
		for(int i = 0; i < rows.size; i++) {
			float y = ( buttonSize.y + paddingBottom ) * i;
			TextButton button = createButton();
			button.setPosition(x, y);
			rows.get(i).add(button);
		}
	}

	/**
	 * Removes and returns a row from the grid 
	 * @param row index of row to remove
	 * @return Array<Button>
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public Array<TextButton> deleteRow(int row) throws ArrayIndexOutOfBoundsException {
		if( ( row >= 0 ) && ( row < rows.size ) ) {
			Array<TextButton> buttons = rows.removeIndex(row);
			updatePositions();
			return buttons;
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
	public Array<TextButton> deleteColumn(int column) throws ArrayIndexOutOfBoundsException {
		Array<TextButton> buttons = new Array<TextButton>();
		for(int i = 0; i < rows.size; i++) {
			Array<TextButton> row = rows.get(i);
			if( column < row.size ) { 
				buttons.add(row.removeIndex(column));
			} else {
				throw new ArrayIndexOutOfBoundsException("Row("+i+").size="+row.size+" < column="+column);
			}
		}
		updatePositions();
		return buttons;
	}
	
	public Array<TextButton> getRow(int row) throws ArrayIndexOutOfBoundsException {	
		if( ( row >= 0 ) && ( row < rows.size ) ) {
			return rows.get(row);
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	public Array<TextButton> getColumn(int column) throws ArrayIndexOutOfBoundsException {
		Array<TextButton> buttons = new Array<TextButton>();
		for(int i = 0; i < rows.size; i++) {
			Array<TextButton> row = rows.get(i);
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
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		for(int i = 0; i < rows.size; i++) {
			Array<TextButton> row = rows.get(i);
			for(int j = 0; j < row.size; j++) {
				TextButton button = row.get(j);
				button.draw(batch, parentAlpha);
			}
		}
	}	
	
	private void updatePositions() {
		float yDelta = buttonSize.y + paddingBottom;
		float xDelta = buttonSize.x + paddingRight;
		for(int i = 0; i < rows.size; i++) {
			float y = yDelta * i;
			Array<TextButton> row = rows.get(i);
			for(int j = 0; j < row.size; j++) {
				float x = xDelta * j;
				TextButton button = row.get(j);
				button.setPosition(x, y);
			}
		}
	}
	
	private TextButton createButton() {
		TextButton button = new TextButton(String.valueOf(buttonNumber++), skin);
		return button;
	}
}
