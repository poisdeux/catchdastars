package com.me.mygdxgame.gameobjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.me.mygdxgame.Textures;

public class Wall extends GameObject {

	public enum Type {
		HORIZONTAL, VERTICAL
	}
	
	public Wall(World world, float x, float y, float length, Type type) {
		super(x, y);
		
		Sprite sprite;
		
		if( type == Type.HORIZONTAL ) {
			sprite = new Sprite(Textures.bricksHorizontal);
		} else {
			sprite = new Sprite(Textures.bricksVertical);
		}
		
		super.setSprite(sprite);
		
		//super.sprite.setSize(length, super.sprite.getHeight());
		
		float cx = x + (sprite.getHeight() / 2f);
		float cy = y + (sprite.getWidth() / 2f);
		
		// Create our body definition
		BodyDef groundBodyDef =new BodyDef();  
		// Set its world position
		groundBodyDef.position.set(cx, cy);

		// Create a body from the defintion and add it to the world
		Body body = world.createBody(groundBodyDef);  

		// Create a polygon shape
		PolygonShape box = new PolygonShape();  
		// Set the polygon shape as a box which is twice the size of our view port and 20 high
		// (setAsBox takes half-width and half-height as arguments)
		box.setAsBox(sprite.getWidth()/2f, sprite.getHeight()/2f);
		// Create a fixture from our polygon shape and add it to our ground body  
		body.createFixture(box, 0.0f); 
		// Clean up after ourselves
		box.dispose();
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		//batch.draw(this.sprite.getTexture(), super.x, super.y);
	}

	@Override
	public void applyForce(Vector2 force) {
		// TODO Auto-generated method stub
		
	}
}
