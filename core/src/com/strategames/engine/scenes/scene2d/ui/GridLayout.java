package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

public class GridLayout extends WidgetGroup {
	private boolean center;

	private boolean sizeInvalid = true;
	private float gridPrefWidth, gridPrefHeight;

	private Vector2 elementSize = new Vector2(10, 10);
	private Vector2 offset = new Vector2(0,0);

	private Array<Holder> elements = new Array<Holder>();

	public GridLayout() {
		super();
		setTransform(false);
		setTouchable(Touchable.childrenOnly);
	}

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

	/**
	 * Will position the element at 0,0 at the center of
	 * the parent
	 * @param center true to center in parent
	 */
	public void setCenter(boolean center) {
		this.center = center;
	}

	public boolean isCenter() {
		return center;
	}

	public void setElementSize(float width, float height) {
		this.elementSize = new Vector2(width, height);
	}

	public Vector2 getElementSize() {
		return elementSize;
	}

	@Override
	public void invalidate() {
		this.sizeInvalid = true;
		super.invalidate();
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
			invalidate();
		} else {
			element.getActor().remove();
			element.setActor(actor);
		}

		if( actor != null ) {
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
				invalidate();
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

	@Override
	public float getPrefWidth () {
		if (sizeInvalid) computeSize();
		return gridPrefWidth;
	}

	@Override
	public float getPrefHeight () {
		if (sizeInvalid) computeSize();
		return gridPrefHeight;
	}

	@Override
	public float getMinWidth () {
		return this.elementSize.x;
	}

	@Override
	public float getMinHeight () {
		return this.elementSize.y;
	}

	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x - getOriginX(), y - getOriginY());
	}


	@Override
	public void setX(float x) {
		super.setX(x - getOriginX());
	}

	@Override
	public void setY(float y) {
		super.setY(y - getOriginY());
	}

	@Override
	public void layout() {
		for( Holder element : this.elements ) {
			Actor actor = element.getActor();

			float xActor = (element.x * this.elementSize.x) + this.offset.x;
			float yActor = (element.y * this.elementSize.y) + this.offset.y;

			actor.setPosition(xActor, yActor);

		}
	}

	private void computeSize() {
		offset.x = 0;
		offset.y = 0;
		
		if( center ) {
			Group parent = getParent();
			if( parent != null ) {
				offset.x = parent.getWidth() / 2f;
				offset.y = parent.getHeight() / 2f;
			}
		}

		int minX = 0;
		int maxX = 0;
		int minY = 0;
		int maxY = 0;
		for( Holder element : elements ) {
			if( element.x < minX ) {
				minX = element.x;
			} 
			if( element.x > maxX ) {
				maxX = element.x;
			}
			if( element.y < minY ) {
				minY = element.y;
			} 
			if( element.y > maxY ) {
				maxY = element.y;
			}
		}

		this.gridPrefWidth = offset.x + (((maxX - minX) + 1) * elementSize.x);
		this.gridPrefHeight = offset.y + (((maxY - minY) + 1) * elementSize.y);

		sizeInvalid = false;
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
