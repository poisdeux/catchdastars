package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.scenes.scene2d.ui.EventHandler.ActorListener;
import com.strategames.engine.utils.Textures;

public class MenuButton extends com.badlogic.gdx.scenes.scene2d.ui.Button {
	private float IMAGEWIDTH = GameEngine.convertWorldToScreen(0.30f);
	private static Drawable drawable = new TextureRegionDrawable(Textures.getInstance().menu);
	private EventHandler eventHandler = new EventHandler(this);

	public MenuButton() {
		super(drawable);
		setSize(IMAGEWIDTH, IMAGEWIDTH);
		addListener(this.eventHandler);
	}

	public void setListener(ActorListener listener) {
		this.eventHandler.setListener(listener);
	}
}
