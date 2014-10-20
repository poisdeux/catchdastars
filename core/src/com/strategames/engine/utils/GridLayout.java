package com.strategames.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

public class GridLayout extends WidgetGroup {

	private Vector2 elementSize = new Vector2(10, 10);
	private Vector2 offset = new Vector2();
	
	public interface GridElement<T> {
		public T newInstance();
	}

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
	public void set(int x, int y, Actor actor) {
		
		Holder element = getHolderAt(x, y);
		
		if( element == null ) {
			element = new Holder(actor, x, y);
			this.elements.add(element);
		} else {
			element.getActor().remove();
			element.setActor(actor);
		}
		
		float xActor = element.x * this.elementSize.x;
		float yActor = element.y * this.elementSize.y;
		if( actor != null ) {
			actor.setPosition(xActor + offset.x, yActor + offset.y);
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
