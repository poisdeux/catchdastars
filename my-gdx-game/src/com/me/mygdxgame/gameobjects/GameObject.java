package com.me.mygdxgame.gameobjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public abstract class GameObject {
	public float x;
	public float y;
	public float angle;
	public Sprite sprite;
	
	public GameObject(Sprite sprite, float x, float y) {
		this.x = x;
		this.y = y;
		
		if( sprite == null ) {
			sprite = new Sprite();
		}
		setSprite(sprite);
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
		this.sprite.setOrigin(this.sprite.getWidth()/2, this.sprite.getHeight()/2);
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public void draw(SpriteBatch batch) {
		this.sprite.setPosition(this.x, this.y);
		this.sprite.setRotation(this.angle);
		this.sprite.draw(batch);
	}
	
}
