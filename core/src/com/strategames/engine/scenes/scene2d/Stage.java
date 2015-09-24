/**
 * 
 * Copyright 2014 Martijn Brekhof
 *
 * This file is part of Catch Da Stars.
 *
 * Catch Da Stars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Catch Da Stars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Catch Da Stars.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.strategames.engine.scenes.scene2d;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Stage extends com.badlogic.gdx.scenes.scene2d.Stage {

	public Stage(FitViewport fitViewport) {
		super(fitViewport);
	}

	public Array<Actor> getActorsAt(float x, float y) {
		Array<Actor> actors = getActors();
		Array<Actor> actorsHit = new Array<Actor>();
		Vector2 point = new Vector2();
		for(int i = 0; i < actors.size; i++) {
			Actor actor = actors.get(i);
			actor.parentToLocalCoordinates(point.set(x, y));
			if( actor.hit(point.x, point.y, false) != null ) {
				actorsHit.add(actor);
			}
		}
		return actorsHit;
	}
	
	public Array<Actor> getActorsInRectangle(Rectangle rectangle) {
		Array<Actor> actors = getActors();
		Array<Actor> actorsInRectangle = new Array<Actor>();
		for(int i = 0; i < actors.size; i++) {
			Actor actor = actors.get(i);
			Rectangle rectangleActor = new Rectangle(actor.getX(), actor.getY(), 
					actor.getWidth(), actor.getHeight());
			if( rectangle.overlaps(rectangleActor) ) {
				actorsInRectangle.add(actor);
			}
		}
		return actorsInRectangle;
	}
	
	public Array<Actor> getActorsOverlapping(Actor actor) {
		Rectangle rectangle = new Rectangle(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
		return getActorsInRectangle(rectangle);
	}
}
