package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class EventHandler implements EventListener {
	private Timer timer;
	private boolean longPress;
	private boolean drag;
	private ActorListener listener = null;
	private Actor actor = null;
	
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
	}
	
	public EventHandler(Actor actor) {
		this.timer = new Timer();
		this.actor = actor;
	}
	
	public void setListener(ActorListener listener) {
		this.listener = listener;
	}
	
	@Override
	public boolean handle(Event e) {
		
		if (!(e instanceof InputEvent)) return false;
		InputEvent event = (InputEvent)e;

		switch (event.getType()) {
		case touchDown:
			touchDown(event);
			return true;
		case touchUp:
			touchUp(event);
			return true;
		case touchDragged:
			touchDragged(event);
			return true;
		default:
			return false;
		}
	}

	private void touchDown(InputEvent event) {
		this.longPress = false;
		this.timer.scheduleTask(new Task() {
			
			@Override
			public void run() {
				longPress = true;
				handleLongPress();
			}
		}, 1);
	}
	
	private void touchUp(InputEvent event) {
		this.timer.clear();
		if( ( ! longPress ) && ( ! drag ) ) {
			handleTap();
		}
		drag = false;
	}
	
	private void touchDragged(InputEvent event) {
		this.timer.clear();
		this.drag = true;
	}
	
	private void handleTap() {
		if( this.listener != null )
			this.listener.onTap(this.actor);
	}
	
	private void handleLongPress() {
		if( this.listener != null )
			this.listener.onLongPress(this.actor);
	}

}
