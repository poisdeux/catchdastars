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
	private Sprite spriteMiddlePart;
	private Sprite spriteLeftPart;
	private Sprite spriteRightPart;
	private Vector2 startPosition;
	private Vector2 endPosition;
	private Type type;

	public enum Type {
		HORIZONTAL, VERTICAL
	}

	public Wall(World world, float x, float y, float length, Type type) {
		this.type = type;

		PolygonShape box = new PolygonShape();  

		if( type == Type.HORIZONTAL ) {
			this.spriteMiddlePart = new Sprite(Textures.bricksHorizontal);
			this.spriteLeftPart = new Sprite(Textures.bricksHorizontalEndLeft);
			this.spriteRightPart = new Sprite(Textures.bricksHorizontalEndRight);
			this.startPosition = new Vector2(x - (length/2f), y - (this.spriteMiddlePart.getHeight() / 2f));
			this.endPosition = new Vector2(x + (length/2f), y - (this.spriteMiddlePart.getHeight() / 2f));
			
			box.setAsBox(length/2f, spriteMiddlePart.getHeight()/2f);
		} else {
			this.spriteMiddlePart = new Sprite(Textures.bricksVertical);
			this.startPosition = new Vector2(x - (this.spriteMiddlePart.getWidth() / 2f), y - (length/2f));
			this.endPosition = new Vector2(x - (this.spriteMiddlePart.getWidth() / 2f), y + (length/2f));

			box.setAsBox(spriteMiddlePart.getWidth()/2f, length/2f);
		}

		BodyDef groundBodyDef =new BodyDef();  
		groundBodyDef.position.set(x, y); // Set its world position
		Body body = world.createBody(groundBodyDef);
		body.createFixture(box, 0.0f); //Attach the box we created horizontally or vertically to the body
		box.dispose();
	}

	@Override
	public void draw(SpriteBatch batch) {
		if ( type == Type.HORIZONTAL ) {
			
			this.spriteLeftPart.setPosition(this.startPosition.x, this.startPosition.y);
			this.spriteLeftPart.draw(batch);
			
			float stepSize = this.spriteMiddlePart.getWidth();
			float middlePartEndPosition = this.endPosition.x - this.spriteRightPart.getWidth();
			
			for(float xd = this.startPosition.x + this.spriteLeftPart.getWidth(); 
					xd < middlePartEndPosition; 
					xd += stepSize ) {
				this.spriteMiddlePart.setPosition(xd, this.startPosition.y);
				this.spriteMiddlePart.draw(batch);
			}
			
			this.spriteRightPart.setPosition(middlePartEndPosition, this.startPosition.y);
			this.spriteRightPart.draw(batch);
		} else {
			for(float yd = this.startPosition.y; yd < this.endPosition.y; yd += this.spriteMiddlePart.getHeight() ) {
				this.spriteMiddlePart.setPosition(this.startPosition.x, yd);
				this.spriteMiddlePart.draw(batch);
			}
		}
	}

	@Override
	public void applyForce(Vector2 force) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPosition(float x, float y, boolean transform) {
	}

	@Override
	public void setAngle(float angle) {
	}
	
	@Override
	public Body getBody() {
		return null;
	}
}
