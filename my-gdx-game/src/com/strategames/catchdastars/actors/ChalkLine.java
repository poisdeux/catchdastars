package com.strategames.catchdastars.actors;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.strategames.catchdastars.utils.Textures;

public class ChalkLine extends Image {
	private Sprite[] chalks;
	private ArrayList<Sprite> chalkLine; 
	private float length;
	private int steps;
	private int stepSize = 8;
	private int duration;
	private Random randomNumberGenerator;
	private float x;
	private float y;
	
	public ChalkLine() {
		super();
		
		this.chalks = new Sprite[5];
		this.chalks[0] = new Sprite(Textures.chalk1);
		this.chalks[1] = new Sprite(Textures.chalk2);
		this.chalks[2] = new Sprite(Textures.chalk3);
		this.chalks[3] = new Sprite(Textures.chalk4);
		this.chalks[4] = new Sprite(Textures.chalk5);
		
		this.randomNumberGenerator = new Random();
		
		this.chalkLine = new ArrayList<Sprite>();
		
		setScaling(Scaling.none);
		
		setLength(this.length);
	}


	public static ChalkLine create(float x, float y, float length) {
		ChalkLine line = new ChalkLine();
		line.setStart(x, y);
		line.setLength(length);
		return line;
	}

	/**
	 * Sets the start position of this line
	 * @param x
	 * @param y
	 */
	public void setStart(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets the length of the line.
	 * @param length
	 */
	public void setLength(float length) {
		this.length = length;
		this.steps = (int) (this.length / this.stepSize);
	}

	public void setDuration(int milliseconds) {
		this.duration = milliseconds;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if( this.steps > 0 ) {
			//add chalk point
			this.chalkLine.add(this.chalks[this.randomNumberGenerator.nextInt(5)]);
			this.steps--;
		}
		
		int maxXPos = this.chalkLine.size() * this.stepSize;
		
		int index = 0;
		for(int xOffset = 0; xOffset < maxXPos; xOffset += this.stepSize) {
			Sprite sprite = this.chalkLine.get(index++);
			sprite.setPosition(this.x + xOffset, this.y);
		}
	}
}
