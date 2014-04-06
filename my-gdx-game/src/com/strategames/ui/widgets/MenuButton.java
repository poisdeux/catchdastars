package com.strategames.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.catchdastars.Game;
import com.strategames.catchdastars.utils.Textures;
import com.strategames.interfaces.ButtonListener;

public class MenuButton extends com.badlogic.gdx.scenes.scene2d.ui.Button implements EventListener {
	private ButtonListener listener = null;
	private float IMAGEWIDTH = Game.convertBoxToWorld(0.30f);
	private static Drawable drawable = new TextureRegionDrawable(Textures.menu);
	
	public MenuButton() {
		super(drawable);
		setScale(IMAGEWIDTH / getMinWidth());
		addListener(this);
	}

	@Override
	public boolean handle(Event e) {
		
		if (!(e instanceof InputEvent)) return false;
		InputEvent event = (InputEvent)e;

		switch (event.getType()) {
		case touchDown:
			return true;
		case touchUp:
			this.listener.onTap(this);
			return true;
		default:
			return false;
		}
	}

	public void setListener(ButtonListener listener) {
			this.listener = listener;
	}
}
