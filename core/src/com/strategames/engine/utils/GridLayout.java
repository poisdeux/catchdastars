package com.strategames.engine.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.strategames.ui.interfaces.ActorListener;

public class GridLayout extends WidgetGroup {

	/**
	 * Interface used by GridLayout to notify listeners
	 * for click events on grid items
	 * @author martijn
	 *
	 */
	public interface OnItemClickedListener {
		/**
		 * Called when item is tapped
		 * @param x position in gridlayout
		 * @param y position in gridlayout
		 * @param actor tapped
		 */
		public void onTap(int x, int y, Actor actor);

		/**
		 * Called when item is long pressed
		 * <br/>
		 * Note that onLongPress is only called on actors that
		 * implement the {@link ActorListener} interface
		 * @param x position in gridlayout
		 * @param y position in gridlayout
		 * @param actor tapped
		 */
		public void onLongPress(int x, int y, Actor actor);
	}

	private OnItemClickedListener listener;

	private Vector2 elementSize = new Vector2(10, 10);
	private Vector2 offset = new Vector2();

	private Array<Holder> elements = new Array<Holder>();

	@Override
	public void clear() {
		elements.clear();
		super.clear();
	}

	public Array<Actor> getElements() {
		Array<Actor> actors = new Array<Actor>();
		for(Holder holder : elements) {
			actors.add(holder.getActor());
		}
		return actors;
	}

	public void setElementSize(float width, float height) {
		this.elementSize = new Vector2(width, height);
	}

	public Vector2 getElementSize() {
		return elementSize;
	}

	public void setListener(OnItemClickedListener listener) {
		this.listener = listener;
	}

	public void setOffset(Vector2 offset) {
		this.offset = offset;
	}

	public Vector2 getOffset() {
		return offset;
	}

	/**
	 * Sets element at given index.
	 * @param x 
	 * @param y 
	 * @param actor element to set at position x, y
	 */
	public void set(final int x, final int y, final Actor actor) {

		Holder element = getHolderAt(x, y);

		if( element == null ) {
			element = new Holder(actor, x, y);
			this.elements.add(element);
		} else {
			element.getActor().remove();
			element.setActor(actor);
		}

		if( actor != null ) {
			setupActor(element);
			addActor(actor);
		}
	}


	/**
	 * Removes actor at given position
	 * @param x
	 * @param y
	 * @return removed actor or null if failed
	 */
	public Actor remove(int x, int y) {
		for(Holder element : this.elements) {
			if( ( element.getX() == x ) && ( element.getY() == y ) ) {
				Actor actor = element.getActor();
				actor.remove();
				return actor; 
			}
		}
		return null;
	}

	/**
	 * Returns the element at the given grid position
	 * @param x
	 * @param y
	 * @return Actor or null if not found
	 */
	public Actor get(int x, int y) {
		for(Holder element : this.elements) {
			if( ( element.getX() == x ) && ( element.getY() == y ) ) {
				Actor actor = element.getActor();
				return actor; 
			}
		}
		return null;
	}

	/**
	 * Returns grid position of given element
	 * @param actor
	 * @return position (int[0] is x and int[1] is y) or null if not found
	 */
	public int[] getPosition(Actor actor) {
		for(Holder element : this.elements) {
			if( ( element.getActor() == actor ) ) {
				return new int[] {element.getX(), element.getY()}; 
			}
		}
		return null;
	}

	private void setupActor(final Holder element) {
		final Actor actor = element.getActor();

		float xActor = element.x * this.elementSize.x;
		float yActor = element.y * this.elementSize.y;

		actor.setPosition(xActor, yActor);

		if( actor instanceof ActorListener ) {
			ActorListener actorListener = (ActorListener) actor;
			actorListener.setListener(new ActorListener() {

				@Override
				public void onTap(Actor actor) {
					GridLayout.this.listener.onTap(element.x, element.y, actor);
				}

				@Override
				public void onLongPress(Actor actor) {
					GridLayout.this.listener.onLongPress(element.x, element.y, actor);
				}

				@Override
				public void setListener(ActorListener listener) {
				}
			});
		} else {
			actor.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float xc, float yc) {
					if( listener != null ) {
						listener.onTap(element.x, element.y, actor);
					}
				}
			});
		}
	}

	private Holder getHolderAt(int x, int y) {
		Holder holder = null;

		for(Holder element : this.elements) {
			if( ( element.getX() == x ) && ( element.getY() == y ) ) {
				holder = element;
			}
		}
		return holder;
	}

	private class Holder {
		private int x;
		private int y;
		private Actor actor;

		public Holder(Actor actor, int x, int y) {
			this.actor = actor;
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public Actor getActor() {
			return actor;
		}

		public void setActor(Actor actor) {
			this.actor = actor;
		}
	}
}
