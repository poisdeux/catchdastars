package com.me.mygdxgame.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.me.mygdxgame.Textures;


public class SplashImage extends GameObject {

	TextureRegion region;
	
	public SplashImage() {
		region = Textures.blueBalloon;
	}

	public void draw (SpriteBatch batch, float parentAlpha) {
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
				getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}

	@Override
	public void setPosition(float x, float y, boolean transform) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAngle(float angle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyForce(Vector2 force) {
		// TODO Auto-generated method stub

	}

	@Override
	public Body getBody() {
		// TODO Auto-generated method stub
		return null;
	}

}
