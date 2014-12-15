package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Test class to determine why Table is correctly positioned in a ScrollPan
 * and GridLayout is not
 * @author martijn
 *
 */
public class Table extends com.badlogic.gdx.scenes.scene2d.ui.Table implements EventListener {

//		@Override
//		public void setPosition(float x, float y) {
//			Gdx.app.log("Table", "setPosition: x="+x+", y="+y);
//			super.setPosition(x, y);
//		}

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

	@Override
	public boolean handle(Event e) {
		if (!(e instanceof InputEvent)) return false;
		InputEvent event = (InputEvent)e;
		Actor actor;
//		Gdx.app.log("GridLayout", "handle: event="+event.getType().name());
		switch (event.getType()) {
		case touchDown:
			actor = hit(event.getStageX(), event.getStageY(), false);
			if( actor == null ) {
				return false;
			}

			if( actor instanceof Label ) {
				Label label = (Label) actor;
				Gdx.app.log("Table", "touchDown: label="+label.getText());
			}
			return true;
		case touchUp:
			actor = hit(event.getStageX(), event.getStageY(), false);
			if( actor == null ) {
				return false;
			}

			if( actor instanceof Label ) {
				Label label = (Label) actor;
				Gdx.app.log("Table", "touchDown: label="+label.getText());
			}
			return true;
		default:
			return false;
		}
	}
	
	
}
