package com.strategames.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.strategames.engine.game.Game;
import com.strategames.engine.utils.Textures;
import com.strategames.ui.interfaces.ButtonListener;

public class MenuButton extends com.badlogic.gdx.scenes.scene2d.ui.Button implements EventListener {
	private ButtonListener listener = null;
	private float IMAGEWIDTH = Game.convertWorldToScreen(0.30f);
	private static Drawable drawable = new TextureRegionDrawable(Textures.getInstance().menu);
	
	public MenuButton() {
		super(drawable);
		setSize(IMAGEWIDTH, IMAGEWIDTH);
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
