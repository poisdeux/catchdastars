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
	private Sprite sprite;
	private Vector2 begin;
	private Vector2 end;
	private Type type;

	public enum Type {
		HORIZONTAL, VERTICAL
	}

	public Wall(World world, float x, float y, float length, Type type) {
		this.type = type;

		PolygonShape box = new PolygonShape();  

		if( type == Type.HORIZONTAL ) {
			sprite = new Sprite(Textures.bricksHorizontal);
			this.begin = new Vector2(x - (length/2f), y - (this.sprite.getHeight() / 2f));
			this.end = new Vector2(x + (length/2f), y - (this.sprite.getHeight() / 2f));

			box.setAsBox(length/2f, sprite.getHeight()/2f);
		} else {
			sprite = new Sprite(Textures.bricksVertical);
			this.begin = new Vector2(x - (this.sprite.getWidth() / 2f), y - (length/2f));
			this.end = new Vector2(x - (this.sprite.getWidth() / 2f), y + (length/2f));

			box.setAsBox(sprite.getWidth()/2f, length/2f);
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
			for(float xd = this.begin.x; xd < this.end.x; xd += this.sprite.getWidth() ) {
				this.sprite.setPosition(xd, this.begin.y);
				this.sprite.draw(batch);
			}
		} else {
			for(float yd = this.begin.y; yd < this.end.y; yd += this.sprite.getHeight() ) {
				this.sprite.setPosition(this.begin.x, yd);
				this.sprite.draw(batch);
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
}
