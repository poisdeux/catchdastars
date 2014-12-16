package com.strategames.engine.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

/**
 * TODO Make GridLayout work in a ScrollPane. Use Table as an example.
 * see http://nexsoftware.net/wp/2013/05/09/libgdx-making-a-paged-level-selection-screen/
 * @author martijn
 *
 */
public class GridLayout extends WidgetGroup {

	private boolean sizeInvalid;

	private Vector2 elementSize = new Vector2(10, 10);
	//	private Vector2 offset = new Vector2();

	private Array<Holder> elements = new Array<Holder>();

	public GridLayout() {
		super();
		setTransform(false);
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

	
	
	public void setElementSize(float width, float height) {
		this.elementSize = new Vector2(width, height);
	}

	public Vector2 getElementSize() {
		return elementSize;
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
			sizeInvalid = true;
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
				sizeInvalid = true;
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
		return getWidth();
	}

	@Override
	public float getPrefHeight () {
		if (sizeInvalid) computeSize();
		return getHeight();
	}

	@Override
	public float getMinWidth () {
		if (sizeInvalid) computeSize();
		return getWidth();
	}

	@Override
	public float getMinHeight () {
		if (sizeInvalid) computeSize();
		return getHeight();
	}

	@Override
	public void setSize(float width, float height) {
		Gdx.app.log("GridLayout", "setSize: width="+width+", height="+height);
		super.setSize(width, height);
	}
	
//	@Override
//	public void setPosition(float x, float y) {
//		Gdx.app.log("GridLayout", "setPosition: x="+x+", y="+y);
//		super.setPosition(x, y);
//	}
//	
//	@Override
//	public void setX(float x) {
//		Gdx.app.log("GridLayout", "setX: x="+x);
//		super.setX(x);
//	}
//	
//	@Override
//	public void setY(float y) {
//		Gdx.app.log("GridLayout", "setY: y="+y);
//		super.setY(y);
//	}
	
	@Override
	public void layout() {
		Gdx.app.log("GridLayout", "layout: ");
		
		//Center in middle of layout
		float offsetX = getWidth() / 2f;
		float offsetY = getHeight() / 2f;
		
		for( Holder element : this.elements ) {
			Actor actor = element.getActor();

			float xActor = element.x * this.elementSize.x;
			float yActor = element.y * this.elementSize.y;

			actor.setPosition(xActor + offsetX, yActor + offsetY);
			
			Gdx.app.log("GridLayout", "layout(): actor="+actor+", pos=("+actor.getX()+", "+actor.getY()+")");
		}
	}
	
	private void computeSize() {
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

		float width = (maxX - minX) * elementSize.x;
		float height = (maxY - minY) * elementSize.y;
		setSize(width, height);

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
