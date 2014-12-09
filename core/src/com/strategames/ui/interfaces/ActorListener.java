package com.strategames.ui.interfaces;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface ActorListener {
	/**
	 * Called when actor is clicked
	 * @param actor
	 */
	public void onTap(Actor actor);
	/**
	 * Called when actor is long pressed
	 * @param actor
	 */
	public void onLongPress(Actor actor);
	/**
	 * Use this to set the listener that will be called
	 * when object is tapped or long pressed. This is useful
	 * for chaining purposes to propagate the event to other
	 * objects as well.
	 * @param listener
	 */
	public void setListener(ActorListener listener);
}
