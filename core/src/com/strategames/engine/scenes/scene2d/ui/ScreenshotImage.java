package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;

public class ScreenshotImage extends Image {
	private EventHandler eventHandler = new EventHandler(this);
	private Object tag;

	public ScreenshotImage(Drawable drawable) {
		super(drawable);
		addListener(this.eventHandler);
	}

	public ScreenshotImage(Texture texture) {
		super(texture);
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
