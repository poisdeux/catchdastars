package com.me.mygdxgame.gameobjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public abstract class GameObject {
	public float x;
	public float y;
	public float angle;
	private Sprite sprite;
	
	public GameObject(float x, float y) {
		this.x = x;
		this.y = y;
		sprite = new Sprite();
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public Sprite getSprite() {
		return this.sprite;
	}
	
	public void setPosition(float x, float y, boolean transform) {
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
	
	abstract public void applyForce(Vector2 force);
	
}
