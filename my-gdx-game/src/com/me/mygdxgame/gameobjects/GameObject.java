package com.me.mygdxgame.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;


public abstract class GameObject extends Actor {
	
	abstract public void setPosition(float x, float y, boolean transform);
	
	abstract public void setAngle(float angle);
	
	abstract public void draw(SpriteBatch batch);
	
	abstract public void applyForce(Vector2 force);
	
	abstract public Body getBody();
}
