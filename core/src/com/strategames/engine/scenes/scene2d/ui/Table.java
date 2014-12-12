package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;

/**
 * Test class to determine why Table is correctly positioned in a ScrollPan
 * and GridLayout is not
 * @author martijn
 *
 */
public class Table extends com.badlogic.gdx.scenes.scene2d.ui.Table {

		@Override
		public void setPosition(float x, float y) {
			Gdx.app.log("Table", "setPosition: x="+x+", y="+y);
			super.setPosition(x, y);
		}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		Gdx.app.log("Table", "setBounds: x="+x+", y="+y+", width="+width+", height="+height);
		super.setBounds(x, y, width, height);
	}

	@Override
	public void setSize(float width, float height) {
		Gdx.app.log("Table", "setSize: height="+height+", width="+width);
		super.setSize(width, height);
	}
	@Override
	public void setHeight(float height) {
		Gdx.app.log("Table", "setHeight: height="+height);
		super.setHeight(height);
	}

	@Override
	public void setWidth(float width) {
		Gdx.app.log("Table", "setWidth: width="+width);
		super.setWidth(width);
	}
}
