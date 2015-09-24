/**
 * 
 * Copyright 2013 Martijn Brekhof
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

package com.strategames.engine.gameobject.types;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.engine.game.GameEngine;
import com.strategames.engine.utils.Textures;

public class ChalkLine extends Image {
	private static Sprite[] chalks;
	private ArrayList<Sprite> chalkLine; 
	private float length;
	private float lengthPerStep;
	private int steps;
	private int stepSize = 1;
	private int duration;
	private Random randomNumberGenerator;
	private Vector2 start;
	private Vector2 end;
	private Vector2 increments;
	private Textures textures = Textures.getInstance();
	
	public interface ChalkLineAnimationListener {
		public void onLineDrawEnd(ChalkLine line);
	}

	private ChalkLineAnimationListener listener;

	public ChalkLine() {
		super();

		this.start = new Vector2();
		this.end = new Vector2();

		synchronized (this) {
			if( chalks == null ) {
				chalks = new Sprite[5];
				chalks[0] = new Sprite(textures.chalk1);
				chalks[1] = new Sprite(textures.chalk2);
				chalks[2] = new Sprite(textures.chalk3);
				chalks[3] = new Sprite(textures.chalk4);
				chalks[4] = new Sprite(textures.chalk5);

				for( int i = 0; i < chalks.length; i++ ) {
					chalks[i].setScale(0.5f);
				}
			}
		}

		this.randomNumberGenerator = new Random();

		this.chalkLine = new ArrayList<Sprite>();

		setScaling(Scaling.stretch);

		this.listener = null;
	}

	public ChalkLine(float xStart, float yStart, float xEnd, float yEnd, int milliseconds, ChalkLineAnimationListener listener) {
		this();
		setStart(xStart, yStart);
		setEnd(xEnd, yEnd);
		setDuration(milliseconds);
		setListener(listener);
		init();
	}

	//	public static ChalkLine create(float xStart, float yStart, float xEnd, float yEnd, int milliseconds, ChalkLineAnimationListener listener) {
	//		ChalkLine line = new ChalkLine();
	//		line.setStart(xStart, yStart);
	//		line.setEnd(xEnd, yEnd);
	//		line.setDuration(milliseconds);
	//		line.setListener(listener);
	//		line.init();
	//		return line;
	//	}

	public void setListener(ChalkLineAnimationListener listener) {
		this.listener = listener;
	}

	/**
	 * Sets the start position of this line
	 * @param x
	 * @param y
	 */
	public void setStart(float x, float y) {
		this.start.x = x;
		this.start.y = y;
	}

	public Vector2 getStart() {
		return start;
	}

	public void setEnd(float x, float y) {
		this.end.x = x;
		this.end.y = y;
	}

	public Vector2 getEnd() {
		return end;
	}

	public void setDuration(int milliseconds) {
		this.duration = milliseconds;
	}

	public float getLength() {
		return length;
	}

	public void init() {
		this.length = this.start.dst(this.end);

		this.steps = (int) (this.duration / (GameEngine.FRAMES_PER_SECOND * 1000));

		if( this.steps < 1 ) { 
			this.steps = 1;
		}

		this.lengthPerStep = this.length / this.steps;

		this.increments = this.end.cpy(); 
		this.increments.sub(this.start);
		this.increments.scl(1f/(this.steps * this.lengthPerStep));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		//		Gdx.app.log("ChalkLine", "draw: getColor().a="+getColor().a);

		if( this.steps > 0 ) {
			//add chalk points
			for(int i = 0; i < this.lengthPerStep; i += this.stepSize) {
				this.chalkLine.add(chalks[this.randomNumberGenerator.nextInt(5)]);
			}
			this.steps--;
		} else if( this.steps == 0 ) {
			if( this.listener != null ) {
				this.listener.onLineDrawEnd(this);
			}
			this.steps--;
		}

		int lineSize = this.chalkLine.size();

		float x = this.start.x;
		float y = this.start.y;
		for(int i = 0; i < lineSize; i++) {
			Sprite sprite = this.chalkLine.get(i);
			sprite.setPosition(x, y);
			sprite.draw(batch, getColor().a);
			x += this.increments.x;
			y += this.increments.y;
		}
	}
}
