package com.strategames.ui.widgets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.strategames.engine.utils.GridLayout.GridElement;
import com.strategames.ui.interfaces.ActorListener;

/**
 * TextButton that also reacts to longpresses
 * @author mbrekhof
 *
 */
public class ScreenshotImage extends Image implements EventListener, GridElement<ScreenshotImage> {
	private Timer timer;
	private boolean longPress;
	private ActorListener listener = null;
	private Object tag;

	public ScreenshotImage(Drawable drawable) {
		super(drawable);
		this.timer = new Timer();

		addListener(this);
	}

	public ScreenshotImage(Texture texture) {
		super(texture);
		this.timer = new Timer();

		addListener(this);
	}

	@Override
	public boolean handle(Event e) {

		if (!(e instanceof InputEvent)) return false;
		InputEvent event = (InputEvent)e;

		switch (event.getType()) {
		case touchDown:
			return touchDown(event);
		case touchUp:
			touchUp(event);
			return true;
		default:
			return false;
		}
	}

	public void setListener(ActorListener listener) {
		this.listener = listener;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public Object getTag() {
		return this.tag;
	}

	private boolean touchDown(InputEvent event) {
		this.longPress = false;
		this.timer.scheduleTask(new Task() {

			@Override
			public void run() {
				longPress = true;
				handleLongPress();
			}
		}, 1);
		return true;
	}

	private boolean touchUp(InputEvent event) {
		this.timer.clear();
		if( ! longPress ) {
			handleTap();
		}
		return true;
	}

	private void handleTap() {
		if( this.listener != null )
			this.listener.onTap(this);
	}

	private void handleLongPress() {
		if( this.listener != null )
			this.listener.onLongPress(this);
	}

	@Override
	public ScreenshotImage newInstance() {
		return new ScreenshotImage(getDrawable());
	}
}
