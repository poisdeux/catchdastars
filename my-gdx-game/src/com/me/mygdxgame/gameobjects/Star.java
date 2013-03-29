package com.me.mygdxgame.gameobjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Textures;

/**
 * @author martijn brekhof
 * 
 */

public class Star extends GameObject {
	private float rotationSpeed;
	private Body star;
	private Sprite sprite;
	
	public static enum Type {
		BLUE, 
		RED,
		YELLOW
	}

	public Type type;
	
	public Star(World world, float x, float y, Type type) {
		this.type = type;
		
		if( type == Type.BLUE ) {
			this.sprite = new Sprite(Textures.starBlue);
		} else if ( type == Type.RED ) {
			this.sprite = new Sprite(Textures.starRed);
		} else if ( type == Type.YELLOW ) {
			this.sprite = new Sprite(Textures.starYellow);
		}
		
		float halfWidth = this.sprite.getWidth()/2f;
		float halfHeight = this.sprite.getHeight()/2f;
		CircleShape circle = new CircleShape();
		circle.setRadius(halfWidth * 0.7f);
		
		BodyDef bd = new BodyDef();  
		bd.position.set(new Vector2(x + halfWidth, y + halfHeight));
		this.star = world.createBody(bd);  
		this.star.createFixture(circle, 0.0f);
		circle.dispose();
		
		this.rotationSpeed = 0.1f;
		
		setPosition(x, y, false);
	}


	@Override
	public void setPosition(float x, float y, boolean transform) {
		this.sprite.setPosition(x, y);
	}


	@Override
	public void setAngle(float angle) {
	}


	@Override
	public void draw(SpriteBatch batch) {
		this.sprite.rotate(this.rotationSpeed);
		this.sprite.draw(batch);
	}


	@Override
	public void applyForce(Vector2 force) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Body getBody() {
		return this.star;
	}
}
