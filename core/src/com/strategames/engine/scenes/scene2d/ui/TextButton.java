package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;

public class TextButton extends com.badlogic.gdx.scenes.scene2d.ui.TextButton {
	
	private EventHandler eventHandler = new EventHandler(this);
	private Object tag;
	
	public TextButton(String text, Skin skin) {
		super(text, skin);
		addListener(this.eventHandler);
	}

	
	public void setListener(ActorListener listener) {
			this.eventHandler.setListener(listener);
	}
	
	public void setTag(Object tag) {
		this.tag = tag;
	}
	
	public Object getTag() {
		return this.tag;
	}
}
