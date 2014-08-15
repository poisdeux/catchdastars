package com.strategames.engine.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.strategames.engine.utils.Textures;

public class WallVertical extends Wall {
	private Textures textures = Textures.getInstance();
	
	private static TextureRegion textureRegion;
	
	private int amountOfParts;
	
	public WallVertical() {
		super();
		setPartSize(HEIGHT);
	}
	
	@Override
	public void setLength(float length) {
		if( length < HEIGHT ) {
			super.setLength(HEIGHT); //Make sure length is not smaller than a single block
		} else {
			super.setLength(length);
		}
		
		setHeight(getLength());
		setWidth(WIDTH);
		this.amountOfParts = (int) (length / HEIGHT);
	}
	
	@Override
	protected TextureRegion createTextureRegion() {
		if( textureRegion == null ) {
			this.textures.bricksVertical.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
			textureRegion = new TextureRegion(this.textures.bricksVertical);
		}
		return textureRegion;
	}
	
	@Override
	protected Body setupBox2D() {
		return super.setupBox2D();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		Color color = getColor();
		batch.setColor(color);
		batch.draw(this.textures.bricksVertical, getX(), getY(), WIDTH, getLength(), 0, 0, -1, this.amountOfParts);
	}

	@Override
	protected GameObject newInstance() {
		Wall wall = new WallVertical();
		wall.setPartSize(HEIGHT);
		return wall;
	}
}
