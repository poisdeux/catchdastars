package com.strategames.catchdastars.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;


public class SplashImage extends GameObject {

	Texture texture;
	
	public SplashImage() {
		texture = new Texture( "images/splashscreen.png" );
	}

	public void draw (SpriteBatch batch, float parentAlpha) {
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		batch.draw(texture, getX(), getY());
	}

	@Override
	void writeValues(Json json) {
		
	}

	@Override
	void readValue(String key, Object value) {
		
	}

	@Override
	void setupImage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void setupBox2D() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GameObject createCopy() {
		// TODO Auto-generated method stub
		return null;
	}
}
