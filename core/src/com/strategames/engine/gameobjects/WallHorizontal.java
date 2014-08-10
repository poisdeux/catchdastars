package com.strategames.engine.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.strategames.engine.utils.Textures;

public class WallHorizontal extends Wall {
	private float startHorizontalMiddlePart;
	private float startHorizontalEndPart;
	private float endHorizontalMiddlePart;
	private int amountOfParts;

	private boolean drawSingleBrick;

	private static TextureRegion textureRegion;

	private static Textures textures = Textures.getInstance();

	private Color color = getColor();
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		setupParts();
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		setupParts();
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		setupParts();
	}

	@Override
	public void setLength(float length) {
		if( length < WIDTH ) {
			length = WIDTH; //Make sure length is not smaller than a single block
		}
		super.setLength(length);

		setHeight(HEIGHT);
		setWidth(getLength());
		this.amountOfParts = ((int) (length / WIDTH)) - 2;
		if( this.amountOfParts < 2 ) {
			this.drawSingleBrick = true;
		} else {
			this.drawSingleBrick = false;
		}
		setupParts();
	}

	@Override
	protected TextureRegion createTextureRegion() {
		if( textureRegion == null ) {
			textures.bricksHorizontal.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			textureRegion = new TextureRegion(Textures.getInstance().bricksHorizontal);
		}
		return textureRegion;
	}

	@Override
	protected Body setupBox2D() {
		return super.setupBox2D();
	}

	@Override
	protected GameObject newInstance() {
		Wall wall = new WallHorizontal();
		wall.setPartSize(WIDTH);
		return wall;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(color);
		float x = getX();
		float y = getY();
		if( this.drawSingleBrick ) {
			batch.draw(textures.bricksVertical, x, y, WIDTH, HEIGHT);
		} else {
			batch.draw(textures.bricksHorizontalEndLeft, x, y, WIDTH, HEIGHT);
			batch.draw(textures.bricksHorizontal, this.startHorizontalMiddlePart, y, this.endHorizontalMiddlePart, HEIGHT, 0, 0, this.amountOfParts, -1);			
			batch.draw(textures.bricksHorizontalEndRight, this.startHorizontalEndPart, y, WIDTH, HEIGHT);
		}

		//		drawBoundingBox(batch);
	}
	
	private void setupParts() {
		this.startHorizontalMiddlePart = getX() + WIDTH;
		this.endHorizontalMiddlePart = getLength() - (2 * WIDTH);
		this.startHorizontalEndPart = this.startHorizontalMiddlePart + this.endHorizontalMiddlePart;
	}
}
